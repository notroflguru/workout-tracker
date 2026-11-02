package notroflguru.workouttracker.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Универсальный объект ответа при возникновении ошибки")
public class ErrorResponse {

    @Schema(description = "Код ошибки", example = "VALIDATION_FAILED")
    private String code;

    @Schema(description = "Описание ошибки", example = "Request validation failed")
    private String message;

    @Schema(description = "Список ошибок", example = "\"muscleList\": \"не должно быть пустым\"")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> errors;

    @Schema(description = "Временная метка ошибки", example = "2026-01-01 00:01:02")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public ErrorResponse(String code, String message, LocalDateTime timestamp) {
        this.code = code;
        this.message = message;
        this.timestamp = timestamp;
    }

    public ErrorResponse(String code, String message, Map<String, String> errors, LocalDateTime timestamp) {
        this.code = code;
        this.message = message;
        this.errors = errors;
        this.timestamp = timestamp;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
}
