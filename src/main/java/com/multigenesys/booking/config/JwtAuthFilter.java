package com.multigenesys.booking.config;


import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    public JwtAuthFilter(JwtUtils jwtUtils) { this.jwtUtils = jwtUtils; }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        String h = req.getHeader("Authorization");
        if (h != null && h.startsWith("Bearer ")) {
            String token = h.substring(7);
            if (jwtUtils.validate(token)) {
                Claims c = jwtUtils.getClaims(token);
                String username = c.getSubject();
                Object rolesObj = c.get("roles");
                List<SimpleGrantedAuthority> auths = ((List<?>) rolesObj).stream()
                        .map(Object::toString)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
                // store userId as request attribute for convenience
                req.setAttribute("userId", c.get("userId"));
                UsernamePasswordAuthenticationToken a = new UsernamePasswordAuthenticationToken(username, null, auths);
                SecurityContextHolder.getContext().setAuthentication(a);
            }
        }
        chain.doFilter(req, res);
    }
}
