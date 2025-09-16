package com.multigenesys.booking.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class ReservationDto {
    private Long id;
    private Long resourceId;
    // userId will be ignored for USER role; derived from JWT
    private Long userId;
    private String status; // PENDING, CONFIRMED, CANCELLED
    private BigDecimal price;
    private Instant startTime;
    private Instant endTime;
}

