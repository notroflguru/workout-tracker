package notroflguru.workouttracker.exercise.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "exercise")
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @ElementCollection
    @CollectionTable(name = "exercise_muscles")   // Вспомогательная таблица exercise - muscle чтобы не хранить списком
    @Column(name = "muscles", nullable = false)
    @Enumerated(EnumType.STRING)
    private List<Muscle> muscleList;

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

    public List<Muscle> getMuscleList() {
        return muscleList;
    }

    public void setMuscleList(List<Muscle> muscles) {
        this.muscleList = muscles;
    }
}
