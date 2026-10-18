package org.yascode.springsecsection1.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.yascode.springsecsection1.model.User;
import org.yascode.springsecsection1.service.UserService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class WelcomeController {

    private final UserService userService;

    private List<UserDto> users = new ArrayList<>(
            List.of(
                    new UserDto(1L, "John Doe", "john.doe@example.com"),
                    new UserDto(2L,"Jane Doe", "jane.doe@example.com"),
                    new UserDto(3L,"John Smith", "john.smith@example.com")
            )
    );

    @GetMapping("/welcome")
    @PreAuthorize("hasAuthority('UPDATE')")
    public String sayWelcome() {
        return "Welcome to Spring Application with security";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/admin/dashboard")
    public String adminDashboard(Authentication authentication) {
        return "Admin dashboard";
    }

    @PreAuthorize("hasAuthority('PERM_users:read')")
    @GetMapping("/api/users")
    public List<UserDto> getUsers(Authentication authentication) {
        return users;
    }

    @PreAuthorize("hasAuthority('PERM_users:delete')")
    @DeleteMapping("/api/users/{id}")
    public void deleteUser(@PathVariable Long id, Authentication authentication) {
        users.removeIf(user -> user.id().equals(id));
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @GetMapping("/v2/{userId}")
    @PreAuthorize("@userSecurity.isOwner(#userId, authentication)")
    public User getUserByUserId(@PathVariable String userId) {
        return userService.getById(userId);
    }

    public record UserDto(
            Long id,
            String name,
            String email
    ) {
    }

}
