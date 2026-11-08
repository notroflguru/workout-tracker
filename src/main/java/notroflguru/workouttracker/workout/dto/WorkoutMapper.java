package notroflguru.workouttracker.workout.dto;

import notroflguru.workouttracker.exercise.model.Exercise;
import notroflguru.workouttracker.workout.dto.request.CreateExerciseSetRequest;
import notroflguru.workouttracker.workout.dto.request.CreateWorkoutExerciseRequest;
import notroflguru.workouttracker.workout.dto.request.CreateWorkoutRequest;
import notroflguru.workouttracker.workout.dto.response.ExerciseSetResponse;
import notroflguru.workouttracker.workout.dto.response.ShortWorkoutResponse;
import notroflguru.workouttracker.workout.dto.response.WorkoutExerciseResponse;
import notroflguru.workouttracker.workout.dto.response.WorkoutResponse;
import notroflguru.workouttracker.workout.model.ExerciseSet;
import notroflguru.workouttracker.workout.model.Workout;
import notroflguru.workouttracker.workout.model.WorkoutExercise;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkoutMapper {

    public WorkoutExercise toEntity(CreateWorkoutExerciseRequest request, Exercise exercise) {
        WorkoutExercise entity = new WorkoutExercise();

        entity.setExercise(exercise);
        entity.setOrderNumber(request.getOrderNumber());

        return entity;
    }

    public ExerciseSet toEntity(CreateExerciseSetRequest request) {
        ExerciseSet exerciseSet = new ExerciseSet();

        exerciseSet.setWeight(request.getWeight());
        exerciseSet.setReps(request.getReps());
        exerciseSet.setRir(request.getRir());
        exerciseSet.setOrderNumber(request.getOrderNumber());

        return exerciseSet;
    }

    public WorkoutResponse toResponse(Workout entity) {
        WorkoutResponse response = new WorkoutResponse();

        response.setId(entity.getId());
        response.setDate(entity.getDate());
        response.setNotes(entity.getNotes());
        response.setWorkoutExerciseResponseList(
                entity.getWorkoutExerciseList().stream()
                        .map(this::toResponse)
                        .toList()
        );

        return response;
    }

    public WorkoutExerciseResponse toResponse(WorkoutExercise entity) {
        WorkoutExerciseResponse response = new WorkoutExerciseResponse();

        response.setId(entity.getId());
        response.setExerciseId(entity.getExercise().getId());
        response.setExerciseName(entity.getExercise().getName());
        response.setExerciseSetResponseList(
                entity.getExerciseSetList().stream()
                        .map(this::toResponse)
                        .toList());
        response.setOrderNumber(entity.getOrderNumber());

        return response;
    }

    public ExerciseSetResponse toResponse(ExerciseSet entity) {
        ExerciseSetResponse response = new ExerciseSetResponse();

        response.setId(entity.getId());
        response.setWeight(entity.getWeight());
        response.setReps(entity.getReps());
        response.setOrderNumber(entity.getOrderNumber());
        response.setRir(entity.getRir());

        return response;
    }
    public ShortWorkoutResponse toShortResponse(Workout entity) {
        ShortWorkoutResponse response = new ShortWorkoutResponse();

        response.setId(entity.getId());
        response.setDate(entity.getDate());

        return response;
    }

    public List<ShortWorkoutResponse> toShortResponseList(List<Workout> workoutList) {
        return workoutList.stream().map(this::toShortResponse).toList();
    }
}
