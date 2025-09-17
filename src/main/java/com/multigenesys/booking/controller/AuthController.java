package com.multigenesys.booking.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multigenesys.booking.config.JwtUtils;
import com.multigenesys.booking.dto.AuthRequest;
import com.multigenesys.booking.dto.AuthResponse;
import com.multigenesys.booking.entity.User;
import com.multigenesys.booking.repository.UserRepository;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserRepository userRepo;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authManager, UserRepository userRepo, JwtUtils jwtUtils) {
        this.authManager = authManager;
        this.userRepo = userRepo;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        Authentication a = authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        User u = userRepo.findByUsername(req.getUsername()).orElseThrow();
        String token = jwtUtils.generateToken(u.getUsername(), u.getId(), u.getRoles());
        return ResponseEntity.ok(new AuthResponse(token, "Bearer", 3600_000L));
    }
}