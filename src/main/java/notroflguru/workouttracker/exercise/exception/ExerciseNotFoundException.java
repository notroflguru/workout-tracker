package notroflguru.workouttracker.exercise.exception;

public class ExerciseNotFoundException extends RuntimeException {
    public ExerciseNotFoundException() {
        super("Упражнение не найдено");
    }
}
