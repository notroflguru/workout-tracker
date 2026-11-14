package notroflguru.workouttracker.admin.service;

import notroflguru.workouttracker.admin.dto.request.ChangeUserRoleRequest;
import notroflguru.workouttracker.user.dto.UserMapper;
import notroflguru.workouttracker.user.dto.response.AdminUserResponse;
import notroflguru.workouttracker.user.exception.UserNotFoundException;
import notroflguru.workouttracker.user.model.Role;
import notroflguru.workouttracker.user.model.User;
import notroflguru.workouttracker.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public AdminService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    public Page<AdminUserResponse> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toAdminResponse);
    }

    @Transactional
    public AdminUserResponse changeUserRole(Long id, ChangeUserRoleRequest request) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        if (user.getRole() == Role.ADMIN
                && request.getRole() != Role.ADMIN
                && userRepository.countByRole(Role.ADMIN) <= 1) {
            throw new IllegalStateException("Cannot demote the last admin");
        }

        user.setRole(request.getRole());

        return userMapper.toAdminResponse(user);
    }
}
