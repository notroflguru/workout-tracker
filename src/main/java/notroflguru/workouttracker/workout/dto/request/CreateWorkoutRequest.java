package notroflguru.workouttracker.workout.dto.request;

import notroflguru.workouttracker.workout.model.WorkoutExercise;

import java.time.LocalDate;
import java.util.List;

public class CreateWorkoutRequest {
    private LocalDate date;
    private String notes;
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
