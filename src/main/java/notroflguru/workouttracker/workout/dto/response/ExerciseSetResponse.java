package notroflguru.workouttracker.workout.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@JsonPropertyOrder({ "id", "weight", "reps", "rir", "orderNumber"})
public class ExerciseSetResponse {
    @Schema(description = "Уникальный идентификатор подхода", example = "1")
    private Long id;

    @Schema(description = "Вес в кг", example = "70.0")
    private BigDecimal weight;

    @Schema(description = "Количество повторений", example = "5")
    private int reps;

    @Schema(description = "Количество повторений в запасе", example = "2-3")
    private String rir;

    @Schema(description = "Порядковый номер подхода на упражнении", example = "1")
    private int orderNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
