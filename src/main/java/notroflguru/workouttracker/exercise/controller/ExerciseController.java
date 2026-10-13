package notroflguru.workouttracker.exercise.controller;

import jakarta.validation.Valid;
import notroflguru.workouttracker.exercise.dto.CreateExerciseRequest;
import notroflguru.workouttracker.exercise.dto.ExerciseResponse;
import notroflguru.workouttracker.exercise.service.ExerciseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercise")
public class ExerciseController {
    ExerciseService service;

    public ExerciseController(ExerciseService exerciseService) {
        this.service = exerciseService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<ExerciseResponse>> findAll() {
        List<ExerciseResponse> responseList = service.findAll();
        return ResponseEntity.ok(responseList);
    }

    @PostMapping
    public ResponseEntity<ExerciseResponse> createExercise(
            @RequestBody @Valid CreateExerciseRequest request
            ) {
        ExerciseResponse response = service.createExercise(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{name}")
    public ResponseEntity<ExerciseResponse> findExercise(
            @PathVariable("name") String name
    ) {
        ExerciseResponse response = service.findExercise(name);
        return ResponseEntity.ok(response);
    }
}
