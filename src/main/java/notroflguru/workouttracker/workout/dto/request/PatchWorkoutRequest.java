package notroflguru.workouttracker.workout.dto.request;

import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

public class PatchWorkoutRequest {

    private LocalDate date;

    @Length(
            max = 255,
            message = "Длина должна быть не более 255 символов"
    )
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