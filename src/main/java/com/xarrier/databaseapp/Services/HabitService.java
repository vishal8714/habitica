package com.xarrier.databaseapp.Services;

import com.xarrier.databaseapp.DTOs.Habit.CreateHabitRequest;
import com.xarrier.databaseapp.DTOs.Habit.HabitResponse;
import com.xarrier.databaseapp.Entities.Habit;
import com.xarrier.databaseapp.Entities.HabitLog;
import com.xarrier.databaseapp.Entities.User;
import com.xarrier.databaseapp.Exceptions.UsernameAlreadyExistsException;
import com.xarrier.databaseapp.Repositories.HabitLogRepository;
import com.xarrier.databaseapp.Repositories.HabitRepository;
import com.xarrier.databaseapp.Repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HabitService {

    private final HabitRepository habitRepository;
    private final UserRepository userRepository;
    private final HabitLogRepository habitLogRepository;
    private final ConcessionService concessionService;

    /* --------------------
       CREATE HABIT
       -------------------- */
    public Habit createHabit(Long userId, CreateHabitRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameAlreadyExistsException());

        Habit habit = Habit.builder()
                .user(user)
                .name(request.getName())
                .visibility(Habit.Visibility.valueOf(request.getVisibility()))
                .plannedDays(request.getPlannedDays().toString())
                .build();

        int today = LocalDate.now().getDayOfWeek().getValue();

        if (parsePlannedDays(habit.getPlannedDays()).contains(today)) {

            // Invalidate today's perfect day
            if (user.getLastPerfectDay() != null &&
                    user.getLastPerfectDay().isEqual(LocalDate.now())) {

                user.setLastPerfectDay(null);
                user.setDailyStreak(0);
                userRepository.save(user);
            }
        }


        return habitRepository.save(habit);
    }

    /* --------------------
       GET ALL HABITS
       -------------------- */
    public List<HabitResponse> getAllHabits(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return habitRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /* --------------------
       GET TODAY'S HABITS
       -------------------- */
    public List<HabitResponse> getTodaysHabits(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int today = LocalDate.now().getDayOfWeek().getValue(); // 1–7

        return habitRepository.findByUserAndIsPausedFalse(user)
                .stream()
                .filter(habit ->
                        parsePlannedDays(habit.getPlannedDays()).contains(today)
                )
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public void undoHabitCompletion(Long userId, Long habitId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Habit habit = habitRepository.findByIdAndUser(habitId, user)
                .orElseThrow(() -> new RuntimeException("Habit not found"));

        LocalDate today = LocalDate.now();

        HabitLog log = habitLogRepository
                .findByHabitAndLogDate(habit, today)
                .orElseThrow(() -> new RuntimeException("Habit not completed today"));

    /* --------------------
       Delete today's log
       -------------------- */
        habitLogRepository.delete(log);

    /* --------------------
       Recalculate habit streak
       -------------------- */
        HabitLog previousLog = habitLogRepository
                .findTopByHabitOrderByLogDateDesc(habit)
                .orElse(null);

        if (previousLog == null) {
            habit.setCurrentStreak(0);
        } else {
            habit.setCurrentStreak(recalculateHabitStreak(habit, previousLog.getLogDate()));
        }

        habitRepository.save(habit);

    /* --------------------
       Recalculate daily streak
       -------------------- */
        boolean perfectToday = isPerfectDay(user, today);

        if (!perfectToday && today.equals(user.getLastPerfectDay())) {
            user.setDailyStreak(0);
            user.setLastPerfectDay(null);
        }

    /* --------------------
       Update user score
       -------------------- */
        user.setTotalScore(Math.max(0, user.getTotalScore() - 1));
        userRepository.save(user);
    }


    private int recalculateHabitStreak(Habit habit, LocalDate lastCompletedDate) {

        List<Integer> plannedDays = parsePlannedDays(habit.getPlannedDays());
        int streak = 1;

        LocalDate date = lastCompletedDate.minusDays(1);

        while (true) {

            int dayOfWeek = date.getDayOfWeek().getValue();

            if (plannedDays.contains(dayOfWeek)) {

                boolean completed = habitLogRepository
                        .findByHabitAndLogDate(habit, date)
                        .isPresent();

                if (!completed) {
                    break;
                }
                streak++;
            }

            date = date.minusDays(1);
        }

        return streak;
    }



    private boolean isPerfectDay(User user, LocalDate date) {

        int dayOfWeek = date.getDayOfWeek().getValue();

        List<Habit> plannedHabits = habitRepository
                .findByUserAndIsPausedFalse(user)
                .stream()
                .filter(habit ->
                        parsePlannedDays(habit.getPlannedDays()).contains(dayOfWeek)
                )
                .toList();

        // No planned habits = NOT a perfect day
        if (plannedHabits.isEmpty()) {
            return false;
        }

        for (Habit habit : plannedHabits) {
            boolean completed = habitLogRepository
                    .findByHabitAndLogDate(habit, date)
                    .isPresent();

            if (!completed) {
                return false;
            }
        }

        return true;
    }



    /* --------------------
       MAPPERS
       -------------------- */
    private HabitResponse mapToResponse(Habit habit) {
        return HabitResponse.builder()
                .id(habit.getId())
                .name(habit.getName())
                .visibility(habit.getVisibility().name())
                .plannedDays(parsePlannedDays(habit.getPlannedDays()))
                .currentStreak(habit.getCurrentStreak())
                .maxStreak(habit.getMaxStreak())
                .isPaused(habit.getIsPaused())
                .createdAt(habit.getCreatedAt())
                .build();
    }

    private List<Integer> parsePlannedDays(String plannedDays) {
        // "[1,3,5]" → List<Integer>
        return List.of(
                        plannedDays
                                .replace("[", "")
                                .replace("]", "")
                                .split(",")
                ).stream()
                .map(String::trim)
                .map(Integer::valueOf)
                .toList();
    }

    private boolean hasMissedPlannedDays(Habit habit, LocalDate lastCompletedDate) {

        List<Integer> plannedDays = parsePlannedDays(habit.getPlannedDays());

        LocalDate dateToCheck = lastCompletedDate.plusDays(1);
        LocalDate yesterday = LocalDate.now().minusDays(1);

        while (!dateToCheck.isAfter(yesterday)) {

            int dayOfWeek = dateToCheck.getDayOfWeek().getValue();

            // Only care about planned days
            if (plannedDays.contains(dayOfWeek)) {

                boolean completed = habitLogRepository
                        .findByHabitAndLogDate(habit, dateToCheck)
                        .isPresent();

                if (!completed) {
                    return true; // streak break
                }
            }

            dateToCheck = dateToCheck.plusDays(1);
        }

        return false;
    }

    private boolean shouldBreakDailyStreak(User user, LocalDate today) {

        LocalDate yesterday = today.minusDays(1);

        // If yesterday was already a perfect day → streak continues
        if (user.getLastPerfectDay() != null &&
                user.getLastPerfectDay().isEqual(yesterday)) {
            return false;
        }

        int yesterdayDayOfWeek = yesterday.getDayOfWeek().getValue();

        List<Habit> plannedYesterday = habitRepository
                .findByUserAndIsPausedFalse(user)
                .stream()
                .filter(habit ->
                        parsePlannedDays(habit.getPlannedDays())
                                .contains(yesterdayDayOfWeek)
                )
                .toList();

        // No planned habits yesterday → no break
        if (plannedYesterday.isEmpty()) {
            return false;
        }

        // Planned habits existed, but not perfect → break
        return true;
    }


    public void completeHabit(Long userId, Long habitId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        /* --------------------
   DAILY STREAK BREAK CHECK
   -------------------- */
        LocalDate today = LocalDate.now();

        if (shouldBreakDailyStreak(user, today)) {


            boolean freezeUsed = concessionService.consumeDailyFreeze(user);

            if (!freezeUsed) {
                user.setDailyStreak(0);
            }
            userRepository.save(user);
        }


        Habit habit = habitRepository.findByIdAndUser(habitId, user)
                .orElseThrow(() -> new RuntimeException("Habit not found"));

        if (habit.getIsPaused()) {
            throw new RuntimeException("Habit is paused");
        }

        int to_day = LocalDate.now().getDayOfWeek().getValue();
        if (!parsePlannedDays(habit.getPlannedDays()).contains(to_day)) {
            throw new RuntimeException("Habit is not planned for today");
        }

        LocalDate todayDate = LocalDate.now();

        // Prevent double completion
        if (habitLogRepository.findByHabitAndLogDate(habit, todayDate).isPresent()) {
            throw new RuntimeException("Habit already completed today");
        }

    /* --------------------
       STREAK BREAK CHECK
       -------------------- */
        HabitLog lastLog = habitLogRepository
                .findTopByHabitOrderByLogDateDesc(habit)
                .orElse(null);

        if (lastLog != null) {
            boolean missedPlannedDay = hasMissedPlannedDays(habit, lastLog.getLogDate());
            if (missedPlannedDay) {
//
                boolean freezeUsed = concessionService.consumeHabitFreeze(user);

                if (!freezeUsed) {
                    habit.setCurrentStreak(0);
                }
            }
        }

    /* --------------------
       Save habit log
       -------------------- */
        HabitLog log = HabitLog.builder()
                .habit(habit)
                .user(user)
                .logDate(todayDate)
                .planned(true)
                .completed(true)
                .build();

        habitLogRepository.save(log);

    /* --------------------
       Update streaks
       -------------------- */
        habit.setCurrentStreak(habit.getCurrentStreak() + 1);

        if (habit.getCurrentStreak() > habit.getMaxStreak()) {
            habit.setMaxStreak(habit.getCurrentStreak());
        }

        habitRepository.save(habit);

        /* --------------------
   DAILY STREAK CHECK
   -------------------- */
       /* --------------------
   DAILY STREAK (STATE-BASED)
   -------------------- */
        LocalDate todayDateOnly = LocalDate.now();

        boolean perfectToday = isPerfectDay(user, todayDateOnly);

        if (perfectToday) {

            // If yesterday was perfect → continue streak
            if (user.getLastPerfectDay() != null &&
                    user.getLastPerfectDay().isEqual(todayDateOnly.minusDays(1))) {

                user.setDailyStreak(user.getDailyStreak() + 1);

            } else {
                // New streak starts today
                user.setDailyStreak(1);
            }

            if (user.getDailyStreak() > user.getMaxDailyStreak()) {
                user.setMaxDailyStreak(user.getDailyStreak());
            }

            user.setLastPerfectDay(todayDateOnly);
            userRepository.save(user);

        } else {
            // Today is NOT perfect → do nothing now
            // (break happens tomorrow via daily-streak-break logic)
        }



    /* --------------------
       Update user score
       -------------------- */
        user.setTotalScore(user.getTotalScore() + 1);
        userRepository.save(user);
    }



}
