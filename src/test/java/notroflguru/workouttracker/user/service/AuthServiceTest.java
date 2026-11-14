package notroflguru.workouttracker.user.service;

import notroflguru.workouttracker.user.dto.UserMapper;
import notroflguru.workouttracker.user.dto.request.LoginRequest;
import notroflguru.workouttracker.user.dto.request.RegisterRequest;
import notroflguru.workouttracker.user.dto.response.AuthResponse;
import notroflguru.workouttracker.user.exception.UserAlreadyExistsException;
import notroflguru.workouttracker.user.exception.UserNotFoundException;
import notroflguru.workouttracker.user.model.User;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtCore jwtCore;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(authenticationManager, jwtCore, userService, new UserMapper());
    }

    @Test
    void register_CreatesUserAndReturnsAuthResponse() {
        RegisterRequest request = registerRequest();
        User savedUser = user();

        when(userService.createUser(any(RegisterRequest.class)))
                .thenReturn(savedUser);

        when(jwtCore.generateToken(any(UserDetails.class)))
                .thenReturn("jwt-token");

        AuthResponse result = authService.register(request);

        assertEquals(savedUser.getId(), result.getUser().getId());

        ArgumentCaptor<RegisterRequest> captor =
                ArgumentCaptor.forClass(RegisterRequest.class);

        verify(userService).createUser(captor.capture());

        RegisterRequest capturedRequest = captor.getValue();

        assertEquals("jwt-token", result.getToken());
        assertEquals("pavel", result.getUser().getLogin());
        assertEquals("Pavel", result.getUser().getName());
        assertEquals("pavel@example.com", result.getUser().getEmail());
        assertEquals(request.getLogin(), capturedRequest.getLogin());
        assertEquals(request.getName(), capturedRequest.getName());
        assertEquals(request.getEmail(), capturedRequest.getEmail());
        assertEquals(request.getPassword(), capturedRequest.getPassword());

        verify(jwtCore).generateToken(any(SecurityUser.class));
    }

    @Test
    void login_TrimsAndNormalizesIdentifierBeforeAuthentication() {
        LoginRequest request = new LoginRequest();
        request.setIdentifier("  PAVEL  ");
        request.setPassword("StrongPassword123");

        User user = user();
        SecurityUser userDetails = new SecurityUser(user);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtCore.generateToken(userDetails)).thenReturn("jwt-token");

        authService.login(request);

        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);

        verify(authenticationManager).authenticate(captor.capture());

        assertEquals("pavel", captor.getValue().getPrincipal());
        assertEquals("StrongPassword123", captor.getValue().getCredentials());
    }

    @Test
    void register_WhenLoginExists_ThrowsConflictException() {
        RegisterRequest request = registerRequest();
        when(userService.createUser(any(RegisterRequest.class))).thenThrow(new UserAlreadyExistsException("login", "pavel"));

        assertThrows(
                UserAlreadyExistsException.class,
                () -> authService.register(request)
        );
    }

    @Test
    void register_WhenEmailExists_ThrowsConflictException() {
        RegisterRequest request = registerRequest();
        when(userService.createUser(any(RegisterRequest.class))).thenThrow(new UserAlreadyExistsException("email", "email@example.com"));

        assertThrows(
                UserAlreadyExistsException.class,
                () -> authService.register(request)
        );
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

        AuthResponse result = authService.login(request);

        assertEquals("jwt-token", result.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtCore).generateToken(userDetails);
    }

    @Test
    void login_WhenCredentialsAreInvalid_ThrowsBadCredentialsException() {
        LoginRequest request = new LoginRequest();

        request.setIdentifier("unknown");
        request.setPassword("StrongPassword123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException(""));

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(request.getIdentifier(), request.getPassword()));
        verifyNoInteractions(jwtCore);
    }

    @Test
    void login_WhenUserIsNull_ThrowsUserNotFoundException() {
        LoginRequest request = new LoginRequest();
        request.setIdentifier("pavel");
        request.setPassword("StrongPassword123");

        SecurityUser userDetails = mock(SecurityUser.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUser()).thenReturn(null);

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

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
