package notroflguru.workouttracker.user.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String fieldName, String fieldValue) {
        super(String.format("Пользователь с %s %s уже существует", fieldName, fieldValue));
    }
}
