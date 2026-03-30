// package com.mydev.ecommerce.config;

// import com.mydev.ecommerce.auth.security.JwtAuthFilter;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.HttpMethod;

// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;

// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// @Configuration
// public class SecurityConfig {

//   @Bean
//   public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {

//     http
//       .csrf(csrf -> csrf.disable())
//       .cors(cors -> {})

//       .sessionManagement(sm ->
//           sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//       )

//       .httpBasic(b -> b.disable())
//       .formLogin(f -> f.disable())

//       .authorizeHttpRequests(auth -> auth

//         .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

//         // public routes
//         .requestMatchers("/", "/ping", "/ping-test", "/error").permitAll()

//         .requestMatchers("/api/auth/**").permitAll()

//         .requestMatchers("/api/products/**").permitAll()
//         .requestMatchers("/api/categories/**").permitAll()

//         .requestMatchers("/api/addresses/**").authenticated()
//         .requestMatchers("/api/orders/**").authenticated()
//         .requestMatchers("/api/admin/orders/**").hasRole("ADMIN")

//         // images
//         .requestMatchers("/images/**").permitAll()

//         // admin routes
//         .requestMatchers("/api/admin/**").hasRole("ADMIN")

//         // user routes
//         .requestMatchers("/api/user/**").hasAnyRole("CUSTOMER","ADMIN")


//         .requestMatchers("/api/cart/**").authenticated()

//         // everything else requires auth
//         .anyRequest().authenticated()
//       )

//       .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

//     return http.build();
//   }
// }












package com.mydev.ecommerce.config;

import com.mydev.ecommerce.auth.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic(b -> b.disable())
            .formLogin(f -> f.disable())
            .authorizeHttpRequests(auth -> auth

                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // public routes
                .requestMatchers("/", "/ping", "/ping-test", "/error").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/products/**").permitAll()
                .requestMatchers("/api/categories/**").permitAll()
                .requestMatchers("/images/**").permitAll()

                // admin routes
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // authenticated user routes
                .requestMatchers("/api/addresses/**").authenticated()
                .requestMatchers("/api/orders/**").authenticated()
                .requestMatchers("/api/cart/**").authenticated()
                .requestMatchers("/api/user/**").hasAnyRole("CUSTOMER", "ADMIN")

                // everything else
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}