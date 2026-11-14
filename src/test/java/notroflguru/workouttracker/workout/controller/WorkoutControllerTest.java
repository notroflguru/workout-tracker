package notroflguru.workouttracker.workout.controller;

import notroflguru.workouttracker.user.model.Role;
import notroflguru.workouttracker.user.model.User;
import notroflguru.workouttracker.user.security.JwtAuthenticationFilter;
import notroflguru.workouttracker.user.security.SecurityUser;
import notroflguru.workouttracker.user.service.UserDetailsService;
import notroflguru.workouttracker.workout.dto.request.CreateWorkoutRequest;
import notroflguru.workouttracker.workout.dto.request.PatchWorkoutRequest;
import notroflguru.workouttracker.workout.dto.response.ExerciseSetResponse;
import notroflguru.workouttracker.workout.dto.response.ShortWorkoutResponse;
import notroflguru.workouttracker.workout.dto.response.WorkoutExerciseResponse;
import notroflguru.workouttracker.workout.dto.response.WorkoutResponse;
import notroflguru.workouttracker.workout.exception.WorkoutNotFoundException;
import notroflguru.workouttracker.workout.service.WorkoutService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WorkoutController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Import(WorkoutControllerTest.MvcTestConfig.class)
public class WorkoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkoutService workoutService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UserDetailsService userDetailsService;



    private User testUser;
    private SecurityUser securityUser;
    private UsernamePasswordAuthenticationToken authentication;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setLogin("test-user");
        testUser.setRole(Role.USER);

        securityUser = new SecurityUser(testUser);

        authentication = new UsernamePasswordAuthenticationToken(
                securityUser,
                null,
                securityUser.getAuthorities()
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @TestConfiguration
    static class MvcTestConfig {

        @Bean
        static BeanPostProcessor authenticationPrincipalResolver() {
            return new BeanPostProcessor() {
                @Override
                public Object postProcessAfterInitialization(Object bean, String beanName) {
                    if (bean instanceof RequestMappingHandlerAdapter adapter) {
                        List<HandlerMethodArgumentResolver> resolvers =
                                new java.util.ArrayList<>();
                        resolvers.add(new TestAuthenticationPrincipalResolver());
                        resolvers.addAll(adapter.getArgumentResolvers());
                        adapter.setArgumentResolvers(resolvers);
                    }
                    return bean;
                }
            };
        }
    }

    private static class TestAuthenticationPrincipalResolver
            implements HandlerMethodArgumentResolver {

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return parameter.hasParameterAnnotation(AuthenticationPrincipal.class)
                    && SecurityUser.class.isAssignableFrom(parameter.getParameterType());
        }

        @Override
        public Object resolveArgument(
                MethodParameter parameter,
                ModelAndViewContainer mavContainer,
                NativeWebRequest webRequest,
                WebDataBinderFactory binderFactory
        ) {
            User user = new User();
            user.setId(1L);
            user.setEmail("test@example.com");
            user.setLogin("test-user");
            user.setRole(Role.USER);
            return new SecurityUser(user);
        }
    }

    @Test
    @DisplayName("Should successfully create a workout and return 201")
    void createWorkout_Created_Returns201() throws Exception {
        String requestJson = """
            {
              "date": "2026-10-22",
              "notes": "Тестовая тренировка",
              "workoutExerciseRequestList": [
                {
                  "exerciseId": 2,
                  "orderNumber": 1,
                  "exerciseSetRequestList": [
                    {
                      "weight": 100.0,
                      "reps": 8,
                      "rir": "2",
                      "orderNumber": 1
                    }
                  ]
                }
              ]
            }
            """;

        ExerciseSetResponse setResponse = new ExerciseSetResponse();
        setResponse.setId(1L);
        setResponse.setWeight(new BigDecimal("100.0"));
        setResponse.setReps(8);
        setResponse.setRir("2");
        setResponse.setOrderNumber(1);

        WorkoutExerciseResponse exerciseResponse = new WorkoutExerciseResponse();
        exerciseResponse.setId(1L);
        exerciseResponse.setExerciseId(2L);
        exerciseResponse.setExerciseName("Bench press");
        exerciseResponse.setOrderNumber(1);
        exerciseResponse.setExerciseSetResponseList(List.of(setResponse));

        WorkoutResponse mockResponse = new WorkoutResponse();
        mockResponse.setId(1L);
        mockResponse.setDate(LocalDate.of(2026, Month.OCTOBER, 22));
        mockResponse.setNotes("Тестовая тренировка");
        mockResponse.setWorkoutExerciseResponseList(List.of(exerciseResponse));

        when(workoutService.createWorkout(any(CreateWorkoutRequest.class), anyLong()))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/api/workouts")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.date").value("2026-10-22"))
                .andExpect(jsonPath("$.notes").value("Тестовая тренировка"))
                .andExpect(jsonPath("$.workoutExerciseResponseList[0].exerciseId").value(2))
                .andExpect(jsonPath("$.workoutExerciseResponseList[0].exerciseName").value("Bench press"))
                .andExpect(jsonPath("$.workoutExerciseResponseList[0].exerciseSetResponseList[0].weight").value(100.0))
                .andExpect(jsonPath("$.workoutExerciseResponseList[0].exerciseSetResponseList[0].reps").value(8))
                .andExpect(jsonPath("$.workoutExerciseResponseList[0].exerciseSetResponseList[0].rir").value("2"));
    }

    @Test
    @DisplayName("Should return 400 when workout exercises are null")
    void whenWorkoutExercisesAreNull_Returns400() throws Exception {
        String requestJson = """
            {
              "date": "2026-10-22",
              "notes": "Тестовая тренировка",
              "workoutExerciseRequestList": null
            }
            """;

        mockMvc.perform(post("/api/workouts")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when workout exercises are empty")
    void whenWorkoutExercisesAreEmpty_Returns400() throws Exception {
        String requestJson = """
            {
              "date": "2026-10-22",
              "notes": "Тестовая тренировка",
              "workoutExerciseRequestList": []
            }
            """;

        mockMvc.perform(post("/api/workouts")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when exercise sets are null")
    void whenExerciseSetsAreNull_Returns400() throws Exception {
        String requestJson = """
            {
              "date": "2026-10-22",
              "workoutExerciseRequestList": [
                {
                  "exerciseId": 2,
                  "orderNumber": 1,
                  "exerciseSetRequestList": null
                }
              ]
            }
            """;

        mockMvc.perform(post("/api/workouts")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when exercise sets are empty")
    void whenExerciseSetsAreEmpty_Returns400() throws Exception {
        String requestJson = """
            {
              "date": "2026-10-22",
              "workoutExerciseRequestList": [
                {
                  "exerciseId": 2,
                  "orderNumber": 1,
                  "exerciseSetRequestList": []
                }
              ]
            }
            """;

        mockMvc.perform(post("/api/workouts")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when weight is negative")
    void whenWeightIsNegative_Returns400() throws Exception {
        String requestJson = """
            {
              "date": "2026-10-22",
              "workoutExerciseRequestList": [
                {
                  "exerciseId": 2,
                  "orderNumber": 1,
                  "exerciseSetRequestList": [
                    {
                      "weight": -10,
                      "reps": 8,
                      "rir": "2",
                      "orderNumber": 1
                    }
                  ]
                }
              ]
            }
            """;

        mockMvc.perform(post("/api/workouts")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when reps are zero")
    void whenRepsAreZero_Returns400() throws Exception {
        String requestJson = """
            {
              "date": "2026-10-22",
              "workoutExerciseRequestList": [
                {
                  "exerciseId": 2,
                  "orderNumber": 1,
                  "exerciseSetRequestList": [
                    {
                      "weight": 100,
                      "reps": 0,
                      "rir": "2",
                      "orderNumber": 1
                    }
                  ]
                }
              ]
            }
            """;

        mockMvc.perform(post("/api/workouts")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when set order is negative")
    void whenSetOrderIsNegative_Returns400() throws Exception {
        String requestJson = """
            {
              "date": "2026-10-22",
              "workoutExerciseRequestList": [
                {
                  "exerciseId": 2,
                  "orderNumber": 1,
                  "exerciseSetRequestList": [
                    {
                      "weight": 100,
                      "reps": 8,
                      "rir": "2",
                      "orderNumber": -1
                    }
                  ]
                }
              ]
            }
            """;

        mockMvc.perform(post("/api/workouts")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when exercise order is negative")
    void whenExerciseOrderIsNegative_Returns400() throws Exception {
        String requestJson = """
            {
              "date": "2026-10-22",
              "workoutExerciseRequestList": [
                {
                  "exerciseId": 2,
                  "orderNumber": -1,
                  "exerciseSetRequestList": [
                    {
                      "weight": 100,
                      "reps": 8,
                      "rir": "2",
                      "orderNumber": 1
                    }
                  ]
                }
              ]
            }
            """;

        mockMvc.perform(post("/api/workouts")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    private WorkoutResponse createWorkoutResponse() {
        ExerciseSetResponse setResponse = new ExerciseSetResponse();
        setResponse.setId(1L);
        setResponse.setWeight(new BigDecimal("100.0"));
        setResponse.setReps(8);
        setResponse.setRir("2");
        setResponse.setOrderNumber(1);

        WorkoutExerciseResponse exerciseResponse = new WorkoutExerciseResponse();
        exerciseResponse.setId(1L);
        exerciseResponse.setExerciseId(2L);
        exerciseResponse.setExerciseName("Bench press");
        exerciseResponse.setOrderNumber(1);
        exerciseResponse.setExerciseSetResponseList(List.of(setResponse));

        WorkoutResponse response = new WorkoutResponse();
        response.setId(1L);
        response.setDate(LocalDate.of(2026, Month.OCTOBER, 22));
        response.setNotes("Тестовая тренировка");
        response.setWorkoutExerciseResponseList(List.of(exerciseResponse));

        return response;
    }

    @Test
    @DisplayName("Should return workout by ID and HTTP 200")
    void findWorkoutById_Returns200() throws Exception {
        WorkoutResponse response = createWorkoutResponse();

        when(workoutService.findWorkoutById(1L, 1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/workouts/{id}", 1L)
                        .with(authentication(authentication))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.date").value("2026-10-22"))
                .andExpect(jsonPath("$.notes").value("Тестовая тренировка"))
                .andExpect(jsonPath("$.workoutExerciseResponseList[0].exerciseId").value(2))
                .andExpect(jsonPath("$.workoutExerciseResponseList[0].exerciseName").value("Bench press"));
    }

    @Test
    @DisplayName("Should return 404 when workout does not exist")
    void findWorkoutById_NotFound_Returns404() throws Exception {
        when(workoutService.findWorkoutById(999L, 1L))
                .thenThrow(new WorkoutNotFoundException());

        mockMvc.perform(get("/api/workouts/{id}", 999L)
                        .with(authentication(authentication)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("WORKOUT_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Тренировка не найдена"));
    }

    @Test
    @DisplayName("Should return 400 when workout ID is invalid")
    void findWorkoutById_InvalidId_Returns400() throws Exception {
        mockMvc.perform(get("/api/workouts/{id}", "abc")
                        .with(authentication(authentication)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("WRONG_ID"));
    }

    @Test
    @DisplayName("Should partially update workout and return 200")
    void patchWorkout_Returns200() throws Exception {
        String requestJson = """
            {
              "date": "2026-11-02",
              "notes": "Обновлённая тренировка"
            }
            """;

        WorkoutResponse response = createWorkoutResponse();
        response.setDate(LocalDate.of(2026, Month.NOVEMBER, 2));
        response.setNotes("Обновлённая тренировка");

        when(workoutService.updateWorkout(
                anyLong(),
                any(notroflguru.workouttracker.workout.dto.request.PatchWorkoutRequest.class),
                anyLong()
        )).thenReturn(response);

        mockMvc.perform(patch("/api/workouts/{id}", 1L)
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.date").value("2026-11-02"))
                .andExpect(jsonPath("$.notes").value("Обновлённая тренировка"));

        verify(workoutService).updateWorkout(
                eq(1L),
                any(PatchWorkoutRequest.class),
                eq(1L)
        );
    }

    @Test
    @DisplayName("Should accept empty PATCH body without changing workout")
    void patchWorkout_EmptyBody_Returns200() throws Exception {
        WorkoutResponse response = createWorkoutResponse();

        when(workoutService.updateWorkout(
                eq(1L),
                any(PatchWorkoutRequest.class),
                eq(1L)
        )).thenReturn(response);

        mockMvc.perform(patch("/api/workouts/{id}", 1L)
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2026-10-22"))
                .andExpect(jsonPath("$.notes").value("Тестовая тренировка"));

        verify(workoutService).updateWorkout(
                eq(1L),
                any(PatchWorkoutRequest.class),
                eq(1L)
        );
    }

    @Test
    @DisplayName("Should return 400 when PATCH notes are too long")
    void patchWorkout_NotesTooLong_Returns400() throws Exception {
        String tooLongNotes = "a".repeat(256);

        String requestJson = """
            {
              "notes": "%s"
            }
            """.formatted(tooLongNotes);

        mockMvc.perform(patch("/api/workouts/{id}", 1L)
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
    }

    @Test
    @DisplayName("Should return 404 when PATCH target does not exist")
    void patchWorkout_NotFound_Returns404() throws Exception {
        String requestJson = """
            {
              "notes": "Новая заметка"
            }
            """;

        when(workoutService.updateWorkout(
                anyLong(),
                any(PatchWorkoutRequest.class),
                anyLong()
        )).thenThrow(new WorkoutNotFoundException());

        mockMvc.perform(patch("/api/workouts/{id}", 999L)
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("WORKOUT_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Тренировка не найдена"));
    }

    @Test
    @DisplayName("Should accept null notes in PATCH request")
    void patchWorkout_NullNotes_Returns200() throws Exception {
        String requestJson = """
            {
              "notes": null
            }
            """;

        WorkoutResponse response = createWorkoutResponse();
        response.setNotes("");

        when(workoutService.updateWorkout(
                eq(1L),
                any(PatchWorkoutRequest.class),
                eq(1L)
        )).thenReturn(response);

        mockMvc.perform(patch("/api/workouts/{id}", 1L)
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notes").value(""));

        verify(workoutService).updateWorkout(
                eq(1L),
                argThat(request ->
                        request.areNotesProvided()
                                && request.getNotes() == null
                ), eq(1L));
    }

    @Test
    @DisplayName("Should update only date when notes is absent")
    void patchWorkout_OnlyDate_Returns200() throws Exception {
        String requestJson = """
            {
              "date": "2026-11-02"
            }
            """;

        WorkoutResponse response = createWorkoutResponse();
        response.setDate(LocalDate.of(2026, Month.NOVEMBER, 2));

        when(workoutService.updateWorkout(
                eq(1L),
                argThat(request ->
                        request.getDate().equals(LocalDate.of(2026, 11, 2))
                                && !request.areNotesProvided()
                ), eq(1L)
        )).thenReturn(response);

        mockMvc.perform(patch("/api/workouts/{id}", 1L)
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2026-11-02"))
                .andExpect(jsonPath("$.notes").value("Тестовая тренировка"));
    }

    @Test
    @DisplayName("Should delete a workout")
    void deleteWorkout_Returns204() throws Exception {
        mockMvc.perform(delete("/api/workouts/{id}", 1L)
                        .with(authentication(authentication))
)
                .andExpect(status().isNoContent());

        verify(workoutService).deleteWorkout(1L, 1L);
    }

    @Test
    @DisplayName("Should return 400 when deleting workout with invalid ID")
    void deleteWorkout_InvalidId_Returns400() throws Exception {
        mockMvc.perform(delete("/api/workouts/{id}", "abc")
                        .with(authentication(authentication)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("WRONG_ID"));
    }

    @Test
    @DisplayName("Should return 404 when deleting nonexistent workout")
    void deleteWorkout_NotFound_Returns404() throws Exception {
        doThrow(new WorkoutNotFoundException())
                .when(workoutService)
                .deleteWorkout(999L, 1L);

        mockMvc.perform(delete("/api/workouts/{id}", 999L)
                        .with(authentication(authentication)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("WORKOUT_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Тренировка не найдена"));
    }



    @Test
    @DisplayName("Should return all workouts")
    void findAll_Returns200() throws Exception {
        ShortWorkoutResponse first = new ShortWorkoutResponse();
        first.setId(1L);
        first.setDate(LocalDate.of(2026, 10, 22));

        ShortWorkoutResponse second = new ShortWorkoutResponse();
        second.setId(2L);
        second.setDate(LocalDate.of(2026, 11, 2));

        when(workoutService.findAll(anyLong()))
                .thenReturn(List.of(first, second));

        mockMvc.perform(get("/api/workouts")
                        .with(authentication(authentication))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].date").value("2026-10-22"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].date").value("2026-11-02"));
    }

    @Test
    @DisplayName("Should replace workout and return 200")
    void replaceWorkout_Returns200() throws Exception {
        String requestJson = """
            {
              "date": "2026-11-02",
              "notes": "Полностью обновлённая тренировка",
              "workoutExerciseRequestList": [
                {
                  "exerciseId": 2,
                  "orderNumber": 1,
                  "exerciseSetRequestList": [
                    {
                      "weight": 120,
                      "reps": 5,
                      "rir": "2",
                      "orderNumber": 1
                    }
                  ]
                }
              ]
            }
            """;

        WorkoutResponse response = createWorkoutResponse();
        response.setDate(LocalDate.of(2026, 11, 2));
        response.setNotes("Полностью обновлённая тренировка");

        when(workoutService.replaceWorkout(
                eq(1L),
                any(CreateWorkoutRequest.class),
                eq(1L)
        )).thenReturn(response);

        mockMvc.perform(put("/api/workouts/{id}", 1L)
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.date").value("2026-11-02"))
                .andExpect(jsonPath("$.notes").value("Полностью обновлённая тренировка"));

        verify(workoutService).replaceWorkout(
                eq(1L),
                any(CreateWorkoutRequest.class),
                eq(1L)
        );
    }

}
