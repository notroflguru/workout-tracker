package notroflguru.workouttracker.exercise.repository;

import notroflguru.workouttracker.exercise.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    Optional<Exercise> findByNameIgnoreCase (String name);
    // SELECT *
    // FROM exercise
    // WHERE LOWER(name) = LOWER(?);

}
