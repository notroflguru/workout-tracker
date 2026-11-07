package notroflguru.workouttracker.exercise.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import notroflguru.workouttracker.exercise.dto.Muscle;

import java.util.List;

@JsonPropertyOrder({ "id", "name", "description", "muscleList"})
@Schema(description = "Объект ответа упражнения")
public class ExerciseResponse {
    @Schema(description = "Уникальный идентификатор упражнения", example = "2")
    private Long id;

    @Schema(description = "Название упражнения", example = "Squat")
    private String name;

    @Schema(description = "Описание упражнения", example = "Squat with a barbell")
    private String description;

    @Schema(description = "Список мышц, задействующихся в упражнении", example = "[\"Quadriceps\", \"Hamstrings\", \"Glutes\"]")
    private List<Muscle> muscleList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
