package com.multigenesys.booking.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multigenesys.booking.entity.Reservation;
import com.multigenesys.booking.entity.ResourceEntity;
import com.multigenesys.booking.entity.User;
import com.multigenesys.booking.repository.ReservationRepository;
import com.multigenesys.booking.repository.ResourceRepository;
import com.multigenesys.booking.repository.UserRepository;

import jakarta.persistence.criteria.Predicate;

@Service
public class ReservationService {
	private final ReservationRepository reservationRepo;
	private final ResourceRepository resourceRepo;
	private final UserRepository userRepo;

	public ReservationService(ReservationRepository reservationRepo, ResourceRepository resourceRepo,
			UserRepository userRepo) {
		this.reservationRepo = reservationRepo;
		this.resourceRepo = resourceRepo;
		this.userRepo = userRepo;
	}

	public Page<Reservation> list(Optional<Reservation.Status> status, Optional<BigDecimal> minPrice,
			Optional<BigDecimal> maxPrice, Optional<Long> userId, int page, int size, Sort sort) {
		Specification<Reservation> spec = (root, query, cb) -> {
			List<Predicate> preds = new ArrayList<>();
			status.ifPresent(s -> preds.add(cb.equal(root.get("status"), s)));
			minPrice.ifPresent(min -> preds.add(cb.ge(root.get("price"), min)));
			maxPrice.ifPresent(max -> preds.add(cb.le(root.get("price"), max)));
			userId.ifPresent(uid -> preds.add(cb.equal(root.get("user").get("id"), uid)));
			return cb.and(preds.toArray(new Predicate[0]));
		};
		return reservationRepo.findAll(spec, PageRequest.of(page, size, sort));
	}

	public Optional<Reservation> get(Long id) {
		return reservationRepo.findById(id);
	}

	@Transactional
	public Reservation create(Long requesterUserId, Reservation r) {
		// enforce user from requester if requester exists
		if (requesterUserId != null) {
			User u = userRepo.findById(requesterUserId).orElseThrow();
			r.setUser(u);
		} else {
			// ensure user exists
			r.setUser(userRepo.findById(r.getUser().getId()).orElseThrow());
		}
		ResourceEntity res = resourceRepo.findById(r.getResource().getId()).orElseThrow();
		r.setResource(res);

		// optional conflict check for CONFIRMED
		if (r.getStatus() == Reservation.Status.CONFIRMED) {
			boolean conflict = reservationRepo.existsConflict(res, Reservation.Status.CONFIRMED, r.getStartTime(),
					r.getEndTime());
			if (conflict)
				throw new IllegalStateException("Time slot already booked for this resource.");
		}
		return reservationRepo.save(r);
	}

	public Reservation update(Long id, Reservation updated) {
		Reservation existing = reservationRepo.findById(id).orElseThrow();
		existing.setStartTime(updated.getStartTime());
		existing.setEndTime(updated.getEndTime());
		existing.setPrice(updated.getPrice());
		existing.setStatus(updated.getStatus());
		// don't change user/resource here in minimal example
		return reservationRepo.save(existing);
	}

	public void delete(Long id) {
		reservationRepo.deleteById(id);
	}
}
