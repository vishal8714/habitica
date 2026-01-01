package com.xarrier.databaseapp.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "habits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* Owner */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility visibility;

    /**
     * Planned days of week
     * Example: [1,3,5] → Mon, Wed, Fri
     */
    @Column(name = "planned_days", columnDefinition = "JSON", nullable = false)
    private String plannedDays;

    @Builder.Default
    @Column(name = "current_streak", nullable = false)
    private Integer currentStreak = 0;

    @Builder.Default
    @Column(name = "max_streak", nullable = false)
    private Integer maxStreak = 0;

    @Builder.Default
    @Column(name = "is_paused", nullable = false)
    private Boolean isPaused = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public enum Visibility {
        PUBLIC,
        PRIVATE
    }
}
