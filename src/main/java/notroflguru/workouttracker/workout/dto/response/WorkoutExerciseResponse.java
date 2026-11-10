package notroflguru.workouttracker.workout.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@JsonPropertyOrder({ "id", "exerciseId", "exerciseName", "orderNumber", "exerciseSetResponseList"})
public class WorkoutExerciseResponse {
    @Schema(description = "Уникальный идентификатор", example = "1")
    private Long id;

    @Schema(description = "Уникальный идентификатор упражнения (см. ExerciseResponse)", example = "2")
    private Long exerciseId;

    @Schema(description = "Название упражнения", example = "Squat")
    private String exerciseName;

    @Schema(description = "Список подходов в упражнении")
    private List<ExerciseSetResponse> exerciseSetResponseList;

    @Schema(description = "Порядок выполнения упражнения в тренировке", example = "1")
    private int orderNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public List<ExerciseSetResponse> getExerciseSetResponseList() {
        return exerciseSetResponseList;
    }

    public void setExerciseSetResponseList(List<ExerciseSetResponse> exerciseSetResponseList) {
        this.exerciseSetResponseList = exerciseSetResponseList;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }
}