package notroflguru.workouttracker.user.repository;

import notroflguru.workouttracker.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String login);
    boolean existsByLogin(String login);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}
