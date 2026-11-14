package notroflguru.workouttracker.exercise.repository;

import notroflguru.workouttracker.exercise.model.Exercise;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    @Override
    @EntityGraph(attributePaths = "muscleList")
    Optional<Exercise> findById(Long id);

    Optional<Exercise> findByNameIgnoreCase (String name);

    List<Exercise> findAllByIdIn(Collection<Long> ids);
    // SELECT *
    // FROM exercise
    // WHERE LOWER(name) = LOWER(?);

    @EntityGraph(attributePaths = "muscleList")
    List<Exercise> findAllByOrderByNameAsc();
}
