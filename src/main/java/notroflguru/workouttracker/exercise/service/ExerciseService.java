package notroflguru.workouttracker.exercise.service;

import notroflguru.workouttracker.exercise.dto.*;
import notroflguru.workouttracker.exercise.model.Exercise;
import notroflguru.workouttracker.exercise.repository.ExerciseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExerciseService {
    private ExerciseRepository repository;
    private ExerciseMapper mapper;

    public ExerciseService(ExerciseRepository exerciseRepository, ExerciseMapper exerciseMapper) {
        this.repository = exerciseRepository;
        this.mapper = exerciseMapper;
    }

    public List<ExerciseResponse> findAll() {
        return mapper.toResponseList(repository.findAll());
    }

    public ExerciseResponse createExercise(CreateExerciseRequest request) {
        Exercise entity = mapper.toEntity(request);
        Exercise savedEntity = repository.save(entity);
        if (repository.existsById(savedEntity.getId())) {
            return mapper.toResponse(savedEntity);
        }
        return null;
    }
}
