package notroflguru.workouttracker.workout.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public class CreateWorkoutExerciseRequest {
    @NotNull @Positive
    @Schema(description = "Уникальный идентификатор", example = "1")
    private Long exerciseId;

    @Positive
    @Schema(description = "Порядок выполнения упражнения в тренировке", example = "1")
    private int orderNumber;

    @NotNull @NotEmpty
    @Schema(description = "Список подходов в упражнении")
    private List<@Valid CreateExerciseSetRequest> exerciseSetRequestList;

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
