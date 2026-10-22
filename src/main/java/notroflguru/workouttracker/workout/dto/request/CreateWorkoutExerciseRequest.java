package notroflguru.workouttracker.workout.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public class CreateWorkoutExerciseRequest {
    @NotNull @Positive
    private Long exerciseId;

    @Positive
    private int orderNumber;

    @NotNull @NotEmpty @Valid
    private List<CreateExerciseSetRequest> exerciseSetRequestList;

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
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
