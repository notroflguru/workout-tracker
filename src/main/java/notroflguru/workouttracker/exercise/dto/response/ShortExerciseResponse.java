package notroflguru.workouttracker.exercise.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import notroflguru.workouttracker.exercise.model.Muscle;

import java.util.List;

@JsonPropertyOrder({ "id", "name", "muscleList"})
@Schema(description = "Объект ответа упражнения")
public class ShortExerciseResponse {
    @Schema(description = "Уникальный идентификатор упражнения", example = "2")
    private Long id;

    @Schema(description = "Название упражнения", example = "Squat")
    private String name;

    @Schema(description = "Список мышц, которые задействуются в упражнении", example = "[\"Quadriceps\", \"Hamstrings\", \"Glutes\"]")
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

    public List<Muscle> getMuscleList() {
        return muscleList;
    }

    public void setMuscleList(List<Muscle> muscleList) {
        this.muscleList = muscleList;
    }
}