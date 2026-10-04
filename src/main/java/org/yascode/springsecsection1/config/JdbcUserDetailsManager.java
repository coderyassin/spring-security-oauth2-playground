package org.yascode.springsecsection1.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.GroupManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.yascode.springsecsection1.model.Group;
import org.yascode.springsecsection1.model.User;
import org.yascode.springsecsection1.repository.GroupRepository;
import org.yascode.springsecsection1.repository.UserRepository;
import org.yascode.springsecsection1.repository.projection.GroupNameView;

import java.util.HashSet;
import java.util.List;

@NullMarked
@Service
public class JdbcUserDetailsManager implements CustomUserDetailsService, GroupManager {

    protected final Log logger = LogFactory.getLog(getClass());

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public JdbcUserDetailsManager(UserRepository userRepository, GroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    public List<String> findAllGroups() {
        return groupRepository.findGroupNamesBy()
                .stream()
                .map(GroupNameView::getGroupName)
                .toList();
    }

    @Override
    public List<String> findUsersInGroup(String groupName) {
        Assert.hasText(groupName, "groupName should have text");
        return groupRepository.findMemberUsernamesByGroupName(groupName);
    }

    @Override
    public void createGroup(String groupName, List<GrantedAuthority> authorities) {
        Assert.hasText(groupName, "groupName should have text");
        Assert.notNull(authorities, "authorities cannot be null");
        this.logger.debug("Creating new group '" + groupName + "' with authorities "
                + AuthorityUtils.authorityListToSet(authorities));

        Group group = Group.builder()
                .groupName(groupName)
                .authorities(AuthorityUtils.authorityListToSet(authorities))
                .build();
        groupRepository.save(group);
    }

    @Override
    @Transactional
    public void deleteGroup(String groupName) {
        this.logger.debug("Deleting group '" + groupName + "'");
        Assert.hasText(groupName, "groupName should have text");
        Group group = groupRepository.findByGroupName(groupName)
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + groupName));
        groupRepository.delete(group);
    }

    @Override
    @Transactional
    public void renameGroup(String oldName, String newName) {
        Assert.hasText(oldName, "oldName should have text");
        Assert.hasText(newName, "newName should have text");
        getGroup(oldName).setGroupName(newName);
    }

    @Override
    @Transactional
    public void addUserToGroup(String username, String group) {
        Assert.hasText(username, "username should have text");
        Assert.hasText(group, "group should have text");
        getGroup(group).getMembers().add(getUser(username));
    }

    @Override
    @Transactional
    public void removeUserFromGroup(String username, String groupName) {
        Assert.hasText(username, "username should have text");
        Assert.hasText(groupName, "groupName should have text");
        getGroup(groupName).getMembers().remove(getUser(username));
    }

    @Override
    public List<GrantedAuthority> findGroupAuthorities(String groupName) {
        Assert.hasText(groupName, "groupName should have text");
        return groupRepository.findByGroupName(groupName)
                .map(group -> group.getAuthorities().stream()
                        .map(SimpleGrantedAuthority::new)
                        .map(GrantedAuthority.class::cast)
                        .toList())
                .orElseGet(List::of);
    }

    @Override
    @Transactional
    public void addGroupAuthority(String groupName, GrantedAuthority authority) {
        Assert.hasText(groupName, "groupName should have text");
        Assert.notNull(authority, "authority cannot be null");
        getGroup(groupName).getAuthorities().add(authority.getAuthority());
    }

    @Override
    @Transactional
    public void removeGroupAuthority(String groupName, GrantedAuthority authority) {
        Assert.hasText(groupName, "groupName should have text");
        Assert.notNull(authority, "authority cannot be null");
        getGroup(groupName).getAuthorities().remove(authority.getAuthority());
    }

    @Override
    @Transactional
    public void createUser(UserDetails user) {
        Assert.notNull(user, "user cannot be null");
        Assert.hasText(user.getUsername(), "username should have text");
        Assert.isTrue(!userExists(user.getUsername()), "User already exists: " + user.getUsername());

        User newUser = User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .accountNonExpired(user.isAccountNonExpired())
                .accountNonLocked(user.isAccountNonLocked())
                .credentialsNonExpired(user.isCredentialsNonExpired())
                .enabled(user.isEnabled())
                .roles(user instanceof User managedUser
                        ? new HashSet<>(managedUser.getRoles())
                        : new HashSet<>())
                .build();
        userRepository.save(newUser);
    }

    @Override
    @Transactional
    public void updateUser(UserDetails user) {
        Assert.notNull(user, "user cannot be null");
        Assert.hasText(user.getUsername(), "username should have text");

        User existingUser = getUser(user.getUsername());
        existingUser.setPassword(user.getPassword());
        existingUser.setAccountNonExpired(user.isAccountNonExpired());
        existingUser.setAccountNonLocked(user.isAccountNonLocked());
        existingUser.setCredentialsNonExpired(user.isCredentialsNonExpired());
        existingUser.setEnabled(user.isEnabled());
        if (user instanceof User managedUser) {
            existingUser.setRoles(new HashSet<>(managedUser.getRoles()));
        }
    }

    @Override
    @Transactional
    public void deleteUser(String username) {
        Assert.hasText(username, "username should have text");
        User user = getUser(username);
        new HashSet<>(user.getGroups()).forEach(group -> group.getMembers().remove(user));
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void changePassword(@Nullable String oldPassword, @Nullable String newPassword) {
        Assert.hasText(newPassword, "newPassword should have text");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        getUser(username).setPassword(newPassword);
    }

    @Override
    public boolean userExists(String username) {
        Assert.hasText(username, "username should have text");
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    private Group getGroup(String groupName) {
        return groupRepository.findByGroupName(groupName)
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + groupName));
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Override
    public UserDetails findOrCreate(String subject, String email) {
        return userRepository.findBySubjectOrEmail(subject, email)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .username(subject)
                            .subject(subject)
                            .email(email)
                            .accountNonExpired(true)
                            .accountNonLocked(true)
                            .credentialsNonExpired(true)
                            .enabled(true)
                            .build();
                    userRepository.save(newUser);
                    return newUser;
                });
    }
}
