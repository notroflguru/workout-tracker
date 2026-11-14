package notroflguru.workouttracker.user.service;

import lombok.extern.slf4j.Slf4j;
import notroflguru.workouttracker.user.dto.UserMapper;
import notroflguru.workouttracker.user.dto.request.PatchUserRequest;
import notroflguru.workouttracker.user.dto.request.RegisterRequest;
import notroflguru.workouttracker.user.dto.response.UserResponse;
import notroflguru.workouttracker.user.exception.UserAlreadyExistsException;
import notroflguru.workouttracker.user.exception.UserNotFoundException;
import notroflguru.workouttracker.user.model.Role;
import notroflguru.workouttracker.user.model.User;
import notroflguru.workouttracker.user.repository.UserRepository;
import notroflguru.workouttracker.user.security.SecurityUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Transactional
    public User createUser(RegisterRequest request) {

        String login = normalizeIdentifier(request.getLogin());
        String email = normalizeIdentifier(request.getEmail());
        String name = request.getName().trim();

        if (userRepository.existsByLogin(login)) {
            throw new UserAlreadyExistsException("логином", login);
        }

        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("почтой", email);
        }

        User user = new User();
        user.setLogin(login);
        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );
        user.setRole(Role.USER);

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(SecurityUser userDetails) {
        User user = userRepository.findById(userDetails.getUser().getId())
                .orElseThrow(UserNotFoundException::new);

        log.info("Successfully found user with ID: {}", userDetails.getUser().getId());

        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse patchUser(SecurityUser userDetails, PatchUserRequest request) {
        User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(UserNotFoundException::new);

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName().trim());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String email = normalizeIdentifier(request.getEmail());

            if (!user.getEmail().equalsIgnoreCase(email)
                    && userRepository.existsByEmail(email)) {
                throw new UserAlreadyExistsException("почтой", email);
            }

            user.setEmail(email);
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPasswordHash(
                    passwordEncoder.encode(request.getPassword())
            );
        }

        log.info("Successfully patched user with ID: {}", user.getId());

        return userMapper.toResponse(user);
    }


    private String normalizeIdentifier(String identifier) {
        return identifier.trim().toLowerCase();
    }
}
