package notroflguru.workouttracker.user.service;

import notroflguru.workouttracker.user.model.User;
import notroflguru.workouttracker.user.repository.UserRepository;
import notroflguru.workouttracker.user.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {

        String normalizedIdentifier = identifier.toLowerCase().trim();

        User user = userRepository.findByLoginOrEmail(normalizedIdentifier, normalizedIdentifier)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        return new SecurityUser(user);
    }
}