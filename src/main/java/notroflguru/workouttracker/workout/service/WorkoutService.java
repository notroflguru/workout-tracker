package notroflguru.workouttracker.workout.service;

import jakarta.transaction.Transactional;
import notroflguru.workouttracker.exercise.exception.ExerciseNotFoundException;
import notroflguru.workouttracker.exercise.model.Exercise;
import notroflguru.workouttracker.exercise.repository.ExerciseRepository;
import notroflguru.workouttracker.workout.dto.request.CreateExerciseSetRequest;
import notroflguru.workouttracker.workout.dto.request.CreateWorkoutExerciseRequest;
import notroflguru.workouttracker.workout.dto.request.CreateWorkoutRequest;
import notroflguru.workouttracker.workout.dto.WorkoutMapper;
import notroflguru.workouttracker.workout.dto.response.WorkoutResponse;
import notroflguru.workouttracker.workout.model.ExerciseSet;
import notroflguru.workouttracker.workout.model.Workout;
import notroflguru.workouttracker.workout.model.WorkoutExercise;
import notroflguru.workouttracker.workout.repository.WorkoutRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    @Transactional
    public WorkoutResponse createWorkout(CreateWorkoutRequest request) {
        Workout entity = mapper.toEntity(request);

        List<WorkoutExercise> workoutExerciseList = new ArrayList<>();

        for (CreateWorkoutExerciseRequest exerciseRequest : request.getWorkoutExerciseRequestList()) {
            Exercise exercise = exerciseRepository.findById(
                    exerciseRequest.getExerciseId())
                            .orElseThrow(() -> new ExerciseNotFoundException("Exercise not found: " + exerciseRequest.getExerciseId())
            );

            WorkoutExercise workoutExercise = mapper.toEntity(exerciseRequest, exercise);

            workoutExercise.setWorkout(entity);
            List<ExerciseSet> exerciseSetList = new ArrayList<>();
            for (CreateExerciseSetRequest setRequest
                    : exerciseRequest.getExerciseSetRequestList()) {

                ExerciseSet exerciseSet = mapper.toEntity(setRequest);
                exerciseSet.setWorkoutExercise(workoutExercise);
                exerciseSetList.add(exerciseSet);
            }

            workoutExercise.setExerciseSetList(exerciseSetList);
            workoutExerciseList.add(workoutExercise);
        }

        entity.setWorkoutExerciseList(workoutExerciseList);

        Workout savedEntity = workoutRepository.save(entity);
        return mapper.toResponse(savedEntity);
    }
}
