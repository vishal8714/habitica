package com.xarrier.databaseapp.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "habit_logs",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"habit_id", "log_date"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* Habit */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;

    /* User */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Column(nullable = false)
    private Boolean planned;

    @Column(nullable = false)
    private Boolean completed;
}
