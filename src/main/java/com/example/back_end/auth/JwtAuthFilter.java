package com.example.back_end.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtService jwt;
  private final UserDetailsService uds;

  public JwtAuthFilter(JwtService jwt, UserDetailsService uds) {
    this.jwt = jwt;
    this.uds = uds;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    String header = req.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7);
      try {
        Jws<Claims> jws = jwt.parse(token);
        String username = jws.getBody().getSubject();
        UserDetails user = uds.loadUserByUsername(username);
        var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
      } catch (Exception ignored) {
      }
    }
    chain.doFilter(req, res);
  }
}
