package notroflguru.workouttracker.exercise.repository;

import notroflguru.workouttracker.exercise.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
}
