package notroflguru.workouttracker.admin.controller;

import jakarta.validation.Valid;
import notroflguru.workouttracker.admin.dto.request.ChangeUserRoleRequest;
import notroflguru.workouttracker.admin.service.AdminService;
import notroflguru.workouttracker.user.dto.response.AdminUserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService service;

    public AdminController(AdminService adminService) {
        this.service = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<Page<AdminUserResponse>> findAllUsers(
            @PageableDefault(
                    size = 20,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        Page<AdminUserResponse> responsePage = service.findAllUsers(pageable);

        return ResponseEntity.ok(responsePage);
    }

    @PatchMapping("/users/{id}/role")
    public ResponseEntity<AdminUserResponse> changeUserRole(
            @PathVariable Long id,
            @Valid @RequestBody ChangeUserRoleRequest request
    ) {

        return ResponseEntity.ok(service.changeUserRole(id, request));
    }
}
