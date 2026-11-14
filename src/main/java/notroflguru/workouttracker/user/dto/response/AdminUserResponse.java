package notroflguru.workouttracker.user.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import notroflguru.workouttracker.user.model.Role;

@JsonPropertyOrder({"id", "login", "role"})
public class AdminUserResponse {
    private Long id;

    private String login;

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
