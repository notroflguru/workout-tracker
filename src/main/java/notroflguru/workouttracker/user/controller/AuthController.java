package notroflguru.workouttracker.user.controller;


import jakarta.validation.Valid;
import notroflguru.workouttracker.user.dto.UserMapper;
import notroflguru.workouttracker.user.dto.request.LoginRequest;
import notroflguru.workouttracker.user.dto.request.RegisterRequest;
import notroflguru.workouttracker.user.dto.response.AuthResponse;
import notroflguru.workouttracker.user.dto.response.UserResponse;
import notroflguru.workouttracker.user.model.User;
import notroflguru.workouttracker.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService service;

    public AuthController(UserService userService) {
        this.service = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(
            @Valid @RequestBody RegisterRequest request) {

        AuthResponse response = service.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = service.login(request);

        return ResponseEntity.ok(response);
    }
}
