package notroflguru.workouttracker.user.service;

import notroflguru.workouttracker.user.dto.request.RegisterRequest;
import notroflguru.workouttracker.user.exception.UserAlreadyExistsException;
import notroflguru.workouttracker.user.model.Role;
import notroflguru.workouttracker.user.model.User;
import notroflguru.workouttracker.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class UserServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:18.6-alpine")
                    .withDatabaseName("workout-database-test")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should successfully create user")
    void createUser_SuccessfullySaved() {
        RegisterRequest request = registerRequest();

        User result = userService.createUser(request);

        assertNotNull(result.getId());
        assertEquals("pavel", result.getLogin());
        assertEquals("Pavel", result.getName());
        assertEquals("pavel@example.com", result.getEmail());
        assertNotEquals(request.getPassword(), result.getPasswordHash());

        assertTrue(userRepository.existsById(result.getId()));
    }

    @Test
    @DisplayName("Should save encoded password")
    void createUser_EncodesPassword() {
        RegisterRequest request = registerRequest();

        User result = userService.createUser(request);

        User savedUser = userRepository.findById(result.getId())
                .orElseThrow();

        assertNotEquals(request.getPassword(), savedUser.getPasswordHash());
        assertNotNull(savedUser.getPasswordHash());
    }

    @Test
    @DisplayName("Should assign USER role")
    void createUser_SetsUserRole() {
        User result = userService.createUser(registerRequest());

        assertEquals(Role.USER, result.getRole());
    }

    @Test
    @DisplayName("Should throw exception when login already exists")
    void createUser_WhenLoginExists_ThrowsException() {
        userService.createUser(registerRequest());

        RegisterRequest duplicate = registerRequest();
        duplicate.setEmail("another@example.com");

        assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.createUser(duplicate)
        );

        assertEquals(1, userRepository.count());
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void createUser_WhenEmailExists_ThrowsException() {
        userService.createUser(registerRequest());

        RegisterRequest duplicate = registerRequest();
        duplicate.setLogin("another");

        assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.createUser(duplicate)
        );

        assertEquals(1, userRepository.count());
    }

    private RegisterRequest registerRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setLogin("pavel");
        request.setName("Pavel");
        request.setEmail("pavel@example.com");
        request.setPassword("StrongPassword123");
        return request;
    }
}