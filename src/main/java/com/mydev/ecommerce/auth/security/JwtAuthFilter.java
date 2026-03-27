

package com.mydev.ecommerce.auth.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends GenericFilter {

  private final JwtService jwtService;

  public JwtAuthFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest req = (HttpServletRequest) request;
    String auth = req.getHeader("Authorization");

    System.out.println("JWT PATH = " + req.getRequestURI());
    System.out.println("JWT HEADER = " + auth);

    if (auth != null && auth.startsWith("Bearer ")) {
      String token = auth.substring(7);

      try {
        Claims claims = jwtService.parseClaims(token);

        String email = claims.get("email", String.class);
        String role = claims.get("role", String.class);

        System.out.println("JWT EMAIL = " + email);
        System.out.println("JWT ROLE = " + role);

        if (email != null) {
          var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
          var authentication =
              new UsernamePasswordAuthenticationToken(email, null, authorities);

          SecurityContextHolder.getContext().setAuthentication(authentication);
          System.out.println("JWT AUTH SET = " + email);
        }

      } catch (Exception e) {
        System.out.println("JWT ERROR = " + e.getClass().getName() + " :: " + e.getMessage());
        SecurityContextHolder.clearContext();
      }
    }

    chain.doFilter(request, response);
  }
}