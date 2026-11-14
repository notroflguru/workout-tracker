package notroflguru.workouttracker.exercise.service;

import lombok.extern.slf4j.Slf4j;
import notroflguru.workouttracker.exercise.dto.*;
import notroflguru.workouttracker.exercise.dto.request.CreateExerciseRequest;
import notroflguru.workouttracker.exercise.dto.response.ExerciseResponse;
import notroflguru.workouttracker.exercise.dto.response.ExerciseShortResponse;
import notroflguru.workouttracker.exercise.exception.ExerciseAlreadyExistsException;
import notroflguru.workouttracker.exercise.exception.ExerciseNotFoundException;
import notroflguru.workouttracker.exercise.model.Exercise;
import notroflguru.workouttracker.exercise.repository.ExerciseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class ExerciseService {

    private final ExerciseRepository repository;
    private final ExerciseMapper mapper;

    public ExerciseService(ExerciseRepository exerciseRepository, ExerciseMapper exerciseMapper) {
        this.repository = exerciseRepository;
        this.mapper = exerciseMapper;
    }

    public List<ExerciseShortResponse> findAll() {
        return mapper.toShortResponseList(repository.findAllByOrderByNameAsc());
    }

    @Transactional
    public ExerciseResponse createExercise(CreateExerciseRequest request) {

        if (repository.findByNameIgnoreCase(request.getName()).isPresent()) {
            throw new ExerciseAlreadyExistsException();
        }

        Exercise entity = mapper.toEntity(request);
        Exercise savedEntity = repository.save(entity);

        log.info("Successfully created new exercise with ID: {} and name: {}", savedEntity.getId(), savedEntity.getName());

        return mapper.toResponse(savedEntity);
    }

    public ExerciseResponse findByName(String name) {

        Exercise entity = repository.findByNameIgnoreCase(name)
                .orElseThrow(ExerciseNotFoundException::new);

        log.debug("Successfully found exercise with name: {}", name);

        return mapper.toResponse(entity);
    }

    public ExerciseResponse findById(Long id) {

        Exercise entity = repository.findById(id)
                .orElseThrow(ExerciseNotFoundException::new);

        log.debug("Successfully found exercise with id: {}", id);

        return mapper.toResponse(entity);
    }

}
