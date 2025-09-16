package com.multigenesys.booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    public enum Status { PENDING, CONFIRMED, CANCELLED }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private ResourceEntity resource;

    @ManyToOne(optional = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private Status status;

    private BigDecimal price;

    private Instant startTime;
    private Instant endTime;

    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}

