package notroflguru.workouttracker.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import notroflguru.workouttracker.user.dto.request.PatchUserRequest;
import notroflguru.workouttracker.user.dto.response.UserResponse;
import notroflguru.workouttracker.user.security.SecurityUser;
import notroflguru.workouttracker.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "Управление пользователями", description = "Изменение и удаление пользователей")
public class UserController {

    private final UserService service;

    public UserController(UserService userService) {
        this.service = userService;
    }
    @GetMapping("/me")
    @Operation(summary = "Получение текущего пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь получен")
            })
    public ResponseEntity<UserResponse> getUser(
            @AuthenticationPrincipal SecurityUser userDetails
    ) {

        UserResponse response = service.getUser(userDetails);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me")
    @Operation(summary = "Изменение почты, имени и пароля текущего пользователя",
            responses = {@ApiResponse(responseCode = "200", description = "Пользователь успешно изменён")
            })
    public ResponseEntity<UserResponse> changeUser(
            @AuthenticationPrincipal SecurityUser userDetails,
            @Valid @RequestBody PatchUserRequest request
    ) {

        UserResponse response = service.patchUser(userDetails, request);

        return ResponseEntity.ok(response);
    }
}
