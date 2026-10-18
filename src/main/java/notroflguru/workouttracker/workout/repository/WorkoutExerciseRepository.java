package notroflguru.workouttracker.workout.repository;

import notroflguru.workouttracker.workout.model.WorkoutExercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExercise, Long> {
}
