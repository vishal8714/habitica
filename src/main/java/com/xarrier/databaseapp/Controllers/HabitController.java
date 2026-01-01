package com.xarrier.databaseapp.Controllers;

import com.xarrier.databaseapp.DTOs.Habit.CreateHabitRequest;
import com.xarrier.databaseapp.DTOs.Habit.HabitResponse;
import com.xarrier.databaseapp.Entities.Habit;
import com.xarrier.databaseapp.Entities.User;
import com.xarrier.databaseapp.Services.HabitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/habits")
@RequiredArgsConstructor
public class HabitController {

    private final HabitService habitService;

    /**
     * TEMPORARY: userId passed explicitly.
     * Later JWT will replace this.
     */
    @PostMapping("/create/{userId}")
    public ResponseEntity<Habit> createHabit(
            @PathVariable Long userId,
            @Valid @RequestBody CreateHabitRequest request
    ) {
        return ResponseEntity.ok(habitService.createHabit(userId, request));
    }


    @GetMapping("/{userId}")
    public ResponseEntity<List<HabitResponse>> getAllHabits(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(habitService.getAllHabits(userId));
    }

    @GetMapping("/{userId}/today")
    public ResponseEntity<List<HabitResponse>> getTodaysHabits(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(habitService.getTodaysHabits(userId));
    }

    @PostMapping("/{habitId}/complete/{userId}")
    public ResponseEntity<Void> completeHabit(
            @PathVariable Long habitId,
            @PathVariable Long userId
    ) {
        habitService.completeHabit(userId, habitId);
        return ResponseEntity.ok().build();
    }

//    @DeleteMapping("/{habitId}/complete")
//    public ResponseEntity<Void> undoHabitCompletion(
//            @PathVariable Long habitId,
//            @RequestParam Long userId
//    ) {
//        habitService.undoHabitCompletion(userId, habitId);
//        return ResponseEntity.noContent().build();
//    }

    @DeleteMapping("/{habitId}/complete")
    public ResponseEntity<Void> undoHabitCompletion(
            @PathVariable Long habitId,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        habitService.undoHabitCompletion(user.getId(), habitId);
        return ResponseEntity.noContent().build();
    }


}
