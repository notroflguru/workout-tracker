package notroflguru.workouttracker.exercise.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import notroflguru.workouttracker.exercise.model.Muscle;

import java.util.Set;

@JsonPropertyOrder({ "id", "name", "description", "muscleList"})
@Schema(description = "Объект ответа упражнения")
public class ExerciseResponse {
    @Schema(description = "Уникальный идентификатор упражнения", example = "2")
    private Long id;

    @Schema(description = "Название упражнения", example = "Squat")
    private String name;

    @Schema(description = "Описание упражнения", example = "Приседания со штангой на плечах")
    private String description;

    @Schema(description = "Список мышц, которые задействуются в упражнении", example = "[\"Quadriceps\", \"Hamstrings\", \"Glutes\"]")
    private Set<Muscle> muscleList;

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

    public Set<Muscle> getMuscleList() {
        return muscleList;
    }

    public void setMuscleList(Set<Muscle> muscleList) {
        this.muscleList = muscleList;
    }
}
