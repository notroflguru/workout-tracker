package notroflguru.workouttracker.exercise.service;

import notroflguru.workouttracker.exercise.dto.CreateExerciseRequest;
import notroflguru.workouttracker.exercise.dto.ExerciseResponse;
import notroflguru.workouttracker.exercise.dto.Muscle;
import notroflguru.workouttracker.exercise.exception.ExerciseAlreadyExistsException;
import notroflguru.workouttracker.exercise.exception.ExerciseNotFoundException;
import notroflguru.workouttracker.exercise.repository.ExerciseRepository;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class ExerciseServiceTest {

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
    private ExerciseService exerciseService;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @BeforeEach
    void setUp() {
        exerciseRepository.deleteAll();
    }

    // Успешное создание упражнения
    @Test
    @DisplayName("Should successfully create an exercise")
    void createExercise_ReturnsCreatedExercise() {
        CreateExerciseRequest request = createExerciseRequest("Bench press");

        ExerciseResponse response = exerciseService.createExercise(request);

        assertEquals("Bench press", response.getName());
        assertEquals("Bench press with a barbell", response.getDescription());
        assertEquals(List.of(Muscle.Chest, Muscle.Triceps), response.getMuscleList());
    }

    // Успешный поиск упражнения по имени игнорируя регистр
    @Test
    @DisplayName("Should find an exercise by name ignoring case")
    void findExerciseByName_IgnoresCase() {
        exerciseService.createExercise(createExerciseRequest("Bench press"));

        ExerciseResponse response = exerciseService.findByName("bEnCh PrEsS");

        assertEquals("Bench press", response.getName());
    }


    // Успешный поиск упражнения по идентификатору
    @Test
    @DisplayName("Should find an exercise by ID")
    void findExerciseById_ReturnsExercise() {
        ExerciseResponse createdExercise = exerciseService.createExercise(createExerciseRequest("Bench press"));

        ExerciseResponse response = exerciseService.findById(createdExercise.getId());

        assertEquals(createdExercise.getId(), response.getId());
        assertEquals("Bench press", response.getName());
    }

    // Выброс исключения при поиске несуществующего упражнения
    @Test
    @DisplayName("Should throw an exception when an exercise does not exist")
    void findExerciseByName_WhenExerciseDoesNotExist_ThrowsException() {
        assertThrows(
                ExerciseNotFoundException.class,
                () -> exerciseService.findByName("Unknown exercise")
        );
    }

    // Выброс исключения при создании уже существующего упражнения (имя - уникальное)
    @Test
    @DisplayName("Should throw an exception when an exercise already exists")
    void createExercise_WhenExerciseAlreadyExists_ThrowsException() {
        exerciseService.createExercise(createExerciseRequest("Bench press"));

        assertThrows(
                ExerciseAlreadyExistsException.class,
                () -> exerciseService.createExercise(createExerciseRequest("bEnCh PrEsS"))
        );
    }

    private CreateExerciseRequest createExerciseRequest(String name) {
        CreateExerciseRequest request = new CreateExerciseRequest();
        request.setName(name);
        request.setDescription("Bench press with a barbell");
        request.setMuscleList(List.of(Muscle.Chest, Muscle.Triceps));
        return request;
    }
}
