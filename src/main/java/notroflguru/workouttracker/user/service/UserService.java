package notroflguru.workouttracker.user.service;

import jakarta.transaction.Transactional;
import notroflguru.workouttracker.user.dto.UserMapper;
import notroflguru.workouttracker.user.dto.request.LoginRequest;
import notroflguru.workouttracker.user.dto.request.RegisterRequest;
import notroflguru.workouttracker.user.dto.response.AuthResponse;
import notroflguru.workouttracker.user.dto.response.UserResponse;
import notroflguru.workouttracker.user.exception.UserAlreadyExistsException;
import notroflguru.workouttracker.user.exception.UserNotFoundException;
import notroflguru.workouttracker.user.model.Role;
import notroflguru.workouttracker.user.model.User;
import notroflguru.workouttracker.user.repository.UserRepository;
import notroflguru.workouttracker.user.security.JwtCore;
import notroflguru.workouttracker.user.security.SecurityUser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final JwtCore jwtCore;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtCore jwtCore,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtCore = jwtCore;
        this.userMapper = userMapper;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        String login = normalizeIdentifier(request.getLogin());
        String email = normalizeIdentifier(request.getEmail());

        userRepository.findByLoginOrEmail(login, email).ifPresent(existingUser -> {
            if (existingUser.getEmail().equalsIgnoreCase(email)) {
                throw new UserAlreadyExistsException("почтой", email);
            } else {
                throw new UserAlreadyExistsException("логином", login);
            }
        });

        User user = new User();

        user.setLogin(login);
        user.setName(request.getName());
        user.setEmail(email);
        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        SecurityUser userDetails = new SecurityUser(savedUser);

        String token = jwtCore.generateToken(userDetails);
        UserResponse userResponse = userMapper.toResponse(savedUser);

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

        String token = jwtCore.generateToken(userDetails);
        UserResponse userResponse = userMapper.toResponse(user);

        return new AuthResponse(token, userResponse);
    }


    private String normalizeIdentifier(String identifier) {

        return identifier.trim().toLowerCase();
    }

}
