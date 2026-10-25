package notroflguru.workouttracker.exercise.controller;

import notroflguru.workouttracker.exercise.dto.CreateExerciseRequest;
import notroflguru.workouttracker.exercise.dto.ExerciseResponse;
import notroflguru.workouttracker.exercise.dto.Muscle;
import notroflguru.workouttracker.exercise.exception.ExerciseAlreadyExistsException;
import notroflguru.workouttracker.exercise.exception.ExerciseNotFoundException;
import notroflguru.workouttracker.exercise.service.ExerciseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ExerciseController.class)
@ActiveProfiles("test")
public class ExerciseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExerciseService exerciseService;

    @Test
    @DisplayName("Should successfully create a new exercise and return 201")
    void createExercise_Created_Returns201() throws Exception {
        String requestJson = """
                {
                  "name" : "Bench press",
                  "description" : "Bench press with a barbell",
                  "muscleList" : ["Chest","Triceps", "Front_delts"]
                }
                """;

        ExerciseResponse mockResponse = new ExerciseResponse();
        mockResponse.setId(1L);
        mockResponse.setName("Bench press");
        mockResponse.setDescription("Bench press with a barbell");
        mockResponse.setMuscleList(List.of(Muscle.Chest, Muscle.Triceps, Muscle.Front_delts));

        Mockito.when(exerciseService.createExercise(Mockito.any(CreateExerciseRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/api/exercise")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Bench press"))
                .andExpect(jsonPath("$.description").value("Bench press with a barbell"))
                .andExpect(jsonPath("$.muscleList[0]").value("Chest"))
                .andExpect(jsonPath("$.muscleList[1]").value("Triceps"))
                .andExpect(jsonPath("$.muscleList[2]").value("Front_delts"));
    }

    @Test
    @DisplayName("Should not create an exercise and return 400")
    void whenInputIsInvalid_BadRequest_Returns400() throws Exception {
        String requestJson = """
                {
                  "name" : "Ben",
                  "description" : "Bench press with a barbell",
                  "muscleList" : ["Chest","Triceps", "Front_delts"]
                }
                """;

        ExerciseResponse mockResponse = new ExerciseResponse();
        mockResponse.setId(1L);
        mockResponse.setName("Ben");
        mockResponse.setDescription("Bench press with a barbell");
        mockResponse.setMuscleList(List.of(Muscle.Chest, Muscle.Triceps, Muscle.Front_delts));

        mockMvc.perform(post("/api/exercise")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("Request validation failed"));
    }

    @Test
    @DisplayName("Should not create an exercise and return 409")
    void whenExerciseAlreadyExists_Conflict_Returns409() throws Exception {
        String requestJson = """
                {
                  "name" : "Bench press",
                  "description" : "Bench press with a barbell",
                  "muscleList" : ["Chest","Triceps", "Front_delts"]
                }
                """;

        Mockito.when(exerciseService.createExercise(Mockito.any(CreateExerciseRequest.class)))
                .thenThrow(new ExerciseAlreadyExistsException("Exercise already exists: Bench press"));

        mockMvc.perform(post("/api/exercise")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EXERCISE_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value("Exercise already exists: Bench press"));

    }

    @Test
    @DisplayName("Should return exercise by name and HTTP 200")
    void whenExerciseExists_Returns200() throws Exception {

        String exerciseName = "Bench press";

        ExerciseResponse mockResponse = new ExerciseResponse();
        mockResponse.setId(1L);
        mockResponse.setName("Bench press");
        mockResponse.setDescription("Bench press with a barbell");
        mockResponse.setMuscleList(List.of(Muscle.Chest, Muscle.Triceps, Muscle.Front_delts));

        Mockito.when(exerciseService.findByName(eq(exerciseName)))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/exercise/name/{name}", exerciseName)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Bench press"))
                .andExpect(jsonPath("$.description").value("Bench press with a barbell"))
                .andExpect(jsonPath("$.muscleList[0]").value("Chest"))
                .andExpect(jsonPath("$.muscleList[1]").value("Triceps"))
                .andExpect(jsonPath("$.muscleList[2]").value("Front_delts"));
    }

    @Test
    @DisplayName("Should not find exercise by name and return 404")
    void whenExerciseDoesNotExistByName_NotFound_Returns404() throws Exception {

        String nonExistingName = "Unknown_Exercise";

        Mockito.when(exerciseService.findByName(eq(nonExistingName)))
                .thenThrow(new ExerciseNotFoundException("Exercise not found with name: " + nonExistingName));

        mockMvc.perform(get("/api/exercise/name/{name}", nonExistingName))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("EXERCISE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Exercise not found with name: Unknown_Exercise"));
    }

    @Test
    @DisplayName("Should not find exercise by id and return 404")
    void whenExerciseDoesNotExistById_NotFound_Returns404() throws Exception {

        Long nonExistingId = 999L;

        Mockito.when(exerciseService.findById(Mockito.any(Long.class)))
                .thenThrow(new ExerciseNotFoundException("Exercise with id " + nonExistingId + " was not found"));

        mockMvc.perform(get("/api/exercise/{id}", nonExistingId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("EXERCISE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Exercise with id 999 was not found"));
    }
}
