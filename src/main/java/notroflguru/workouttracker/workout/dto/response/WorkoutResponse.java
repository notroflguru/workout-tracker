package notroflguru.workouttracker.workout.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDate;
import java.util.List;

@JsonPropertyOrder({ "id", "date", "notes", "workoutExerciseList"})
public class WorkoutResponse {
    private Long id;
    private LocalDate date;
    private String notes;
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
