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
    private final UserMapper mapper;

    public AuthController(UserService userService, UserMapper userMapper) {
        this.service = userService;
        this.mapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(
            @Valid @RequestBody RegisterRequest request) {

        User user = service.register(request);
        UserResponse response = mapper.toResponse(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(
            @Valid @RequestBody LoginRequest request) {

        String token = service.login(request);
        UserResponse user = mapper.toResponse(service.findByLogin(request.getLogin()));

        AuthResponse response = new AuthResponse(token, user);

        return ResponseEntity.ok(response);
    }
}
