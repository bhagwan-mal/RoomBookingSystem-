package com.multigenesys.booking.service;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.multigenesys.booking.entity.Role;
import com.multigenesys.booking.entity.User;
import com.multigenesys.booking.repository.UserRepository;

@Configuration
public class SeedData {
	@Bean
	CommandLineRunner init(UserRepository userRepository, PasswordEncoder encoder) {
		return args -> {
			if (!userRepository.existsByUsername("admin")) {
				User u = new User();
				u.setUsername("admin");
				u.setPassword(encoder.encode("admin123"));
				u.setRoles(Set.of(Role.ROLE_ADMIN));
				u.setEnabled(true);
				userRepository.save(u);
			}
			if (!userRepository.existsByUsername("user")) {
				User u = new User();
				u.setUsername("user");
				u.setPassword(encoder.encode("user123"));
				u.setRoles(Set.of(Role.ROLE_USER));
				u.setEnabled(true);
				userRepository.save(u);
			}
			if (!userRepository.existsByUsername("user2")) {
				User u = new User();
				u.setUsername("user2");
				u.setPassword(encoder.encode("user222"));
				u.setRoles(Set.of(Role.ROLE_USER));
				u.setEnabled(true);
				userRepository.save(u);
			}
		};
	}
}
