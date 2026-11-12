package notroflguru.workouttracker.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Объект запроса изменения пользователя")
public class PatchUserRequest {
    @Size(max = 100)
    @Schema(description = "Имя пользователя", example = "changed_pavel")
    private String name;

    @Email
    @Schema(description = "Почта пользователя", example = "changed_user@example.com")
    private String email;

    @Size(min = 8, max = 100)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_])[A-Za-z\\d@$!%*?&_]{8,}$",
            message = "Пароль должен состоять минимум из 8 символов, включать заглавные и строчные буквы, цифры и специальные знаки."
    )
    @Schema(description = "Пароль пользователя", example = "Very_Strong_Password_123")
    private String password;

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
