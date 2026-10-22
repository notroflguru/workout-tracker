package notroflguru.workouttracker.workout.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.List;

public class CreateWorkoutRequest {
    @NotNull
    private LocalDate date;

    @Length(max = 255, message = "Длина должна быть не более 255 символов")
    private String notes;

    @NotNull @NotEmpty @Valid
    private List<CreateWorkoutExerciseRequest> workoutExerciseRequestList;

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
