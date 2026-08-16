package notroflguru.workouttracker.exercise.dto;

import java.util.List;

public class ExerciseResponse {
    private String name;
    private String description;
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
