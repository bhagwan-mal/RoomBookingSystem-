package com.multigenesys.booking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "resources")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String type;
    @Column(length = 1000)
    private String description;
    private Integer capacity;
    private boolean active = true;
}
