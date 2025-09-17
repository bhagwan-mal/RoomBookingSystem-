package com.multigenesys.booking.controller;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.multigenesys.booking.dto.ReservationDto;
import com.multigenesys.booking.entity.Reservation;
import com.multigenesys.booking.entity.ResourceEntity;
import com.multigenesys.booking.repository.ResourceRepository;
import com.multigenesys.booking.repository.UserRepository;
import com.multigenesys.booking.service.ReservationService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.var;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationService service;
    private final UserRepository userRepo;
    private final ResourceRepository resourceRepo;

    public ReservationController(ReservationService service, UserRepository userRepo, ResourceRepository resourceRepo) {
        this.service = service;
        this.userRepo = userRepo;
        this.resourceRepo = resourceRepo;
    }

    // List with filters: status, minPrice, maxPrice, page, size, sort
    @GetMapping
    public ResponseEntity<?> list(@RequestParam Optional<String> status,
                                  @RequestParam Optional<BigDecimal> minPrice,
                                  @RequestParam Optional<BigDecimal> maxPrice,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "20") int size,
                                  @RequestParam(defaultValue = "createdAt,desc") String sort,
                                  HttpServletRequest req) {
        var parts = sort.split(",");
        Sort s = Sort.by(Sort.Direction.fromString(parts.length>1?parts[1]:"desc"), parts[0]);

        Optional<Reservation.Status> st = status.map(String::toUpperCase).map(Reservation.Status::valueOf);

        // If user role, limit to own
        Object userIdAttr = req.getAttribute("userId");
        Long userIdFromJwt = userIdAttr instanceof Integer ? ((Integer)userIdAttr).longValue()
                : userIdAttr instanceof Long ? (Long) userIdAttr : null;

        // if the principal is USER (has ROLE_USER but not ADMIN), only show own
        boolean isAdmin = req.isUserInRole("ROLE_ADMIN");
        Optional<Long> maybeUserId = isAdmin ? Optional.empty() : Optional.ofNullable(userIdFromJwt);

        var pageRes = service.list(st, minPrice, maxPrice, maybeUserId, page, size, s);
        return ResponseEntity.ok(pageRes);
    }
 
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id, HttpServletRequest req) {
        var rOpt = service.get(id);
        if (rOpt.isEmpty()) return ResponseEntity.notFound().build();
        var r = rOpt.get();
        boolean isAdmin = req.isUserInRole("ROLE_ADMIN");
        Object userIdAttr = req.getAttribute("userId");
        Long userIdFromJwt = userIdAttr instanceof Integer ? ((Integer)userIdAttr).longValue()
                : userIdAttr instanceof Long ? (Long) userIdAttr : null;
        if (!isAdmin && !r.getUser().getId().equals(userIdFromJwt)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(r);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ReservationDto dto, HttpServletRequest req) {
        // map dto to entity
        Reservation r = new Reservation();
        r.setPrice(dto.getPrice());
        r.setStartTime(dto.getStartTime());
        r.setEndTime(dto.getEndTime());
        r.setStatus(dto.getStatus() == null ? Reservation.Status.PENDING : Reservation.Status.valueOf(dto.getStatus()));
        // link resource
        ResourceEntity res = resourceRepo.findById(dto.getResourceId()).orElseThrow();
        r.setResource(res);

        // get user id from JWT if present (for USER)
        Object userIdAttr = req.getAttribute("userId");
        Long userIdFromJwt = userIdAttr instanceof Integer ? ((Integer)userIdAttr).longValue()
                : userIdAttr instanceof Long ? (Long) userIdAttr : null;

        Reservation saved = service.create(userIdFromJwt, r);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ReservationDto dto, HttpServletRequest req) {
        var existing = service.get(id).orElseThrow();
        boolean isAdmin = req.isUserInRole("ROLE_ADMIN");
        Object userIdAttr = req.getAttribute("userId");
        Long userIdFromJwt = userIdAttr instanceof Integer ? ((Integer)userIdAttr).longValue()
                : userIdAttr instanceof Long ? (Long) userIdAttr : null;
        if (!isAdmin && !existing.getUser().getId().equals(userIdFromJwt)) return ResponseEntity.status(403).build();

        Reservation r = new Reservation();
        r.setStartTime(dto.getStartTime());
        r.setEndTime(dto.getEndTime());
        r.setPrice(dto.getPrice());
        r.setStatus(dto.getStatus()==null? existing.getStatus() : Reservation.Status.valueOf(dto.getStatus()));
        var updated = service.update(id, r);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpServletRequest req) {
        var existing = service.get(id).orElseThrow();
        boolean isAdmin = req.isUserInRole("ROLE_ADMIN");
        Object userIdAttr = req.getAttribute("userId");
        Long userIdFromJwt = userIdAttr instanceof Integer ? ((Integer)userIdAttr).longValue()
                : userIdAttr instanceof Long ? (Long) userIdAttr : null;
        if (!isAdmin && !existing.getUser().getId().equals(userIdFromJwt)) return ResponseEntity.status(403).build();

        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}