package notroflguru.workouttracker.workout.controller;

import notroflguru.workouttracker.user.security.JwtAuthenticationFilter;
import notroflguru.workouttracker.user.security.JwtCore;
import notroflguru.workouttracker.user.service.UserDetailsService;
import notroflguru.workouttracker.workout.dto.request.CreateWorkoutRequest;
import notroflguru.workouttracker.workout.dto.request.PatchWorkoutRequest;
import notroflguru.workouttracker.workout.dto.response.ExerciseSetResponse;
import notroflguru.workouttracker.workout.dto.response.ShortWorkoutResponse;
import notroflguru.workouttracker.workout.dto.response.WorkoutExerciseResponse;
import notroflguru.workouttracker.workout.dto.response.WorkoutResponse;
import notroflguru.workouttracker.workout.service.WorkoutService;
import notroflguru.workouttracker.workout.exception.WorkoutNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(WorkoutController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class WorkoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkoutService workoutService;

    @MockitoBean
    private JwtCore jwtCore;

    @MockitoBean
    private UserDetailsService userDetailsService;

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

        when(workoutService.createWorkout(Mockito.any()))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/api/workout")
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

        mockMvc.perform(post("/api/workout")
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

        mockMvc.perform(post("/api/workout")
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

        mockMvc.perform(post("/api/workout")
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

        mockMvc.perform(post("/api/workout")
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

        mockMvc.perform(post("/api/workout")
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

        mockMvc.perform(post("/api/workout")
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

        mockMvc.perform(post("/api/workout")
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

        mockMvc.perform(post("/api/workout")
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

        when(workoutService.findWorkoutById(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/workout/{id}", 1L)
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
        when(workoutService.findWorkoutById(999L))
                .thenThrow(new WorkoutNotFoundException(""));

        mockMvc.perform(get("/api/workout/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("WORKOUT_NOT_FOUND"));
    }

    @Test
    @DisplayName("Should return 400 when workout ID is invalid")
    void findWorkoutById_InvalidId_Returns400() throws Exception {
        mockMvc.perform(get("/api/workout/{id}", "abc"))
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
                any(notroflguru.workouttracker.workout.dto.request.PatchWorkoutRequest.class)
        )).thenReturn(response);

        mockMvc.perform(patch("/api/workout/{id}", 1L)
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
                any(PatchWorkoutRequest.class)
        );
    }

    @Test
    @DisplayName("Should accept empty PATCH body without changing workout")
    void patchWorkout_EmptyBody_Returns200() throws Exception {
        WorkoutResponse response = createWorkoutResponse();

        when(workoutService.updateWorkout(
                eq(1L),
                any(PatchWorkoutRequest.class)
        )).thenReturn(response);

        mockMvc.perform(patch("/api/workout/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2026-10-22"))
                .andExpect(jsonPath("$.notes").value("Тестовая тренировка"));

        verify(workoutService).updateWorkout(
                eq(1L),
                any(PatchWorkoutRequest.class)
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

        mockMvc.perform(patch("/api/workout/{id}", 1L)
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
                any(PatchWorkoutRequest.class)
        )).thenThrow(new WorkoutNotFoundException("Workout not found"));

        mockMvc.perform(patch("/api/workout/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("WORKOUT_NOT_FOUND"));
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
                any(PatchWorkoutRequest.class)
        )).thenReturn(response);

        mockMvc.perform(patch("/api/workout/{id}", 1L)
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
                ));
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
                )
        )).thenReturn(response);

        mockMvc.perform(patch("/api/workout/{id}", 1L)
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
        mockMvc.perform(delete("/api/workout/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(workoutService).deleteWorkout(1L);
    }

    @Test
    @DisplayName("Should return 400 when deleting workout with invalid ID")
    void deleteWorkout_InvalidId_Returns400() throws Exception {
        mockMvc.perform(delete("/api/workout/{id}", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("WRONG_ID"));
    }

    @Test
    @DisplayName("Should return 404 when deleting nonexistent workout")
    void deleteWorkout_NotFound_Returns404() throws Exception {
        doThrow(new WorkoutNotFoundException("Workout not found"))
                .when(workoutService)
                .deleteWorkout(999L);

        mockMvc.perform(delete("/api/workout/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("WORKOUT_NOT_FOUND"));
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

        when(workoutService.findAll())
                .thenReturn(List.of(first, second));

        mockMvc.perform(get("/api/workout/all")
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
                any(CreateWorkoutRequest.class)
        )).thenReturn(response);

        mockMvc.perform(put("/api/workout/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.date").value("2026-11-02"))
                .andExpect(jsonPath("$.notes").value("Полностью обновлённая тренировка"));

        verify(workoutService).replaceWorkout(
                eq(1L),
                any(CreateWorkoutRequest.class)
        );
    }

}
