package com.multigenesys.booking.service;

import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.multigenesys.booking.entity.User;
import com.multigenesys.booking.repository.UserRepository;

import lombok.experimental.var;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	private final UserRepository userRepo;

	public UserDetailsServiceImpl(UserRepository userRepo) {
		this.userRepo = userRepo;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User u = userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("not found"));
		var authorities = u.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.name()))
				.collect(Collectors.toList());
		return org.springframework.security.core.userdetails.User.withUsername(u.getUsername())
				.password(u.getPassword()).authorities(authorities).accountExpired(false).accountLocked(false)
				.credentialsExpired(false).disabled(!u.isEnabled()).build();
	}
}
