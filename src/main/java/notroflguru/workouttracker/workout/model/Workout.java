package notroflguru.workouttracker.workout.model;

import jakarta.persistence.*;
import notroflguru.workouttracker.user.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workout")
public class Workout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "notes")
    private String notes;

    @OneToMany(mappedBy = "workout", cascade = CascadeType.ALL, orphanRemoval = true)
    // cascade = CascadeType.ALL все действия с родительским объектом автоматически совершаются и с дочерним
    // orphanRemoval = true удаляет дочерний объект тогда, когда его отвязывают от родительского
    @OrderBy("orderNumber ASC")
    private List<WorkoutExercise> workoutExerciseList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Long getId() {
        return id;
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

    public List<WorkoutExercise> getWorkoutExerciseList() {
        return workoutExerciseList;
    }

    public void addWorkoutExercise(WorkoutExercise workoutExercise) {
        workoutExerciseList.add(workoutExercise);
        workoutExercise.setWorkout(this);
    }

    public void removeAllWorkoutExercises() {
        workoutExerciseList.clear();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
