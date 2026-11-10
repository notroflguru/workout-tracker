package notroflguru.workouttracker.workout.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Schema(description = "Объект запроса частичного изменения тренировки (даты или заметок_")
public class PatchWorkoutRequest {

    @Schema(description = "Дата тренировки", example = "2026-10-29")
    private LocalDate date;

    @Length(
            max = 255,
            message = "Длина должна быть не более 255 символов"
    )
    @Schema(description = "Заметка к тренировке", example = "Тяжелая тренировка ног")
    private String notes;

    private boolean notesProvided = false;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
        this.notesProvided = true;
    }

    public boolean areNotesProvided() {
        return notesProvided;
    }

}