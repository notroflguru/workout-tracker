package notroflguru.workouttracker.workout.controller;

import notroflguru.workouttracker.workout.dto.response.ExerciseSetResponse;
import notroflguru.workouttracker.workout.dto.response.WorkoutExerciseResponse;
import notroflguru.workouttracker.workout.dto.response.WorkoutResponse;
import notroflguru.workouttracker.workout.service.WorkoutService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(WorkoutController.class)
@ActiveProfiles("test")
public class WorkoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkoutService workoutService;

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

        Mockito.when(workoutService.createWorkout(Mockito.any()))
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
}
