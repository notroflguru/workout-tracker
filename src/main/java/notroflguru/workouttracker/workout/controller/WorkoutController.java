package notroflguru.workouttracker.workout.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(summary = "Создать тренировку", responses = {
            @ApiResponse(responseCode = "201", description = "Тренировка успешно создана",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(
                    name ="Тренировка успешно создана",
                    value = """
                            {
                              "id": 9,
                              "date": "2026-10-22",
                              "notes": "Четвертая",
                              "workoutExerciseResponseList": [
                                  {
                                    "id": 9,
                                    "exerciseId": 2,
                                    "exerciseName": "Squat",
                                    "orderNumber": 1,
                                    "exerciseSetResponseList": [
                                      {
                                        "id": 10,
                                        "weight": 70.0,
                                        "reps": 8,
                                        "rir": "2-3",
                                        "orderNumber": 1
                                      },
                                      {
                                        "id": 11,
                                        "weight": 75.0,
                                        "reps": 5,
                                        "rir": "1-2",
                                        "orderNumber": 2
                                      }
                                    ]
                                  }
                                ]
                            }
                            """
            ))),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные",
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "Validation Error",
                            summary = "Ошибка валидации полей",
                            value = """
                                    {
                                      "code": "VALIDATION_FAILED",
                                      "errors": {
                                          "notes": "Длина должна быть не более 255 символов"
                                      },
                                      "message": "Request validation failed",
                                      "time": "2026-01-01 00:01:02"
                                    }
                                    """
                    )))
    })
    public ResponseEntity<WorkoutResponse> createWorkout (
        @RequestBody @Valid CreateWorkoutRequest request
    ) {
        WorkoutResponse response = service.createWorkout(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
