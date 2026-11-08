package notroflguru.workouttracker.workout.repository;

import notroflguru.workouttracker.workout.model.Workout;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    List<Workout> findAllByUser_Id(Long userId);
    Optional<Workout> findWithExercisesByIdAndUser_Id(Long workoutId, Long userId);
    boolean existsByIdAndUser_Id(Long workoutId, Long userId);
    void deleteByIdAndUser_Id(Long workoutId, Long userId);
}
