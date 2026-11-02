package notroflguru.workouttracker.workout.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import notroflguru.workouttracker.exercise.exception.ExerciseNotFoundException;
import notroflguru.workouttracker.exercise.model.Exercise;
import notroflguru.workouttracker.exercise.repository.ExerciseRepository;
import notroflguru.workouttracker.workout.dto.request.CreateWorkoutExerciseRequest;
import notroflguru.workouttracker.workout.dto.request.CreateWorkoutRequest;
import notroflguru.workouttracker.workout.dto.WorkoutMapper;
import notroflguru.workouttracker.workout.dto.request.PatchWorkoutRequest;
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

    public WorkoutService(WorkoutMapper workoutMapper, WorkoutRepository workoutRepository, ExerciseRepository exerciseRepository) {
        this.mapper = workoutMapper;
        this.workoutRepository = workoutRepository;
        this.exerciseRepository = exerciseRepository;
    }

    private Workout findById(Long id) {
        return workoutRepository.findWithExercisesById(id)
                .orElseThrow(() -> new WorkoutNotFoundException("Workout not found"));
    }

    @Transactional
    public WorkoutResponse createWorkout(CreateWorkoutRequest request) {
        return saveWorkout(new Workout(), request);
    }

    private WorkoutResponse saveWorkout(
            Workout workout,
            CreateWorkoutRequest request
    ) {
        workout.setDate(request.getDate());
        workout.setNotes(request.getNotes());
        workout.removeAllWorkoutExercises();

        Map<Long, Exercise> exercisesById = loadExercises(request);

        request.getWorkoutExerciseRequestList().forEach(workoutExerciseRequest -> {
            WorkoutExercise workoutExercise = buildWorkoutExercise(
                    workoutExerciseRequest,
                    exercisesById
            );

            workout.addWorkoutExercise(workoutExercise);
        });

        Workout savedWorkout = workoutRepository.save(workout);

        log.info("Successfully saved workout with ID: {}", savedWorkout.getId());

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
            throw new ExerciseNotFoundException(
                    "Exercise not found: " + request.getExerciseId()
            );
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
    public WorkoutResponse findWorkoutById(Long id) {

        Workout entity = findById(id);

        log.debug("Successfully found workout with ID: {}", id);

        return mapper.toResponse(entity);
    }

    @Transactional
    public WorkoutResponse updateWorkout(
            Long id,
            PatchWorkoutRequest request
    ) {
        Workout workout = findById(id);

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

        log.info("Successfully patched workout with ID: {}", workout.getId());

        return mapper.toResponse(workout);
    }
}