package notroflguru.workouttracker.user.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import notroflguru.workouttracker.user.model.Role;

@JsonPropertyOrder({"id", "email", "login", "name", "role"})
@Schema(description = "Объект ответа пользователя")
public class UserResponse {

    @Schema(description = "Уникальный идентификатор пользователя", example = "1")
    private Long id;

    @Schema(description = "Логин пользователя", example = "user-login")
    private String login;

    @Schema(description = "Имя пользователя", example = "pavel")
    private String name;

    @Schema(description = "Почта пользователя", example = "user@example.com")
    private String email;

    @Schema(description = "Роль пользователя", example = "USER")
    private Role role;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}