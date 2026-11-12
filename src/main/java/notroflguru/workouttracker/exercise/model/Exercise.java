package notroflguru.workouttracker.exercise.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

@Entity
@Table(name = "exercise")
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", unique = true, nullable = false, length = 63)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @ElementCollection
    @CollectionTable(name = "exercise_muscles")   // Вспомогательная таблица exercise - muscle чтобы не хранить списком
    @Column(name = "muscles", nullable = false, length = 127)
    @Enumerated(EnumType.STRING)
    private Set<Muscle> muscleList;

    public Long getId() {
        return id;
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

    public void setMuscleList(Set<Muscle> muscles) {
        this.muscleList = muscles;
    }
}
