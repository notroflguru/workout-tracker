package notroflguru.workouttracker.user.service;

import notroflguru.workouttracker.user.dto.UserMapper;
import notroflguru.workouttracker.user.dto.request.LoginRequest;
import notroflguru.workouttracker.user.dto.request.RegisterRequest;
import notroflguru.workouttracker.user.dto.response.AuthResponse;
import notroflguru.workouttracker.user.exception.UserAlreadyExistsException;
import notroflguru.workouttracker.user.model.Role;
import notroflguru.workouttracker.user.model.User;
import notroflguru.workouttracker.user.repository.UserRepository;
import notroflguru.workouttracker.user.security.JwtCore;
import notroflguru.workouttracker.user.security.SecurityUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtCore jwtCore;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(
                userRepository,
                passwordEncoder,
                authenticationManager,
                jwtCore,
                new UserMapper()
        );
    }

    @Test
    void register_SavesUserWithEncodedPasswordAndUserRole() {
        RegisterRequest request = registerRequest();
        User savedUser = user();

        when(passwordEncoder.encode("StrongPassword123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtCore.generateToken(any(UserDetails.class))).thenReturn("jwt-token");

        AuthResponse result = userService.register(request);

        assertEquals(savedUser.getId(), result.getUser().getId());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User persistedUser = userCaptor.getValue();
        assertEquals("pavel", persistedUser.getLogin());
        assertEquals("Pavel", persistedUser.getName());
        assertEquals("pavel@example.com", persistedUser.getEmail());
        assertEquals("encoded-password", persistedUser.getPasswordHash());
        assertNotEquals(request.getPassword(), persistedUser.getPasswordHash());
        assertEquals(Role.USER, persistedUser.getRole());
    }

    @Test
    void register_TrimsLoginAndNormalizesEmailBeforeCheckingAndSaving() {
        RegisterRequest request = registerRequest();
        request.setLogin("  Pavel  ");
        request.setEmail("  PAVEL@EXAMPLE.COM ");
        User savedUser = user();

        when(userRepository.findByLoginOrEmail("pavel", "pavel@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtCore.generateToken(any(UserDetails.class))).thenReturn("");

        AuthResponse result = userService.register(request);

        verify(userRepository).findByLoginOrEmail("pavel", "pavel@example.com");
        assertEquals("pavel", result.getUser().getLogin());
        assertEquals("pavel@example.com", result.getUser().getEmail());
    }

    @Test
    void register_WhenLoginExists_ThrowsConflictException() {
        RegisterRequest request = registerRequest();
        User user = user();
        when(userRepository.findByLoginOrEmail("pavel", "pavel@example.com")).thenReturn(Optional.of(user));

        assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.register(request)
        );

        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void register_WhenEmailExists_ThrowsConflictException() {
        RegisterRequest request = registerRequest();
        User user = user();
        when(userRepository.findByLoginOrEmail("pavel", "pavel@example.com")).thenReturn(Optional.of(user));

        assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.register(request)
        );

        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void login_AuthenticatesUserAndReturnsGeneratedToken() {
        LoginRequest request = new LoginRequest();
        request.setIdentifier("pavel");
        request.setPassword("StrongPassword123");

        User user = user();

        UserDetails userDetails = new SecurityUser(user);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtCore.generateToken(userDetails)).thenReturn("jwt-token");

        AuthResponse result = userService.login(request);

        assertEquals("jwt-token", result.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtCore).generateToken(userDetails);
    }

    @Test
    void login_WhenLoginDoesNotExist_ThrowsNotFoundException() {
        LoginRequest request = new LoginRequest();
        
        request.setIdentifier("unknown");
        request.setPassword("StrongPassword123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException(""));

        assertThrows(
                BadCredentialsException.class,
                () -> userService.login(request)
        );

        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(request.getIdentifier(), request.getPassword()));
        verifyNoInteractions(jwtCore);
    }


    private RegisterRequest registerRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setLogin("pavel");
        request.setName("Pavel");
        request.setEmail("pavel@example.com");
        request.setPassword("StrongPassword123");
        return request;
    }

    private User user() {
        User user = new User();
        user.setLogin("pavel");
        user.setEmail("pavel@example.com");
        user.setName("Pavel");
        user.setPasswordHash("HashedPassword123456");
        user.setId(1L);
        return user;
    }
}
