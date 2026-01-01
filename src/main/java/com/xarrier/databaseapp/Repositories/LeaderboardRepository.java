package com.xarrier.databaseapp.Repositories;

import com.xarrier.databaseapp.Entities.Leaderboard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaderboardRepository extends JpaRepository<Leaderboard, Long> {

    List<Leaderboard> findTop20ByOrderByDailyStreakDesc();

    List<Leaderboard> findTop20ByOrderByMaxHabitStreakDesc();

    List<Leaderboard> findTop20ByOrderByTotalScoreDesc();
}
