package notroflguru.workouttracker.exercise.exception;

public class ExerciseAlreadyExistsException extends RuntimeException {
    public ExerciseAlreadyExistsException(String message) {
        super(message);
    }
}
