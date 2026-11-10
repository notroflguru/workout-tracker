package notroflguru.workouttracker.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Объект запроса регистрации")
public class RegisterRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    @Pattern(
            regexp = "^[a-zA-Z0-9_-]{3,20}$",
            message = "Логин должен содержать только латиницу, цифры, символы _ и -"
    )
    @Schema(description = "Логин пользователя", example = "user-login")
    private String login;

    @Size(max = 100)
    @Schema(description = "Имя пользователя", example = "pavel")
    private String name;

    @NotBlank
    @Email
    @Schema(description = "Почта пользователя", example = "user@example.com")
    private String email;

    @NotBlank
    @Size(min = 8, max = 100)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_])[A-Za-z\\d@$!%*?&_]{8,}$",
            message = "Пароль должен состоять минимум из 8 символов, включать заглавные и строчные буквы, цифры и специальные знаки."
    )
    @Schema(description = "Пароль пользователя", example = "Very_Strong_Password_123")
    private String password;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
