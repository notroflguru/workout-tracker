package notroflguru.workouttracker.user.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import notroflguru.workouttracker.user.dto.UserMapper;
import notroflguru.workouttracker.user.dto.request.LoginRequest;
import notroflguru.workouttracker.user.dto.request.PatchUserRequest;
import notroflguru.workouttracker.user.dto.request.RegisterRequest;
import notroflguru.workouttracker.user.dto.response.AuthResponse;
import notroflguru.workouttracker.user.dto.response.UserResponse;
import notroflguru.workouttracker.user.model.User;
import notroflguru.workouttracker.user.security.SecurityUser;
import notroflguru.workouttracker.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/auth")
@Tag(name = "Регистрация и авторизация", description = "Создание и авторизация пользователей")
public class AuthController {

    private final UserService service;

    public AuthController(UserService userService) {
        this.service = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "Регистрация (и автоматическая авторизация) пользователя",
    responses = {
            @ApiResponse(responseCode = "201", description = "Регистрация прошла успешно")
    })
    public ResponseEntity<AuthResponse> registerUser(
            @Valid @RequestBody RegisterRequest request
    ) {

        AuthResponse response = service.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Авторизация пользователя",
    responses = {
            @ApiResponse(responseCode = "200", description = "Авторизация прошла успешно")
    })
    public ResponseEntity<AuthResponse> loginUser(
            @Valid @RequestBody LoginRequest request
    ) {

        AuthResponse response = service.login(request);

        return ResponseEntity.ok(response);
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
