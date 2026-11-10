package notroflguru.workouttracker.workout.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@JsonPropertyOrder({ "id", "date", "notes", "workoutExerciseList"})
@Schema(description = "Объект ответа тренировки")
public class WorkoutResponse {
    @Schema(description = "Уникальный идентификатор тренировки", example = "1")
    private Long id;

    @Schema(description = "Дата тренировки", example = "2026-10-29")
    private LocalDate date;

    @Schema(description = "Заметка к тренировке", example = "Тяжелая тренировка ног")
    private String notes;

    @Schema(description = "Список упражнений, которые были в тренировке")
    private List<WorkoutExerciseResponse> workoutExerciseResponseList;

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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<WorkoutExerciseResponse> getWorkoutExerciseResponseList() {
        return workoutExerciseResponseList;
    }

    public void setWorkoutExerciseResponseList(List<WorkoutExerciseResponse> workoutExerciseResponseList) {
        this.workoutExerciseResponseList = workoutExerciseResponseList;
    }
}
