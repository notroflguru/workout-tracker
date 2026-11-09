package notroflguru.workouttracker.workout.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import notroflguru.workouttracker.exercise.exception.ExerciseNotFoundException;
import notroflguru.workouttracker.exercise.model.Exercise;
import notroflguru.workouttracker.exercise.repository.ExerciseRepository;
import notroflguru.workouttracker.user.exception.UserNotFoundException;
import notroflguru.workouttracker.user.model.User;
import notroflguru.workouttracker.user.repository.UserRepository;
import notroflguru.workouttracker.workout.dto.WorkoutMapper;
import notroflguru.workouttracker.workout.dto.request.CreateWorkoutExerciseRequest;
import notroflguru.workouttracker.workout.dto.request.CreateWorkoutRequest;
import notroflguru.workouttracker.workout.dto.request.PatchWorkoutRequest;
import notroflguru.workouttracker.workout.dto.response.ShortWorkoutResponse;
import notroflguru.workouttracker.workout.dto.response.WorkoutResponse;
import notroflguru.workouttracker.workout.exception.WorkoutNotFoundException;
import notroflguru.workouttracker.workout.model.Workout;
import notroflguru.workouttracker.workout.model.WorkoutExercise;
import notroflguru.workouttracker.workout.repository.WorkoutRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WorkoutService {

    private final WorkoutMapper mapper;
    private final WorkoutRepository workoutRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;

    public WorkoutService(WorkoutMapper workoutMapper, WorkoutRepository workoutRepository, ExerciseRepository exerciseRepository, UserRepository userRepository) {
        this.mapper = workoutMapper;
        this.workoutRepository = workoutRepository;
        this.exerciseRepository = exerciseRepository;
        this.userRepository = userRepository;
    }

    private void applyRequest(
            Workout workout,
            CreateWorkoutRequest request
    ) {
        workout.setDate(request.getDate());
        workout.setNotes(request.getNotes());
        workout.removeAllWorkoutExercises();

        Map<Long, Exercise> exercisesById = loadExercises(request);

        request.getWorkoutExerciseRequestList().forEach(workoutExerciseRequest -> {
            WorkoutExercise workoutExercise =
                    buildWorkoutExercise(workoutExerciseRequest, exercisesById);

            workout.addWorkoutExercise(workoutExercise);
        });
    }

    private Workout findById(Long id, Long userId) {
        return workoutRepository.findWithExercisesByIdAndUser_Id(id, userId)
                .orElseThrow(WorkoutNotFoundException::new);
    }

    public WorkoutResponse createWorkout(CreateWorkoutRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Workout workout = new Workout();
        workout.setUser(user);

        applyRequest(workout, request);

        Workout savedWorkout = workoutRepository.save(workout);
        return mapper.toResponse(savedWorkout);
    }

    private Map<Long, Exercise> loadExercises(CreateWorkoutRequest request) {
        List<Long> exerciseIds = request.getWorkoutExerciseRequestList()
                .stream()
                .map(CreateWorkoutExerciseRequest::getExerciseId)
                .distinct()
                .toList();

        return exerciseRepository.findAllByIdIn(exerciseIds)
                .stream()
                .collect(Collectors.toMap(
                        Exercise::getId,
                        Function.identity()
                ));
    }

    private WorkoutExercise buildWorkoutExercise(
            CreateWorkoutExerciseRequest request,
            Map<Long, Exercise> exercisesById
    ) {
        Exercise exercise = exercisesById.get(request.getExerciseId());

        if (exercise == null) {
            throw new ExerciseNotFoundException();
        }

        WorkoutExercise workoutExercise =
                mapper.toEntity(request, exercise);

        request.getExerciseSetRequestList()
                .stream()
                .map(mapper::toEntity)
                .forEach(workoutExercise::addExerciseSet);

        return workoutExercise;
    }

    @Transactional
    public WorkoutResponse findWorkoutById(Long id, Long userId) {

        Workout workout = findById(id, userId);

        log.debug("Successfully found workout with ID: {}", id);

        return mapper.toResponse(workout);
    }

    @Transactional // Нужно для автоматического обновления данных в базе данных при изменении значений полей сущности
    public WorkoutResponse updateWorkout(
            Long id,
            PatchWorkoutRequest request,
            Long userId
    ) {
        Workout workout = findById(id, userId);

        if (request.getDate() != null) {
            workout.setDate(request.getDate());
        }

        if (request.areNotesProvided()) {
            workout.setNotes(
                    request.getNotes() == null
                            ? ""
                            : request.getNotes()
            );
        }

        log.info("Successfully patched workout with ID: {}", id);

        return mapper.toResponse(workout);
    }

    @Transactional
    public void deleteWorkout(Long id, Long userId) {

        if (!workoutRepository.existsByIdAndUser_Id(id, userId)) {
            throw new WorkoutNotFoundException();
        }

        log.info("Successfully delete workout with ID: {}", id);

        workoutRepository.deleteByIdAndUser_Id(id, userId);
    }

    public List<ShortWorkoutResponse> findAll(Long userId) {
        List<Workout> workoutList = workoutRepository.findAllByUser_Id(userId);

        return mapper.toShortResponseList(workoutList);
    }

    @Transactional
    public WorkoutResponse replaceWorkout(
            Long id,
            CreateWorkoutRequest request,
            Long userId
    ) {
        Workout workout = findById(id, userId);

        applyRequest(workout, request);

        return mapper.toResponse(workout);
    }
}