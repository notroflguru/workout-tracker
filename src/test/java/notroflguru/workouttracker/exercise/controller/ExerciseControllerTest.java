package notroflguru.workouttracker.exercise.controller;

import notroflguru.workouttracker.exercise.dto.request.CreateExerciseRequest;
import notroflguru.workouttracker.exercise.dto.response.ExerciseResponse;
import notroflguru.workouttracker.exercise.model.Muscle;
import notroflguru.workouttracker.exercise.exception.ExerciseAlreadyExistsException;
import notroflguru.workouttracker.exercise.exception.ExerciseNotFoundException;
import notroflguru.workouttracker.exercise.service.ExerciseService;
import notroflguru.workouttracker.user.security.JwtCore;
import notroflguru.workouttracker.user.service.UserDetailsService;
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

import java.util.Set;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ExerciseController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class ExerciseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExerciseService exerciseService;

    @MockitoBean
    private JwtCore jwtCore;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private ExerciseResponse mockResponse() {
        ExerciseResponse mockResponse = new ExerciseResponse();
        mockResponse.setId(1L);
        mockResponse.setName("Bench press");
        mockResponse.setDescription("Bench press with a barbell");
        mockResponse.setMuscleList(Set.of(Muscle.CHEST, Muscle.TRICEPS, Muscle.FRONT_DELTS));
        return mockResponse;
    }

    @Test
    @DisplayName("POST Успешное создание 201")
    void createExercise_Created_Returns201() throws Exception {
        String requestJson = """
                {
                  "name" : "Bench press",
                  "description" : "Bench press with a barbell",
                  "muscleList" : ["CHEST","TRICEPS", "FRONT_DELTS"]
                }
                """;

        ExerciseResponse mockResponse = mockResponse();

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
                .andExpect(jsonPath("$.muscleList[0]").value("CHEST"))
                .andExpect(jsonPath("$.muscleList[1]").value("TRICEPS"))
                .andExpect(jsonPath("$.muscleList[2]").value("FRONT_DELTS"));
    }

    @Test
    @DisplayName("POST Слишком короткое название : 400")
    void whenNameIsTooShort_BadRequest_Returns400() throws Exception {
        String requestJson = """
                {
                  "name" : "Ben",
                  "description" : "Bench press with a barbell",
                  "muscleList" : ["CHEST","TRICEPS", "FRONT_DELTS"]
                }
                """;

        ExerciseResponse mockResponse = mockResponse();
        mockResponse.setName("Ben");

        mockMvc.perform(post("/api/exercise")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("Request validation failed"));
    }

    @Test
    @DisplayName("POST Пустое название : 400")
    void whenNameIsBlank_BadRequest_Returns400() throws Exception {
        String requestJson = """
                {
                  "name" : "",
                  "description" : "Bench press with a barbell",
                  "muscleList" : ["CHEST","TRICEPS", "FRONT_DELTS"]
                }
                """;

        ExerciseResponse mockResponse = mockResponse();
        mockResponse.setName("");


        mockMvc.perform(post("/api/exercise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("Request validation failed"));
    }

    @Test
    @DisplayName("POST Слишком длинное название : 400") // > 63 символов
    void whenNameIsTooLong_BadRequest_Returns400() throws Exception {
        String requestJson = """
                {
                  "name" : "123456123456123456123456123456123456123456123456123456123456123456",
                  "description" : "Bench press with a barbell",
                  "muscleList" : ["CHEST","TRICEPS", "FRONT_DELTS"]
                }
                """;

        ExerciseResponse mockResponse = mockResponse();
        mockResponse.setName("123456123456123456123456123456123456123456123456123456123456123456");

        mockMvc.perform(post("/api/exercise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("Request validation failed"));
    }

    @Test
    @DisplayName("POST Пустое описание : 400")
    void whenDescriptionIsEmpty_BadRequest_Returns400() throws Exception {
        String requestJson = """
                {
                  "name" : "Bench press",
                  "description" : "",
                  "muscleList" : ["CHEST","TRICEPS", "FRONT_DELTS"]
                }
                """;

        ExerciseResponse mockResponse = mockResponse();
        mockResponse.setDescription("");

        mockMvc.perform(post("/api/exercise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("Request validation failed"));
    }

    @Test
    @DisplayName("POST Слишком короткое описание : 400")
    void whenDescriptionIsTooShort_BadRequest_Returns400() throws Exception {
        String requestJson = """
                {
                  "name" : "Bench press",
                  "description" : "Ben",
                  "muscleList" : ["CHEST","TRICEPS", "FRONT_DELTS"]
                }
                """;

        ExerciseResponse mockResponse = mockResponse();
        mockResponse.setDescription("Ben");

        mockMvc.perform(post("/api/exercise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("Request validation failed"));
    }

    @Test
    @DisplayName("POST Слишком длинное описание : 400") // >255 символов
    void whenDescriptionIsTooLong_BadRequest_Returns400() throws Exception {
        String requestJson = """
                {
                  "name" : "Bench press",
                  "description" : "Слишком длинное описаниеСлишком длинное описаниеСлишком длинное описаниеСлишком длинное описаниеСлишком длинное описаниеСлишком длинное описаниеСлишком длинное описаниеСлишком длинное описаниеСлишком длинное описаниеСлишком длинное описаниеСлишком длинное описание",
                  "muscleList" : ["CHEST","TRICEPS", "FRONT_DELTS"]
                }
                """;

        ExerciseResponse mockResponse = mockResponse();
        mockResponse.setDescription("Слишком длинное описаниеСлишком длинное описаниеСлишком длинное описаниеСлишком длинное описаниеСлишком длинное описание" +
                "Слишком длинное описаниеСлишком длинное описаниеСлишком длинное описаниеСлишком длинное описаниеСлишком длинное описаниеСлишком длинное описание");

        mockMvc.perform(post("/api/exercise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("Request validation failed"));
    }

    @Test
    @DisplayName("POST Пустой список мышц : 400")
    void whenMuscleListIsEmpty_BadRequest_Returns400() throws Exception {
        String requestJson = """
                {
                  "name" : "Bench press",
                  "description" : "Bench press with a barbell",
                  "muscleList" : []
                }
                """;

        ExerciseResponse mockResponse = mockResponse();
        mockResponse.setMuscleList(Set.of());

        Mockito.when(exerciseService.createExercise(Mockito.any(CreateExerciseRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/api/exercise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("Request validation failed"));
    }

    @Test
    @DisplayName("POST Слишком большой список мышц : 400")
    void whenMuscleListIsTooBig_BadRequest_Returns400() throws Exception {
        String requestJson = """
                {
                  "name" : "Bench press",
                  "description" : "Bench press with a barbell",
                  "muscleList" : ["CHEST","TRICEPS", "FRONT_DELTS", "BICEPS", "GLUTES", "ABS"]
                }
                """;

        ExerciseResponse mockResponse = mockResponse();
        mockResponse.setMuscleList(Set.of(Muscle.CHEST, Muscle.TRICEPS, Muscle.FRONT_DELTS, Muscle.BICEPS, Muscle.GLUTES, Muscle.ABS));

        Mockito.when(exerciseService.createExercise(Mockito.any(CreateExerciseRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/api/exercise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("Request validation failed"));
    }

    @Test
    @DisplayName("POST Уже существующее 409")
    void whenExerciseAlreadyExists_Conflict_Returns409() throws Exception {
        String requestJson = """
                {
                  "name" : "Bench press",
                  "description" : "Bench press with a barbell",
                  "muscleList" : ["CHEST","TRICEPS", "FRONT_DELTS"]
                }
                """;

        Mockito.when(exerciseService.createExercise(Mockito.any(CreateExerciseRequest.class)))
                .thenThrow(new ExerciseAlreadyExistsException());

        mockMvc.perform(post("/api/exercise")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EXERCISE_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value("Упражнение с таким названием уже существует"));

    }

    @Test
    @DisplayName("GET Успешный поиск по name : 200")
    void whenExerciseExistsByName_Returns200() throws Exception {

        String exerciseName = "Bench press";

        ExerciseResponse mockResponse = mockResponse();

        Mockito.when(exerciseService.findByName(eq(exerciseName)))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/exercise/name/{name}", exerciseName)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Bench press"))
                .andExpect(jsonPath("$.description").value("Bench press with a barbell"))
                .andExpect(jsonPath("$.muscleList[0]").value("CHEST"))
                .andExpect(jsonPath("$.muscleList[1]").value("TRICEPS"))
                .andExpect(jsonPath("$.muscleList[2]").value("FRONT_DELTS"));
    }

    @Test
    @DisplayName("GET Успешный поиск по id : 200")
    void whenExerciseExistsById_Returns200() throws Exception {

        Long exerciseId = 1L;

        ExerciseResponse mockResponse = mockResponse();

        Mockito.when(exerciseService.findById(eq(exerciseId)))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/exercise/{id}", exerciseId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Bench press"))
                .andExpect(jsonPath("$.description").value("Bench press with a barbell"))
                .andExpect(jsonPath("$.muscleList[0]").value("CHEST"))
                .andExpect(jsonPath("$.muscleList[1]").value("TRICEPS"))
                .andExpect(jsonPath("$.muscleList[2]").value("FRONT_DELTS"));
    }

    @Test
    @DisplayName("GET Неуспешный поиск по неверному id : 404")
    void whenExerciseIdIsWrong_BadRequest_Returns400() throws Exception {

        String nonExistingId = "ABCD";

        mockMvc.perform(get("/api/exercise/{id}", nonExistingId))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("WRONG_ID"))
                .andExpect(jsonPath("$.message").value("Could not resolve id"));
    }

    @Test
    @DisplayName("GET Неуспешный поиск по name : 404")
    void whenExerciseDoesNotExistByName_NotFound_Returns404() throws Exception {

        String nonExistingName = "Unknown_Exercise";

        Mockito.when(exerciseService.findByName(eq(nonExistingName)))
                .thenThrow(new ExerciseNotFoundException());

        mockMvc.perform(get("/api/exercise/name/{name}", nonExistingName))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("EXERCISE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Упражнение не найдено"));
    }

    @Test
    @DisplayName("GET Неуспешный поиск по id : 404")
    void whenExerciseDoesNotExistById_NotFound_Returns404() throws Exception {

        Long nonExistingId = 999L;

        Mockito.when(exerciseService.findById(Mockito.any(Long.class)))
                .thenThrow(new ExerciseNotFoundException());

        mockMvc.perform(get("/api/exercise/{id}", nonExistingId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("EXERCISE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Упражнение не найдено"));
    }
}
