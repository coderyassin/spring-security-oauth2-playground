package org.yascode.springsecsection1.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "groups")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_name", nullable = false, unique = true)
    private String groupName;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "group_authorities",
            joinColumns = @JoinColumn(name = "group_id")
    )
    @Column(name = "authority")
    @Builder.Default
    private Set<String> authorities = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "group_members",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "username", referencedColumnName = "username")
    )
    @Builder.Default
    private Set<User> members = new HashSet<>();
}
