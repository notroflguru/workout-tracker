package notroflguru.workouttracker.workout.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Объект запроса создания тренировки")
public class CreateWorkoutRequest {
    @NotNull
    @Schema(description = "Дата тренировки", example = "2026-10-29")
    private LocalDate date;

    @Length(max = 255, message = "Длина должна быть не более 255 символов")
    @Schema(description = "Заметка к тренировке", example = "Тяжелая тренировка ног")
    private String notes;

    @NotNull @NotEmpty
    @Schema(description = "Список упражнений, которые были в тренировке")
    private List<@Valid CreateWorkoutExerciseRequest> workoutExerciseRequestList;

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
    }

    public List<CreateWorkoutExerciseRequest> getWorkoutExerciseRequestList() {
        return workoutExerciseRequestList;
    }

    public void setWorkoutExerciseRequestList(List<CreateWorkoutExerciseRequest> workoutExerciseRequestList) {
        this.workoutExerciseRequestList = workoutExerciseRequestList;
    }
}
