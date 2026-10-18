package notroflguru.workouttracker.workout.repository;

import notroflguru.workouttracker.workout.model.ExerciseSet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseSetRepository extends JpaRepository<ExerciseSet, Long> {
}
