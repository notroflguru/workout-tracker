package notroflguru.workouttracker.user.repository;

import notroflguru.workouttracker.user.model.Role;
import notroflguru.workouttracker.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginOrEmail(String login, String email);
    boolean existsByLogin(String login);
    boolean existsByEmail(String email);
    boolean existsByRole(Role role);
    long countByRole(Role role);
}
