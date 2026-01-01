package com.xarrier.databaseapp.Repositories;

import com.xarrier.databaseapp.Entities.Habit;
import com.xarrier.databaseapp.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HabitRepository extends JpaRepository<Habit, Long> {

    List<Habit> findByUser(User user);

    List<Habit> findByUserAndIsPausedFalse(User user);

    Optional<Habit> findByIdAndUser(Long id, User user);
}
