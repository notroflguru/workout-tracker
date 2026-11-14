package notroflguru.workouttracker.admin.dto.request;

import jakarta.validation.constraints.NotNull;
import notroflguru.workouttracker.user.model.Role;

public class ChangeUserRoleRequest {
    @NotNull
    private Role role;


    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

}
