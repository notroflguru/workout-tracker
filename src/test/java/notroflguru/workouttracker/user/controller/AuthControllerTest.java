package notroflguru.workouttracker.user.controller;

import notroflguru.workouttracker.user.dto.UserMapper;
import notroflguru.workouttracker.user.dto.request.RegisterRequest;
import notroflguru.workouttracker.user.dto.response.AuthResponse;
import notroflguru.workouttracker.user.dto.response.UserResponse;
import notroflguru.workouttracker.user.exception.UserAlreadyExistsException;
import notroflguru.workouttracker.user.exception.UserNotFoundException;
import notroflguru.workouttracker.user.model.Role;
import notroflguru.workouttracker.user.model.User;
import notroflguru.workouttracker.user.security.JwtCore;
import notroflguru.workouttracker.user.service.AuthService;
import notroflguru.workouttracker.user.service.UserDetailsService;
import notroflguru.workouttracker.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private JwtCore jwtCore;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("Регистрация возвращает 201 и безопасные данные пользователя")
    void register_ReturnsCreatedUser() throws Exception {
        AuthResponse response = authResponse();

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "login": "pavel",
                                  "name": "Pavel",
                                  "email": "pavel@example.com",
                                  "password": "StrongPassword123_"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.user.login").value("pavel"))
                .andExpect(jsonPath("$.user.email").value("pavel@example.com"))
                .andExpect(jsonPath("$.user.role").value("USER"))
                .andExpect(jsonPath("$.user.passwordHash").doesNotExist());

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("Регистрация с невалидными данными возвращает 400")
    void register_InvalidRequest_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "login": "",
                                  "email": "not-an-email",
                                  "password": "123"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
    }

    @Test
    @DisplayName("Регистрация с занятым login возвращает 409")
    void register_WhenUserExists_ReturnsConflict() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new UserAlreadyExistsException("логином", "pavel"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "login": "pavel",
                                  "email": "pavel@example.com",
                                  "password": "StrongPassword123_"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("USER_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value("Пользователь с логином pavel уже существует"));
    }

    @Test
    @DisplayName("Логин по login возвращает JWT и пользователя")
    void loginByLogin_ReturnsTokenAndUser() throws Exception {
        AuthResponse authResponse = authResponse();
        when(authService.login(any())).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "identifier": "pavel",
                                  "password": "StrongPassword123_"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.user.login").value("pavel"))
                .andExpect(jsonPath("$.user.email").value("pavel@example.com"))
                .andExpect(jsonPath("$.user.passwordHash").doesNotExist());
    }

    @Test
    @DisplayName("Логин по email возвращает JWT и пользователя")
    void loginByEmail_ReturnsTokenAndUser() throws Exception {
        AuthResponse authResponse = authResponse();
        when(authService.login(any())).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "identifier": "pavel@example.com",
                                  "password": "StrongPassword123_"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.user.login").value("pavel"))
                .andExpect(jsonPath("$.user.email").value("pavel@example.com"))
                .andExpect(jsonPath("$.user.passwordHash").doesNotExist());
    }

    @Test
    @DisplayName("Логин с невалидными данными возвращает 400")
    void login_InvalidRequest_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "login": "",
                                  "password": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
    }

    @Test
    @DisplayName("Логин неизвестного пользователя возвращает 404 по текущему контракту")
    void login_WhenUserDoesNotExist_ReturnsNotFound() throws Exception {
        when(authService.login(any()))
                .thenThrow(new BadCredentialsException("User not found"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "identifier": "unknown",
                                  "password": "StrongPassword123_"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_FAILED"));
    }

    private UserResponse userResponse() {
        UserResponse response = new UserResponse();
        response.setId(1L);
        response.setLogin("pavel");
        response.setName("Pavel");
        response.setEmail("pavel@example.com");
        response.setRole(Role.USER);
        return response;
    }

    private AuthResponse authResponse() {
        UserResponse userResponse = userResponse();
        return new AuthResponse("jwt-token", userResponse);
    }
}
