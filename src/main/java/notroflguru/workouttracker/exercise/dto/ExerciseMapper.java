package notroflguru.workouttracker.exercise.dto;

import notroflguru.workouttracker.exercise.dto.request.CreateExerciseRequest;
import notroflguru.workouttracker.exercise.dto.response.ExerciseResponse;
import notroflguru.workouttracker.exercise.dto.response.ExerciseShortResponse;
import notroflguru.workouttracker.exercise.model.Exercise;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExerciseMapper {
    public ExerciseResponse toResponse(Exercise exercise) {
        ExerciseResponse response = new ExerciseResponse();
        response.setId(exercise.getId());
        response.setName(exercise.getName());
        response.setDescription(exercise.getDescription());
        response.setMuscleList(exercise.getMuscleList());
        return response;
    }

    public List<ExerciseShortResponse> toShortResponseList(List<Exercise> exerciseList) {
        return exerciseList.stream().map(this::toShortResponse).toList();
    }

    public Exercise toEntity(CreateExerciseRequest request) {
        Exercise entity = new Exercise();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setMuscleList(request.getMuscleList());
        return entity;
    }

    public ExerciseShortResponse toShortResponse(Exercise exercise) {
        ExerciseShortResponse response = new ExerciseShortResponse();
        response.setId(exercise.getId());
        response.setName(exercise.getName());
        response.setMuscleList(exercise.getMuscleList());
        return response;
    }
}
