package notroflguru.workouttracker.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Объект запроса авторизации")
public class LoginRequest {

    @NotBlank
    @Schema(description = "Почта или логин пользователя", examples = {"user@example.com", "user-login"})
    private String identifier; // login OR email

    @NotBlank
    @Schema(description = "Пароль пользователя", example = "Very_Strong_Password123")
    private String password;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}