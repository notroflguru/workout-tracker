package notroflguru.workouttracker.workout.controller;

import jakarta.validation.Valid;
import notroflguru.workouttracker.workout.dto.response.WorkoutResponse;
import notroflguru.workouttracker.workout.dto.request.CreateWorkoutRequest;
import notroflguru.workouttracker.workout.service.WorkoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workout")
public class WorkoutController {

    private WorkoutService service;

    public WorkoutController(WorkoutService workoutService) {
        this.service = workoutService;
    }

    @PostMapping()
    public ResponseEntity<WorkoutResponse> createWorkout (
        @RequestBody @Valid CreateWorkoutRequest request
    ) {
        WorkoutResponse response = service.createWorkout(request);
        return ResponseEntity.ok(response);
    }
}
