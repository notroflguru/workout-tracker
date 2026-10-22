package notroflguru.workouttracker.exercise.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import notroflguru.workouttracker.exercise.dto.CreateExerciseRequest;
import notroflguru.workouttracker.exercise.dto.ExerciseResponse;
import notroflguru.workouttracker.exercise.service.ExerciseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercise")
@Tag(name = "Упражнения", description = "Управление упражнениями")
public class ExerciseController {
    ExerciseService service;

    public ExerciseController(ExerciseService exerciseService) {
        this.service = exerciseService;
    }

    @GetMapping("/all")
    @Operation(summary = "Получить все упражнения",
            description = "Возвращает полную информацию о всех существующих упражнениях",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Все упражнения успешно получены"),
            }
    )
    public ResponseEntity<List<ExerciseResponse>> findAll() {
        List<ExerciseResponse> responseList = service.findAll();
        return ResponseEntity.ok(responseList);
    }

    @PostMapping
    @Operation(summary = "Создать новое упражнение", responses = {
            @ApiResponse(responseCode = "201", description = "Упражнение успешно создано"),
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
                                  "message": "Exercise with this name already exists",
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

    @GetMapping("/{name}")
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
                              "message": "Exercise not found: deadlift",
                              "time": "2026-01-01 00:01:02"
                            }
                            """
            )))
    })
    public ResponseEntity<ExerciseResponse> findExercise(
            @PathVariable("name") String name
    ) {
        ExerciseResponse response = service.findExercise(name);
        return ResponseEntity.ok(response);
    }
}
