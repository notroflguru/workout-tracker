package notroflguru.workouttracker.user.service;

import lombok.extern.slf4j.Slf4j;
import notroflguru.workouttracker.user.dto.UserMapper;
import notroflguru.workouttracker.user.dto.request.LoginRequest;
import notroflguru.workouttracker.user.dto.request.RegisterRequest;
import notroflguru.workouttracker.user.dto.response.AuthResponse;
import notroflguru.workouttracker.user.dto.response.UserResponse;
import notroflguru.workouttracker.user.exception.UserNotFoundException;
import notroflguru.workouttracker.user.model.User;
import notroflguru.workouttracker.user.security.JwtCore;
import notroflguru.workouttracker.user.security.SecurityUser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtCore jwtCore;
    private final UserService userService;
    private final UserMapper userMapper;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtCore jwtCore,
                       UserService userService,
                       UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtCore = jwtCore;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        User user = userService.createUser(request);

        SecurityUser userDetails = new SecurityUser(user);

        String token = jwtCore.generateToken(userDetails);
        UserResponse userResponse = userMapper.toResponse(user);

        log.info("Successfully created user with ID: {}", user.getId());

        return new AuthResponse(token, userResponse);
    }

    public AuthResponse login(LoginRequest request) {
        String identifier = normalizeIdentifier(request.getIdentifier());
        String password = request.getPassword();

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                identifier,
                                password
                        )
                );

        SecurityUser userDetails =
                (SecurityUser) authentication.getPrincipal();

        User user = userDetails.getUser();

        if (user==null) {
            throw new BadCredentialsException("");
        }

        String token = jwtCore.generateToken(userDetails);
        UserResponse userResponse = userMapper.toResponse(user);

        log.info("Successfully logged in user with ID: {}", user.getId());

        return new AuthResponse(token, userResponse);
    }

    private String normalizeIdentifier(String identifier) {
        return identifier.trim().toLowerCase();
    }
}
