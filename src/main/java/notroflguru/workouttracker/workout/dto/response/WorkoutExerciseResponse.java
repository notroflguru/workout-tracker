package notroflguru.workouttracker.workout.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({ "id", "exerciseId", "exerciseName", "orderNumber", "exerciseSetResponseList"})
public class WorkoutExerciseResponse {
    private Long id;
    private Long exerciseId;
    private String exerciseName;
    private List<ExerciseSetResponse> exerciseSetResponseList;
    private int orderNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public List<ExerciseSetResponse> getExerciseSetResponseList() {
        return exerciseSetResponseList;
    }

    public void setExerciseSetResponseList(List<ExerciseSetResponse> exerciseSetResponseList) {
        this.exerciseSetResponseList = exerciseSetResponseList;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }
}