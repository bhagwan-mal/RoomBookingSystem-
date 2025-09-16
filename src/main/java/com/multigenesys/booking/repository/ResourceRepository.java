package com.multigenesys.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multigenesys.booking.entity.ResourceEntity;

public interface ResourceRepository extends JpaRepository<ResourceEntity, Long> {
}

