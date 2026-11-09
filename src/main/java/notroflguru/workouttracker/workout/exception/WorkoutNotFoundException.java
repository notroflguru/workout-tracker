package notroflguru.workouttracker.workout.exception;

public class WorkoutNotFoundException extends RuntimeException {
    public WorkoutNotFoundException() {
        super("Тренировка не найдена");
    }
}
