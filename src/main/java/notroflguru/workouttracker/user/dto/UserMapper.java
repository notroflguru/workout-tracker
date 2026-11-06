package notroflguru.workouttracker.user.dto;

import notroflguru.workouttracker.user.dto.response.UserResponse;
import notroflguru.workouttracker.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setLogin(user.getLogin());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setRole(user.getRole());

        return response;
    }
}
