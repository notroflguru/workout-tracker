package notroflguru.workouttracker.exercise.service;

import notroflguru.workouttracker.exercise.dto.*;
import notroflguru.workouttracker.exercise.exception.ExerciseAlreadyExistsException;
import notroflguru.workouttracker.exercise.exception.ExerciseNotFoundException;
import notroflguru.workouttracker.exercise.model.Exercise;
import notroflguru.workouttracker.exercise.repository.ExerciseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExerciseService {

    private final ExerciseRepository repository;
    private final ExerciseMapper mapper;

    public ExerciseService(ExerciseRepository exerciseRepository, ExerciseMapper exerciseMapper) {
        this.repository = exerciseRepository;
        this.mapper = exerciseMapper;
    }

    public List<ExerciseResponse> findAll() {
        return mapper.toResponseList(repository.findAll());
    }

    public ExerciseResponse createExercise(CreateExerciseRequest request) {

        if (repository.findByNameIgnoreCase(request.getName()).isPresent()) {
            throw new ExerciseAlreadyExistsException("Exercise already exists: " + request.getName());
        }

        Exercise entity = mapper.toEntity(request);
        Exercise savedEntity = repository.save(entity);

        return mapper.toResponse(savedEntity);
    }

    public ExerciseResponse findExercise(String name) {

        Exercise entity = repository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ExerciseNotFoundException("Exercise not found: " + name));

        return mapper.toResponse(entity);
    }
}
