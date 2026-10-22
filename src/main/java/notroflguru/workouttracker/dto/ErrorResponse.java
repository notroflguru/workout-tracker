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
    private LocalDateTime time;

    public ErrorResponse(String code, String message, LocalDateTime time) {
        this.code = code;
        this.message = message;
        this.time = time;
    }

    public ErrorResponse(String code, String message, Map<String, String> errors, LocalDateTime time) {
        this.code = code;
        this.message = message;
        this.errors = errors;
        this.time = time;
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

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
}
