package notroflguru.workouttracker.workout.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class CreateExerciseSetRequest {

    @Positive
    @Schema(description = "Вес в кг", example = "70.0")
    private BigDecimal weight;

    @Positive
    @Schema(description = "Количество повторений", example = "5")
    private int reps;

    @Pattern(
            regexp = "^(\\d+-\\d+|\\d+)$",
            message = "Строка не соответствует формату диапазон (например, 1-3) или число (например, 2)"
    )
    @Schema(description = "Количество повторений в запасе", example = "2-3")
    private String rir;

    @Positive
    @Schema(description = "Порядковый номер подхода на упражнении", example = "1")
    private int orderNumber;

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public String getRir() {
        return rir;
    }

    public void setRir(String rir) {
        this.rir = rir;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }
}
