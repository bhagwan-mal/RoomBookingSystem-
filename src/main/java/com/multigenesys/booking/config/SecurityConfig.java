package com.multigenesys.booking.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.multigenesys.booking.service.UserDetailsServiceImpl;

@Configuration
public class SecurityConfig {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl uds;

    public SecurityConfig(JwtUtils jwtUtils, UserDetailsServiceImpl uds) {
        this.jwtUtils = jwtUtils;
        this.uds = uds;
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(jwtUtils);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/login", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers(HttpMethod.GET, "/resources/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                .requestMatchers("/resources/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.POST, "/reservations").hasAnyAuthority("ROLE_ADMIN","ROLE_USER")
                .requestMatchers("/reservations/**").authenticated()
                .anyRequest().authenticated()
            )
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

