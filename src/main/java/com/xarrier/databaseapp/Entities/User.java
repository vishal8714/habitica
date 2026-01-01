package com.xarrier.databaseapp.Entities;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "total_score", nullable = false)
    @Builder.Default
    private Integer totalScore = 0;

    @Column(name = "daily_streak", nullable = false)
    @Builder.Default
    private Integer dailyStreak = 0;

    @Column(name = "last_perfect_day")
    private LocalDate lastPerfectDay;


    @Column(name = "max_daily_streak", nullable = false)
    @Builder.Default
    private Integer maxDailyStreak = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /* --------------------
       Lifecycle hooks
       -------------------- */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.email = this.email.toLowerCase();
    }
}
