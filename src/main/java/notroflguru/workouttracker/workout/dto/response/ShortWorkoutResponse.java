package notroflguru.workouttracker.workout.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@JsonPropertyOrder({"id", "date"})
@Schema(description = "Объект краткого ответа тренировки")
public class ShortWorkoutResponse {

    @Schema(description = "Уникальный идентификатор тренировки", example = "1")
    private Long id;

    @Schema(description = "Дата тренировки", example="2026-10-29")
    private LocalDate date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
