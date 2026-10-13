package notroflguru.workouttracker.exercise.dto;

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

    public List<ExerciseResponse> toResponseList(List<Exercise> exerciseList) {
        return exerciseList.stream().map(this::toResponse).toList();
    }

    public Exercise toEntity(CreateExerciseRequest request) {
        Exercise entity = new Exercise();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setMuscleList(request.getMuscleList());
        return entity;
    }
}
