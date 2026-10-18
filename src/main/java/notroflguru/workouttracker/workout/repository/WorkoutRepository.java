package notroflguru.workouttracker.workout.repository;

import notroflguru.workouttracker.workout.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {
}
