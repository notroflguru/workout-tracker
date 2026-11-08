package notroflguru.workouttracker.workout.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import notroflguru.workouttracker.user.security.SecurityUser;
import notroflguru.workouttracker.workout.dto.request.PatchWorkoutRequest;
import notroflguru.workouttracker.workout.dto.response.ShortWorkoutResponse;
import notroflguru.workouttracker.workout.dto.response.WorkoutResponse;
import notroflguru.workouttracker.workout.dto.request.CreateWorkoutRequest;
import notroflguru.workouttracker.workout.service.WorkoutService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
                                      "timestamp": "2026-01-01 00:01:02"
                                    }
                                    """
                    ))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректный формат ID",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Wrong ID",
                                    summary = "ID имеет неверный формат",
                                    value = """
                                {
                                  "code": "WRONG_ID",
                                  "message": "Could not resolve id",
                                  "timestamp": "2026-01-01 00:01:02"
                                }
                                """
                            )
                    )
            )
    })
    public ResponseEntity<WorkoutResponse> createWorkout (
        @Valid @RequestBody CreateWorkoutRequest request,
        @AuthenticationPrincipal SecurityUser userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        WorkoutResponse response = service.createWorkout(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить тренировку по ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Тренировка успешно найдена",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Тренировка найдена",
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
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Тренировка не найдена",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Workout Not Found",
                                            summary = "Тренировка с указанным ID не существует",
                                            value = """
                                                {
                                                  "code": "WORKOUT_NOT_FOUND",
                                                  "message": "Could not find workout",
                                                  "timestamp": "2026-01-01 00:01:02"
                                                }
                                                """
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<WorkoutResponse> findById (
            @PathVariable("id") Long id,
            @AuthenticationPrincipal SecurityUser userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        WorkoutResponse response = service.findWorkoutById(id, userId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Обновить тренировку по ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Тренировка успешно обновлена",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Тренировка обновлена",
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
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректные входные данные или формат ID",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "Validation Error",
                                                    summary = "Ошибка валидации полей",
                                                    value = """
                                                        {
                                                          "code": "VALIDATION_FAILED",
                                                          "errors": {
                                                            "notes": "Длина должна быть не более 255 символов"
                                                          },
                                                          "message": "Request validation failed",
                                                          "timestamp": "2026-01-01 00:01:02"
                                                        }
                                                        """
                                            ),
                                            @ExampleObject(
                                                    name = "Wrong ID",
                                                    summary = "ID имеет неверный формат",
                                                    value = """
                                                        {
                                                          "code": "WRONG_ID",
                                                          "message": "Could not resolve id",
                                                          "timestamp": "2026-01-01T00:01:02"
                                                        }
                                                        """
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Тренировка не найдена",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Workout Not Found",
                                            summary = "Тренировка с указанным ID не существует",
                                            value = """
                                                {
                                                  "code": "WORKOUT_NOT_FOUND",
                                                  "message": "Workout not found",
                                                  "timestamp": "2026-01-01 00:01:02"
                                                }
                                                """
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<WorkoutResponse> patchWorkout(
            @PathVariable Long id,
            @Valid @RequestBody PatchWorkoutRequest request,
            @AuthenticationPrincipal SecurityUser userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        WorkoutResponse response = service.updateWorkout(id, request, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удаление тренировки по ID",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Тренировка успешно удалена",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Тренировка не найдена",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Workout Not Found",
                                            summary = "Тренировка с указанным ID не существует",
                                            value = """
                                                {
                                                  "code": "WORKOUT_NOT_FOUND",
                                                  "message": "Could not find workout",
                                                  "timestamp": "2026-01-01 00:01:02"
                                                }
                                                """
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<Void> deleteWorkout(
            @PathVariable Long id,
            @AuthenticationPrincipal SecurityUser userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        service.deleteWorkout(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    @Operation(
            summary = "Поиск всех тренировок",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Тренировки успешно найдены",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                        name = "Тренировки найдены",
                                        value = """
                                                [
                                                  {
                                                    "date": "2026-10-18",
                                                    "id": 3
                                                  },
                                                  {
                                                    "date": "2026-10-18",
                                                    "id": 4
                                                  },
                                                  {
                                                    "date": "2026-10-18",
                                                    "id": 5
                                                  },
                                                  {
                                                    "date": "2026-10-19",
                                                    "id": 6
                                                  },
                                                  {
                                                    "date": "2026-10-19",
                                                    "id": 7
                                                  },
                                                  {
                                                    "date": "2026-10-22",
                                                    "id": 9
                                                  },
                                                  {
                                                    "date": "2026-10-22",
                                                    "id": 13
                                                  }
                                                ]
                                                """
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<List<ShortWorkoutResponse>> findAll(
            @AuthenticationPrincipal SecurityUser userDetails
            ) {
        Long userId = userDetails.getUser().getId();

        return ResponseEntity.ok(service.findAll(userId));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Полностью заменить тренировку по ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Тренировка успешно заменена",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Тренировка заменена",
                                            value = """
                                                {
                                                  "id": 9,
                                                  "date": "2026-10-22",
                                                  "notes": "Обновлённая тренировка",
                                                  "workoutExerciseResponseList": [
                                                    {
                                                      "id": 12,
                                                      "exerciseId": 2,
                                                      "exerciseName": "Squat",
                                                      "orderNumber": 1,
                                                      "exerciseSetResponseList": [
                                                        {
                                                          "id": 15,
                                                          "weight": 80.0,
                                                          "reps": 8,
                                                          "rir": "2",
                                                          "orderNumber": 1
                                                        }
                                                      ]
                                                    }
                                                  ]
                                                }
                                                """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректные входные данные или формат ID",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "Validation Error",
                                                    summary = "Ошибка валидации полей",
                                                    value = """
                                                        {
                                                          "code": "VALIDATION_FAILED",
                                                          "errors": {
                                                            "notes": "Длина должна быть не более 255 символов"
                                                          },
                                                          "message": "Request validation failed",
                                                          "timestamp": "2026-01-01 00:01:02"
                                                        }
                                                        """
                                            ),
                                            @ExampleObject(
                                                    name = "Wrong ID",
                                                    summary = "ID имеет неверный формат",
                                                    value = """
                                                        {
                                                          "code": "WRONG_ID",
                                                          "message": "Could not resolve id",
                                                          "timestamp": "2026-01-01 00:01:02"
                                                        }
                                                        """
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Тренировка не найдена",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Workout Not Found",
                                            summary = "Тренировка с указанным ID не существует",
                                            value = """
                                                {
                                                  "code": "WORKOUT_NOT_FOUND",
                                                  "message": "Could not find workout",
                                                  "timestamp": "2026-01-01 00:01:02"
                                                }
                                                """
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<WorkoutResponse> replaceWorkout(
            @PathVariable Long id,
            @Valid @RequestBody CreateWorkoutRequest request,
            @AuthenticationPrincipal SecurityUser userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        WorkoutResponse response = service.replaceWorkout(id, request, userId);

        return ResponseEntity.ok(response);
    }
}
