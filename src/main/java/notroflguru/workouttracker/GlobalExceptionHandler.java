package notroflguru.workouttracker;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import notroflguru.workouttracker.dto.ErrorResponse;
import notroflguru.workouttracker.exercise.exception.ExerciseAlreadyExistsException;
import notroflguru.workouttracker.exercise.exception.ExerciseNotFoundException;
import notroflguru.workouttracker.user.exception.UserAlreadyExistsException;
import notroflguru.workouttracker.user.exception.UserNotFoundException;
import notroflguru.workouttracker.workout.exception.WorkoutNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException exception) {

        log.warn("Unknown api path: {}", exception.getMessage());

        ErrorResponse errorResponse = new ErrorResponse("RESOURCE_NOT_FOUND", exception.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {

        log.warn("Validation failed: {}", exception.getMessage());

        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        ErrorResponse errorResponse = new ErrorResponse("VALIDATION_FAILED", "Request validation failed", errors, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ExerciseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleExerciseNotFoundException(ExerciseNotFoundException exception) {

        log.warn("Exercise not found: {}", exception.getMessage());

        ErrorResponse errorResponse = new ErrorResponse("EXERCISE_NOT_FOUND", exception.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ExerciseAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleExerciseAlreadyExistsException(ExerciseAlreadyExistsException exception) {

        log.warn("Exercise already exists: {}", exception.getMessage());

        ErrorResponse errorResponse = new ErrorResponse("EXERCISE_ALREADY_EXISTS", exception.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {

        log.error("Database integrity violation exception caught", exception);

        String rootMsg = exception.getRootCause() == null
                ? ""
                : String.valueOf(exception.getRootCause().getMessage());

        if (rootMsg.contains("not-null") || rootMsg.contains("null value")) {
            ErrorResponse errorResponse = new ErrorResponse("DATA_INTEGRITY_VIOLATION", "Пропущено обязательное поле", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        if (rootMsg.contains("unique constraint") || rootMsg.contains("duplicate key")) {
            ErrorResponse errorResponse = new ErrorResponse("DATA_INTEGRITY_VIOLATION", "Ошибка валидации данных в базе", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        ErrorResponse errorResponse = new ErrorResponse("DATA_INTEGRITY_VIOLATION", "Ошибка валидации данных в базе", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedError(Exception exception) {

        log.error("SERVER ERROR: Caught unexpected server error", exception);

        ErrorResponse errorResponse = new ErrorResponse("INTERNAL_SERVER_ERROR", "Внутренняя ошибка сервера", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        Map<String, String> errors = new HashMap<>();

        if (exception.getCause() instanceof InvalidFormatException) {
            InvalidFormatException invalidFormatException = (InvalidFormatException) exception.getCause();
            if (invalidFormatException.getTargetType().equals(java.time.LocalDate.class)) {
                errors.put("error", "Неверный формат даты. Ожидается формат 'ГГГГ-ММ-ДД' (yyyy-MM-dd).");
                ErrorResponse errorResponse = new ErrorResponse("WRONG_JSON_FORMAT", "Неверный формат JSON", errors, LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
        }

        errors.put("error", "Некорректный JSON запрос. Проверьте синтаксис (кавычки, запятые, скобки).");
        ErrorResponse errorResponse = new ErrorResponse("WRONG_JSON_FORMAT", "Неверный формат JSON", errors, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {

        log.warn("Could not resolve id: {}", exception.getMessage());

        ErrorResponse errorResponse = new ErrorResponse("WRONG_ID", "Could not resolve id", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(WorkoutNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWorkoutNotFoundException(WorkoutNotFoundException exception) {

        log.warn("Could not find workout: {}", exception.getMessage());

        ErrorResponse errorResponse = new ErrorResponse("WORKOUT_NOT_FOUND", exception.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException exception) {

        log.warn("User already exists exception: {}", exception.getMessage());

        ErrorResponse errorResponse = new ErrorResponse("USER_ALREADY_EXISTS", exception.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException exception) {

        log.warn("Could not find user (UserNotFoundException): {}", exception.getMessage());

        ErrorResponse errorResponse = new ErrorResponse("USER_NOT_FOUND", exception.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException exception) {

        log.warn("Bad credentials exception: {}", exception.getMessage());

        ErrorResponse errorResponse = new ErrorResponse("AUTHENTICATION_FAILED", "Неверный логин или пароль", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException exception) {

        log.warn("Could not find user (UsernameNotFoundException): {}", exception.getMessage());

        ErrorResponse errorResponse = new ErrorResponse("USER_NOT_FOUND", exception.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
