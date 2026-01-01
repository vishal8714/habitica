package com.xarrier.databaseapp.Repositories;

import com.xarrier.databaseapp.Entities.Habit;
import com.xarrier.databaseapp.Entities.HabitLog;
import com.xarrier.databaseapp.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {

    Optional<HabitLog> findByHabitAndLogDate(Habit habit, LocalDate logDate);

    List<HabitLog> findByUserAndLogDate(User user, LocalDate logDate);

    List<HabitLog> findByHabit(Habit habit);

    Optional<HabitLog> findTopByHabitOrderByLogDateDesc(Habit habit);

    void deleteByHabitAndLogDate(Habit habit, LocalDate logDate);

}
