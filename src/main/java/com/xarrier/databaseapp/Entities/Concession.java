package com.xarrier.databaseapp.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "concessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder



public class Concession {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Builder.Default
    @Column(name = "habit_freezes_left", nullable = false)
    private Integer habitFreezesLeft = 2;

    @Builder.Default
    @Column(name = "daily_freezes_left", nullable = false)
    private Integer dailyFreezesLeft = 1;

    @Column(name = "reset_at", nullable = false)
    private LocalDate resetAt;

    @PrePersist
    protected void onCreate() {
        // Reset concessions at start of next month (example rule)
        this.resetAt = LocalDate.now().plusMonths(1).withDayOfMonth(1);
    }
}

