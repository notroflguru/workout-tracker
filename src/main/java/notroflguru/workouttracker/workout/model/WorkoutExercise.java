package notroflguru.workouttracker.workout.model;

import jakarta.persistence.*;
import notroflguru.workouttracker.exercise.model.Exercise;

import java.util.List;

@Entity
@Table(name = "workout_exercise")
// Связующая сущность, чтобы добавить упражнение внутрь конкретной тренировки + подходы по этому упражнению
public class WorkoutExercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workout_id", referencedColumnName = "id", nullable = false)
    private Workout workout;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercise_id", referencedColumnName = "id", nullable = false)
    private Exercise exercise;

    @OneToMany(mappedBy = "workoutExercise", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderNumber ASC")
    private List<ExerciseSet> exerciseSetList;

    @Column(name = "order_number")
    private int orderNumber;

    public Long getId() {
        return id;
    }

    public Workout getWorkout() {
        return workout;
    }

    public void setWorkout(Workout workout) {
        this.workout = workout;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public List<ExerciseSet> getExerciseSetList() {
        return exerciseSetList;
    }

    public void setExerciseSetList(List<ExerciseSet> exerciseSetList) {
        this.exerciseSetList = exerciseSetList;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }
}
