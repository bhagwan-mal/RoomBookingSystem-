package com.multigenesys.booking.dto;

import lombok.Data;

@Data
public class ResourceDto {
    private Long id;
    private String name;
    private String type;
    private String description;
    private Integer capacity;
    private Boolean active;
}

