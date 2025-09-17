package com.multigenesys.booking.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.multigenesys.booking.entity.ResourceEntity;
import com.multigenesys.booking.repository.ResourceRepository;

@Service
public class ResourceService {
	private final ResourceRepository repo;

	public ResourceService(ResourceRepository repo) {
		this.repo = repo;
	}

	public Page<ResourceEntity> list(int page, int size, Sort sort) {
		return repo.findAll(PageRequest.of(page, size, sort));
	}

	public Optional<ResourceEntity> get(Long id) {
		return repo.findById(id);
	}

	public ResourceEntity create(ResourceEntity r) {
		return repo.save(r);
	}

	public ResourceEntity update(Long id, ResourceEntity r) {
		r.setId(id);
		return repo.save(r);
	}

	public void delete(Long id) {
		repo.deleteById(id);
	}

	public boolean existsById(Long id) {
		return repo.existsById(id);
	}
}