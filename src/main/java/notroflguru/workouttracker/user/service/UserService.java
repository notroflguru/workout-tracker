package notroflguru.workouttracker.user.service;

import jakarta.transaction.Transactional;
import notroflguru.workouttracker.user.dto.request.LoginRequest;
import notroflguru.workouttracker.user.dto.request.RegisterRequest;
import notroflguru.workouttracker.user.exception.UserAlreadyExistsException;
import notroflguru.workouttracker.user.exception.UserNotFoundException;
import notroflguru.workouttracker.user.model.Role;
import notroflguru.workouttracker.user.model.User;
import notroflguru.workouttracker.user.repository.UserRepository;
import notroflguru.workouttracker.user.security.JwtCore;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final JwtCore jwtCore;
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtCore jwtCore) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtCore = jwtCore;
    }
    @Transactional
    public User register(RegisterRequest request) {

        if (userRepository.existsByLogin(request.getLogin())) {
            throw new UserAlreadyExistsException("Логин уже занят");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email уже занят");
        }

        User user = new User();
        user.setLogin(request.getLogin().trim());
        user.setName(request.getName());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );
        user.setRole(Role.USER);

        return userRepository.save(user);
    }

    public String login(LoginRequest request) {

        if (! userRepository.existsByLogin(request.getLogin())) {
            throw new UserNotFoundException("User not found");
        }

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getLogin(),
                                request.getPassword()
                        )
                );

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        String token = jwtCore.generateToken(userDetails);

        return token;
    }

    public User findByLogin(String login) {
        return userRepository.findByLogin(login).orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
