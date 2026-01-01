package com.xarrier.databaseapp.DTOs.Habit;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class HabitResponse {

    private Long id;
    private String name;
    private String visibility;
    private List<Integer> plannedDays;
    private Integer currentStreak;
    private Integer maxStreak;
    private Boolean isPaused;
    private LocalDateTime createdAt;
}
