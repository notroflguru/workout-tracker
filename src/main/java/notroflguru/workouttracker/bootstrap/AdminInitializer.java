package notroflguru.workouttracker.bootstrap;

import lombok.RequiredArgsConstructor;
import notroflguru.workouttracker.user.model.Role;
import notroflguru.workouttracker.user.model.User;
import notroflguru.workouttracker.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.login:}")
    private String login;

    @Value("${app.admin.password:}")
    private String password;

    @Value("${app.admin.email:}")
    private String email;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.existsByRole(Role.ADMIN)) {
            return;
        }

        if (login == null || login.isBlank()
                || password == null || password.isBlank()
                || email == null || email.isBlank()) {
            throw new IllegalStateException(
                    "Initial admin credentials are not configured"
            );
        }

        User admin = new User();
        admin.setLogin(login);
        admin.setName("admin");
        admin.setPasswordHash(passwordEncoder.encode(password));
        admin.setEmail(email);
        admin.setRole(Role.ADMIN);

        userRepository.save(admin);
    }
}