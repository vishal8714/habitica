package com.xarrier.databaseapp.DTOs.Habit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateHabitRequest {

    @NotBlank(message = "Habit name is required")
    private String name;

    @NotNull(message = "Visibility is required")
    private String visibility; // PUBLIC / PRIVATE

    @NotEmpty(message = "Planned days cannot be empty")
    private List<Integer> plannedDays;
    // 1 = Monday ... 7 = Sunday
}
