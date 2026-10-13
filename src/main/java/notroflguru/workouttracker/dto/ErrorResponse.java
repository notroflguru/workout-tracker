package notroflguru.workouttracker.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorResponse {
    private String code;
    private String message;
    private Map<String, String> errors;
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
