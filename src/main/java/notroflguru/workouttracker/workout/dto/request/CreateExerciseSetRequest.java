package notroflguru.workouttracker.workout.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class CreateExerciseSetRequest {

    @Positive
    private BigDecimal weight;

    @Positive
    private int reps;

    @Pattern(
            regexp = "^(\\d+-\\d+|\\d+)$", //
            message = "Строка не соответствует формату диапазон (например, 1-3) или число (например, 2)"
    )
    private String rir;

    @Positive
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
