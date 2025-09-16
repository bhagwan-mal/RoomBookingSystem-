package com.multigenesys.booking.controller;


import org.springframework.data.domain.Page;
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

import com.multigenesys.booking.dto.ResourceDto;
import com.multigenesys.booking.entity.ResourceEntity;
import com.multigenesys.booking.service.ResourceService;

import lombok.var;

@RestController
@RequestMapping("/resources")
public class ResourceController {
    private final ResourceService service;
    public ResourceController(ResourceService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "20") int size,
                                  @RequestParam(defaultValue = "id,asc") String sort) {
        var parts = sort.split(",");
        Sort s = Sort.by(Sort.Direction.fromString(parts.length>1?parts[1]:"asc"), parts[0]);
        Page<ResourceEntity> p = service.list(page, size, s);
        var dtoPage = p.map(r -> {
            ResourceDto d = new ResourceDto();
            d.setId(r.getId()); d.setName(r.getName()); d.setType(r.getType());
            d.setDescription(r.getDescription()); d.setCapacity(r.getCapacity()); d.setActive(r.isActive());
            return d;
        });
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return service.get(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ResourceEntity r) {
        return ResponseEntity.ok(service.create(r));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ResourceEntity r) {
        return ResponseEntity.ok(service.update(id,r));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
