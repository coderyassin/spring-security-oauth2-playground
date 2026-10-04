package org.yascode.springsecsection1.model;

import jakarta.persistence.*;
import lombok.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String password;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "subject")
    private String subject;

    @Column(name = "email", unique = true)
    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(mappedBy = "members", fetch = FetchType.EAGER)
    @Builder.Default
    private Set<Group> groups = new HashSet<>();

    @Transient
    private Set<GrantedAuthority> cachedAuthorities;

    @Column(nullable = false, name = "account_non_expired", columnDefinition = "boolean default true")
    private boolean accountNonExpired;

    @Column(nullable = false, name = "account_non_locked", columnDefinition = "boolean default true")
    private boolean accountNonLocked;

    @Column(nullable = false, name = "failed_login_attempts", columnDefinition = "int default 0")
    private int failedLoginAttempts;

    @Column(name = "last_failed_login_at")
    private Instant lastFailedLoginAt;

    @Column(name = "last_successful_login_at")
    private Instant lastSuccessfulLoginAt;

    @Column(name = "locked_until")
    private Instant lockedUntil;

    @Column(nullable = false, name = "credentials_non_expired", columnDefinition = "boolean default true")
    private boolean credentialsNonExpired;

    @Column(nullable = false, name = "enabled", columnDefinition = "boolean default true")
    private boolean enabled;

    @Override
    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.cachedAuthorities == null) {
            this.cachedAuthorities = this.roles.stream()
                    .flatMap(role -> role.getAuthorities().stream())
                    .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                    .collect(Collectors.toUnmodifiableSet());
        }
        return this.cachedAuthorities;
    }

    @Override
    public @Nullable String getPassword() {
        return this.password;
    }

    @Override
    public @NonNull String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked && (this.lockedUntil == null || this.lockedUntil.isBefore(Instant.now()));
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
