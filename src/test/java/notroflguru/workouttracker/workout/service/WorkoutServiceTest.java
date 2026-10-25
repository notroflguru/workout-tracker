package notroflguru.workouttracker.workout.service;

import jakarta.persistence.EntityManager;
import notroflguru.workouttracker.exercise.dto.Muscle;
import notroflguru.workouttracker.exercise.exception.ExerciseNotFoundException;
import notroflguru.workouttracker.exercise.model.Exercise;
import notroflguru.workouttracker.exercise.repository.ExerciseRepository;
import notroflguru.workouttracker.workout.dto.request.CreateExerciseSetRequest;
import notroflguru.workouttracker.workout.dto.request.CreateWorkoutExerciseRequest;
import notroflguru.workouttracker.workout.dto.request.CreateWorkoutRequest;
import notroflguru.workouttracker.workout.dto.response.ExerciseSetResponse;
import notroflguru.workouttracker.workout.dto.response.WorkoutExerciseResponse;
import notroflguru.workouttracker.workout.dto.response.WorkoutResponse;
import notroflguru.workouttracker.workout.repository.ExerciseSetRepository;
import notroflguru.workouttracker.workout.repository.WorkoutExerciseRepository;
import notroflguru.workouttracker.workout.repository.WorkoutRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
public class WorkoutServiceTest {

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
    private WorkoutService workoutService;

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private WorkoutExerciseRepository workoutExerciseRepository;

    @Autowired
    private ExerciseSetRepository exerciseSetRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Exercise exercise1;
    private Exercise exercise2;

    @BeforeEach
    void setUp() {
        workoutRepository.deleteAll();
        exerciseRepository.deleteAll();
        exercise1 = new Exercise();
        exercise1.setName("Squat");
        exercise1.setDescription("Squat with a barbell");
        exercise1.setMuscleList(List.of(Muscle.Quadriceps, Muscle.Hamstrings, Muscle.Glutes));
        exerciseRepository.save(exercise1);
        exercise2 = new Exercise();
        exercise2.setName("Deadlift");
        exercise2.setDescription("");
        exercise2.setMuscleList(List.of(Muscle.Hamstrings, Muscle.Glutes));
        exerciseRepository.save(exercise2);
    }

    // Успешное создание тренировки с каскадной записью в соответствующие таблицы
    @Test
    @DisplayName("Should successfully create a workout")
    void createWorkout_ReturnsCreatedWorkout() {
        CreateWorkoutRequest request = createWorkoutRequest();

        WorkoutResponse response = workoutService.createWorkout(request);

        assertEquals(LocalDate.now(), response.getDate());
        assertEquals("Первая тренировка", response.getNotes());
        assertEquals(1, workoutRepository.count());
        assertEquals(2, workoutExerciseRepository.count());
        assertEquals(4, exerciseSetRepository.count());

        WorkoutExerciseResponse workoutExerciseResponse1 = response.getWorkoutExerciseResponseList().get(0);
        assertEquals(exercise1.getId(), workoutExerciseResponse1.getExerciseId());
        assertEquals("Squat", workoutExerciseResponse1.getExerciseName());
        assertEquals(1, workoutExerciseResponse1.getOrderNumber());

        ExerciseSetResponse exerciseSetResponse1 = workoutExerciseResponse1.getExerciseSetResponseList().get(0);
        assertEquals(1, exerciseSetResponse1.getOrderNumber());
        assertEquals("2-3", exerciseSetResponse1.getRir());
        assertEquals(5, exerciseSetResponse1.getReps());
        assertEquals(BigDecimal.valueOf(120.0), exerciseSetResponse1.getWeight());

        ExerciseSetResponse exerciseSetResponse2 = workoutExerciseResponse1.getExerciseSetResponseList().get(1);
        assertEquals(2, exerciseSetResponse2.getOrderNumber());
        assertEquals("1", exerciseSetResponse2.getRir());
        assertEquals(3, exerciseSetResponse2.getReps());
        assertEquals(BigDecimal.valueOf(125.0), exerciseSetResponse2.getWeight());

        WorkoutExerciseResponse workoutExerciseResponse2 = response.getWorkoutExerciseResponseList().get(1);
        assertEquals(exercise2.getId(), workoutExerciseResponse2.getExerciseId());
        assertEquals("Deadlift", workoutExerciseResponse2.getExerciseName());
        assertEquals(2, workoutExerciseResponse2.getOrderNumber());

        ExerciseSetResponse exerciseSetResponse3 = workoutExerciseResponse2.getExerciseSetResponseList().get(0);
        assertEquals(1, exerciseSetResponse3.getOrderNumber());
        assertEquals("5-6", exerciseSetResponse3.getRir());
        assertEquals(5, exerciseSetResponse3.getReps());
        assertEquals(BigDecimal.valueOf(160.0), exerciseSetResponse3.getWeight());

        ExerciseSetResponse exerciseSetResponse4 = workoutExerciseResponse2.getExerciseSetResponseList().get(1);
        assertEquals(2, exerciseSetResponse4.getOrderNumber());
        assertEquals("2-3", exerciseSetResponse4.getRir());
        assertEquals(3, exerciseSetResponse4.getReps());
        assertEquals(BigDecimal.valueOf(180.0), exerciseSetResponse4.getWeight());
    }


    // Выброс исключения при несуществующем упражнении + откат транзакции
    @Test
    @DisplayName("Should throw an exception when an exercise does not exist")
    void createWorkout_WhenExerciseDoesNotExist_ThrowsException() {
        CreateWorkoutRequest request = createWorkoutRequest();
        request.getWorkoutExerciseRequestList()
                .get(1)
                .setExerciseId(999L);

        assertThrows(
                ExerciseNotFoundException.class,
                () -> workoutService.createWorkout(request)
        );

        assertEquals(0, workoutRepository.count());
        assertEquals(0, workoutExerciseRepository.count());
        assertEquals(0, exerciseSetRepository.count());
    }

    // Проверка, что после сохранения данные действительно читаются из PostgreSQL
    @Test
    @DisplayName("Should read saved workout data from PostgreSQL")
    void createWorkout_ShouldReadSavedDataFromPostgres() {

        CreateWorkoutRequest request = createWorkoutRequest();

        workoutService.createWorkout(request);

        // Очищаем persistence context, чтобы не читать сущности из кэша JPA
        entityManager.clear();

        assertEquals(1, workoutRepository.count());
        assertEquals(2, workoutExerciseRepository.count());
        assertEquals(4, exerciseSetRepository.count());

        List<Map<String, Object>> workouts =
                jdbcTemplate.queryForList("SELECT * FROM workout");

        assertEquals(1, workouts.size());
        assertEquals("Первая тренировка", workouts.get(0).get("notes"));
        assertEquals(
                LocalDate.now(),
                ((java.sql.Date) workouts.get(0).get("date")).toLocalDate()
        );

        List<Map<String, Object>> workoutExercises =
                jdbcTemplate.queryForList("SELECT * FROM workout_exercise");

        assertEquals(2, workoutExercises.size());

        List<Map<String, Object>> exerciseSets =
                jdbcTemplate.queryForList(
                        "SELECT * FROM exercise_set ORDER BY order_number"
                );

        assertEquals(4, exerciseSets.size());
    }

    private CreateWorkoutRequest createWorkoutRequest() {

        CreateExerciseSetRequest exerciseSetRequest1 = createExerciseSetRequest(120.0, 5, "2-3", 1);
        CreateExerciseSetRequest exerciseSetRequest2 = createExerciseSetRequest(125.0, 3, "1", 2);
        CreateExerciseSetRequest exerciseSetRequest3 = createExerciseSetRequest(160.0, 5, "5-6", 1);
        CreateExerciseSetRequest exerciseSetRequest4 = createExerciseSetRequest(180.0, 3, "2-3", 2);


        List<CreateExerciseSetRequest> exerciseSetRequestList1 = new ArrayList<>();
        exerciseSetRequestList1.add(exerciseSetRequest1);
        exerciseSetRequestList1.add(exerciseSetRequest2);

        List<CreateExerciseSetRequest> exerciseSetRequestList2 = new ArrayList<>();
        exerciseSetRequestList2.add(exerciseSetRequest3);
        exerciseSetRequestList2.add(exerciseSetRequest4);

        CreateWorkoutExerciseRequest workoutExerciseRequest1 = createWorkoutExerciseRequest(exerciseSetRequestList1, exercise1.getId(), 1);
        CreateWorkoutExerciseRequest workoutExerciseRequest2 = createWorkoutExerciseRequest(exerciseSetRequestList2, exercise2.getId(), 2);


        List<CreateWorkoutExerciseRequest> workoutExerciseRequestList = new ArrayList<>();
        workoutExerciseRequestList.add(workoutExerciseRequest1);
        workoutExerciseRequestList.add(workoutExerciseRequest2);

        CreateWorkoutRequest request = new CreateWorkoutRequest();
        request.setDate(LocalDate.now());
        request.setNotes("Первая тренировка");
        request.setWorkoutExerciseRequestList(workoutExerciseRequestList);

        return request;
    }

    private CreateExerciseSetRequest createExerciseSetRequest(double weight, int reps, String rir, int orderNumber) {

        CreateExerciseSetRequest request = new CreateExerciseSetRequest();

        request.setWeight(BigDecimal.valueOf(weight));
        request.setReps(reps);
        request.setRir(rir);
        request.setOrderNumber(orderNumber);

        return request;
    }

    private CreateWorkoutExerciseRequest createWorkoutExerciseRequest(List<CreateExerciseSetRequest> list, Long id, int orderNumber) {

        CreateWorkoutExerciseRequest request = new CreateWorkoutExerciseRequest();

        request.setExerciseSetRequestList(list);
        request.setExerciseId(id);
        request.setOrderNumber(orderNumber);

        return request;
    }
}
