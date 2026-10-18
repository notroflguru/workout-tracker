package notroflguru.workouttracker.workout.dto.request;

import java.util.List;

public class CreateWorkoutExerciseRequest {
    private Long exerciseId;
    private String exerciseName;
    private int orderNumber;
    private List<CreateExerciseSetRequest> exerciseSetRequestList;

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

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<CreateExerciseSetRequest> getExerciseSetRequestList() {
        return exerciseSetRequestList;
    }

    public void setExerciseSetRequestList(List<CreateExerciseSetRequest> exerciseSetRequestList) {
        this.exerciseSetRequestList = exerciseSetRequestList;
    }
}
