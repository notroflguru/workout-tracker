package notroflguru.workouttracker.exercise.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import notroflguru.workouttracker.exercise.dto.request.CreateExerciseRequest;
import notroflguru.workouttracker.exercise.dto.response.ExerciseResponse;
import notroflguru.workouttracker.exercise.dto.response.ShortExerciseResponse;
import notroflguru.workouttracker.exercise.service.ExerciseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
@Tag(name = "Упражнения", description = "Управление упражнениями")
public class ExerciseController {
    private final ExerciseService service;

    public ExerciseController(ExerciseService exerciseService) {
        this.service = exerciseService;
    }

    @GetMapping()
    @Operation(summary = "Получить все упражнения",
            description = "Возвращает полную информацию о всех существующих упражнениях",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Все упражнения успешно получены",
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(
                            value = """
                                    [
                                      {
                                        "id": 1,
                                        "name": "Deadlift",
                                        "description": "Классическая становая тяга",
                                        "muscleList": [
                                          "Hamstrings",
                                          "Glutes"
                                        ]
                                      },
                                      {
                                        "id": 2,
                                        "name": "Squat",
                                        "description": "Приседания со штангой на плечах",
                                        "muscleList": [
                                          "Quadriceps",
                                          "Hamstrings",
                                          "Glutes"
                                        ]
                                      },
                                      {
                                        "id": 3,
                                        "name": "Bench press",
                                        "description": "Классический жим штанги лежа на горизонтальной скамье",
                                        "muscleList": [
                                          "Chest",
                                          "Triceps",
                                          "Front_delts"
                                        ]
                                      }
                                    ]
                                    """
                    ))),
            }
    )
    public ResponseEntity<List<ShortExerciseResponse>> findAll() {
        List<ShortExerciseResponse> responseList = service.findAll();
        return ResponseEntity.ok(responseList);
    }

    @PostMapping
    @Operation(summary = "Создать новое упражнение", responses = {
            @ApiResponse(responseCode = "201", description = "Упражнение успешно создано",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(
                    name = "Упражнение успешно создано",
                    value = """
                            {
                              "id": 3,
                              "name": "Bench press",
                              "description": "Классический жим штанги лежа на горизонтальной скамье",
                              "muscleList": [
                                "Chest",
                                "Triceps",
                                "Front_delts"
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
                                  "message": "Request validation failed",
                                  "errors": {
                                    "name": "Название не может быть пустым"
                                  },
                                  "timestamp": "2026-01-01 00:01:02"
                                }
                                """
                            )
                    )),
            @ApiResponse(responseCode = "409", description = "Упражнение уже существует",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Already Exists Error",
                                    summary = "Упражнение уже есть в базе",
                                    value = """
                                {
                                  "code": "EXERCISE_ALREADY_EXISTS",
                                  "message": "Упражнение с таким названием уже существует",
                                  "timestamp": "2026-01-01 00:01:02"
                                }
                                """
                            )
                    ))
    })
    public ResponseEntity<ExerciseResponse> createExercise(
            @RequestBody @Valid CreateExerciseRequest request
            ) {
        ExerciseResponse response = service.createExercise(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Получить упражнение по имени", description = "Возвращает полную информацию о упражнении", responses = {
            @ApiResponse(responseCode = "200", description = "Упражнение успешно найдено"),
            @ApiResponse(responseCode = "404", description = "Упражнение не найдено",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(
                    name = "Not Found Error",
                    summary = "Упражнение не найдено",
                    value = """
                            {
                              "code": "EXERCISE_NOT_FOUND",
                              "message": "Упражнение не найдено",
                              "timestamp": "2026-01-01 00:01:02"
                            }
                            """
            )))
    })
    public ResponseEntity<ExerciseResponse> findExerciseByName(
            @PathVariable("name") String name
    ) {
        ExerciseResponse response = service.findByName(name);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить упражнение по идентификатору", description = "Возвращает полную информацию о упражнении", responses = {
            @ApiResponse(responseCode = "200", description = "Упражнение успешно найдено"),
            @ApiResponse(responseCode = "404", description = "Упражнение не найдено",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Not Found Error",
                                    summary = "Упражнение не найдено",
                                    value = """
                            {
                              "code": "EXERCISE_NOT_FOUND",
                              "message": "Упражнение не найдено",
                              "timestamp": "2026-01-01 00:01:02"
                            }
                            """
                            )))
    })
    public ResponseEntity<ExerciseResponse> findExerciseById(
            @PathVariable("id") Long id
    ) {
        ExerciseResponse response = service.findById(id);
        return ResponseEntity.ok(response);
    }
}
