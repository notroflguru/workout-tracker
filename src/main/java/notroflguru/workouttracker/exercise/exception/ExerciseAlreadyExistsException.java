package notroflguru.workouttracker.exercise.exception;

public class ExerciseAlreadyExistsException extends RuntimeException {
    public ExerciseAlreadyExistsException() {
        super("Упражнение с таким названием уже существует");
    }
}
