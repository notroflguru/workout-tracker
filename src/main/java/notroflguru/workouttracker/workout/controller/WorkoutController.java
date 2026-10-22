package notroflguru.workouttracker.workout.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import notroflguru.workouttracker.workout.dto.response.WorkoutResponse;
import notroflguru.workouttracker.workout.dto.request.CreateWorkoutRequest;
import notroflguru.workouttracker.workout.service.WorkoutService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workout")
@Tag(name = "Тренировки", description = "Управление тренировками")
public class WorkoutController {

    private final WorkoutService service;

    public WorkoutController(WorkoutService workoutService) {
        this.service = workoutService;
    }

    @PostMapping()
    @Operation(summary = "Создать тренировку")
    public ResponseEntity<WorkoutResponse> createWorkout (
        @RequestBody @Valid CreateWorkoutRequest request
    ) {
        WorkoutResponse response = service.createWorkout(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
