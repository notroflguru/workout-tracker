package notroflguru.workouttracker.user.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import notroflguru.workouttracker.user.model.Role;

@JsonPropertyOrder({"id", "email", "login", "name", "role"})
public class UserResponse {

    private Long id;
    private String login;
    private String name;
    private String email;
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