

package com.mydev.ecommerce.config;

import com.mydev.ecommerce.auth.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

@Bean
public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
  http
    .csrf(csrf -> csrf.disable())
    .cors(cors -> {})

    // ✅ VERY IMPORTANT (fixes 403)
    .sessionManagement(sm -> sm.sessionCreationPolicy(
        org.springframework.security.config.http.SessionCreationPolicy.STATELESS
    ))
    .httpBasic(b -> b.disable())
    .formLogin(f -> f.disable())

    .authorizeHttpRequests(auth -> auth
      .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
      .requestMatchers("/", "/ping").permitAll()
      .requestMatchers("/api/auth/**").permitAll()
      .requestMatchers("/api/categories/**").permitAll()
      .requestMatchers("/api/products/**").permitAll()
      .requestMatchers("/actuator/**").permitAll()
      // .requestMatchers("/api/admin/**").hasRole("ADMIN")
      .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
      .anyRequest().authenticated()
    )
    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

  return http.build();
}
}