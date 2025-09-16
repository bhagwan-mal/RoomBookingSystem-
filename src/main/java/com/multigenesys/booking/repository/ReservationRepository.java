package com.multigenesys.booking.repository;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.multigenesys.booking.entity.Reservation;
import com.multigenesys.booking.entity.ResourceEntity;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {

    @Query("""
      select case when count(r) > 0 then true else false end
      from Reservation r
      where r.resource = :res and r.status = :status
        and r.startTime < :end and r.endTime > :start
    """)
    boolean existsConflict(@Param("res") ResourceEntity res,
                           @Param("status") Reservation.Status status,
                           @Param("start") Instant start,
                           @Param("end") Instant end);
}

