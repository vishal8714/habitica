package com.xarrier.databaseapp.Entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leaderboards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Leaderboard {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "daily_streak", nullable = false)
    private Integer dailyStreak;

    @Column(name = "max_habit_streak", nullable = false)
    private Integer maxHabitStreak;

    @Column(name = "total_score", nullable = false)
    private Integer totalScore;
}
