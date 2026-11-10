package notroflguru.workouttracker.user.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonPropertyOrder({"token", "user"})
@Schema(description = "Объект ответа авторизации")
public class AuthResponse {

    @Schema(description = "Токен авторизации", example = "a_very_long_jwt_token")
    private String token;

    @Schema(description = "Авторизованный пользователь")
    private UserResponse user;

    public AuthResponse(String token, UserResponse user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }
}
