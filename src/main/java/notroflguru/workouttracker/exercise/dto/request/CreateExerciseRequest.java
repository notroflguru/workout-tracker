package notroflguru.workouttracker.exercise.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import notroflguru.workouttracker.exercise.model.Muscle;

import java.util.List;

@Schema(description = "Объект запроса создания упражнения")
public class CreateExerciseRequest {

    @NotBlank
    @Size(min = 5, max = 63, message = "Название должно быть от 5 до 63 символов")
    @Schema(description = "Название упражнения", example = "Squat")
    private String name;

    @Size(min = 5, max = 255, message = "Описание должно быть от 5 до 255 символов")
    @NotBlank
    @Schema(description = "Описание упражнения", example = "Приседания со штангой на плечах")
    private String description;

    @NotEmpty
    @Size(max = 5, message = "Мышц должно быть не больше 5")
    @Schema(description = "Список мышц, которые задействуются в упражнении", example = "[\"Quadriceps\", \"Hamstrings\", \"Glutes\"]")
    private List<Muscle> muscleList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Muscle> getMuscleList() {
        return muscleList;
    }

    public void setMuscleList(List<Muscle> muscleList) {
        this.muscleList = muscleList;
    }
}
