package notroflguru.workouttracker.user.service;

import notroflguru.workouttracker.user.dto.UserMapper;
import notroflguru.workouttracker.user.dto.request.RegisterRequest;
import notroflguru.workouttracker.user.exception.UserAlreadyExistsException;
import notroflguru.workouttracker.user.model.Role;
import notroflguru.workouttracker.user.model.User;
import notroflguru.workouttracker.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder, new UserMapper());
    }

    @Test
    void createUser_SuccessfullySaved() {
        RegisterRequest request = registerRequest();

        when(userRepository.existsByLogin(any(String.class))).thenReturn(false);
        when(userRepository.existsByEmail(any(String.class))).thenReturn(false);
        when(passwordEncoder.encode(any(String.class))).thenReturn("very-long-encoded-password-hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L); // Имитирует работу БД по присвоению ID
            return savedUser;
        });

        User result = userService.createUser(request);
        assertEquals(1L, result.getId());
        assertEquals("pavel@example.com", result.getEmail());
        assertEquals("pavel", result.getLogin());
        assertEquals("Pavel", result.getName());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_EncodesPassword() {
        RegisterRequest request = registerRequest();

        when(userRepository.existsByLogin(any(String.class))).thenReturn(false);
        when(userRepository.existsByEmail(any(String.class))).thenReturn(false);
        when(passwordEncoder.encode(any(String.class))).thenReturn("very-long-encoded-password-hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.createUser(request);
        assertEquals("very-long-encoded-password-hash", result.getPasswordHash());

        verify(passwordEncoder).encode(any(String.class));
    }

    @Test
    void createUser_SetsUserRole() {
        RegisterRequest request = registerRequest();

        when(userRepository.existsByLogin(any(String.class))).thenReturn(false);
        when(userRepository.existsByEmail(any(String.class))).thenReturn(false);
        when(passwordEncoder.encode(any(String.class))).thenReturn("very-long-encoded-password-hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.createUser(request);
        assertEquals(Role.USER, result.getRole());
    }

    @Test
    void createUser_SavesInRepository() {
        RegisterRequest request = registerRequest();

        when(userRepository.existsByLogin(any(String.class))).thenReturn(false);
        when(userRepository.existsByEmail(any(String.class))).thenReturn(false);
        when(passwordEncoder.encode(any(String.class))).thenReturn("very-long-encoded-password-hash");

        userService.createUser(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(request.getLogin(), savedUser.getLogin());
        assertEquals(request.getEmail(), savedUser.getEmail());
        assertEquals("very-long-encoded-password-hash", savedUser.getPasswordHash());
    }

    @Test
    void createUser_ShouldThrowExceptionWhenLoginAlreadyExists() {
        RegisterRequest request = registerRequest();

        when(userRepository.existsByLogin(request.getLogin())).thenReturn(true);

        assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.createUser(request)
        );

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void createUser_ShouldThrowExceptionWhenEmailAlreadyExists() {
        RegisterRequest request = registerRequest();

        when(userRepository.existsByLogin(request.getLogin())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.createUser(request)
        );

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
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