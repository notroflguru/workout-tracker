package notroflguru.workouttracker.exercise.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public class CreateExerciseRequest {

    @NotBlank
    @Size(min = 5, message = "name is too short")
    @Size(max = 63, message = "name is too long")
    private String name;

    @Size(min = 5, message = "description is too short")
    @Size(max = 255, message = "description is too long")
    @NotBlank
    private String description;

    @NotEmpty
    @Size(max = 5, message = "too much muscles")
    private List<Muscle> muscleList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Muscle> getMuscleList() {
        return muscleList;
    }

    public void setMuscleList(List<Muscle> muscleList) {
        this.muscleList = muscleList;
    }
}
