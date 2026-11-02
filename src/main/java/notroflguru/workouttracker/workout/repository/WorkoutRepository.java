package notroflguru.workouttracker.workout.repository;

import notroflguru.workouttracker.workout.model.Workout;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    @EntityGraph(attributePaths = {"workoutExerciseList", "workoutExerciseList.exercise"})
    @Query("SELECT w FROM Workout w WHERE w.id = :id")
    Optional<Workout> findWithExercisesById(@Param("id") Long id);
}
