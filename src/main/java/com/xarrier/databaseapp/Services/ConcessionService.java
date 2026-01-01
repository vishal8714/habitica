package com.xarrier.databaseapp.Services;

import com.xarrier.databaseapp.Entities.Concession;
import com.xarrier.databaseapp.Entities.User;
import com.xarrier.databaseapp.Repositories.ConcessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ConcessionService {

    private final ConcessionRepository concessionRepository;

    private static final int DEFAULT_HABIT_FREEZES = 2;
    private static final int DEFAULT_DAILY_FREEZES = 1;

    /* --------------------
       Ensure monthly reset
       -------------------- */
    public void ensureMonthlyReset(User user) {

        Concession c = concessionRepository.findById(user.getId())
                .orElseThrow();

        LocalDate today = LocalDate.now();

        if (c.getResetAt().isBefore(today)) {

            c.setHabitFreezesLeft(DEFAULT_HABIT_FREEZES);
            c.setDailyFreezesLeft(DEFAULT_DAILY_FREEZES);

            // set next reset = first day of next month
            c.setResetAt(
                    today.plusMonths(1).withDayOfMonth(1)
            );

            concessionRepository.save(c);
        }
    }

    /* --------------------
       Consume habit freeze
       -------------------- */
    public boolean consumeHabitFreeze(User user) {

        ensureMonthlyReset(user);

        Concession c = concessionRepository.findById(user.getId())
                .orElseThrow();

        if (c.getHabitFreezesLeft() > 0) {
            c.setHabitFreezesLeft(c.getHabitFreezesLeft() - 1);
            concessionRepository.save(c);
            return true;
        }
        return false;
    }

    /* --------------------
       Consume daily freeze
       -------------------- */
    public boolean consumeDailyFreeze(User user) {

        ensureMonthlyReset(user);

        Concession c = concessionRepository.findById(user.getId())
                .orElseThrow();

        if (c.getDailyFreezesLeft() > 0) {
            c.setDailyFreezesLeft(c.getDailyFreezesLeft() - 1);
            concessionRepository.save(c);
            return true;
        }
        return false;
    }
}
