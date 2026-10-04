package org.yascode.springsecsection1.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "AUTHORITIES")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    private String description;
}
