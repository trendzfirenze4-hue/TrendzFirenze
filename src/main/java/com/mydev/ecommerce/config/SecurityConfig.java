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

//     @Bean
//     public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {

//         http
//             .csrf(csrf -> csrf.disable())
//             .cors(cors -> {})
//             .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//             .httpBasic(b -> b.disable())
//             .formLogin(f -> f.disable())
//             .authorizeHttpRequests(auth -> auth

//                 .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

//                 .requestMatchers("/", "/ping", "/warmup", "/ping-test", "/error").permitAll()
//                 .requestMatchers("/api/auth/**").permitAll()
//                 .requestMatchers("/api/products/**").permitAll()
//                 .requestMatchers("/api/categories/**").permitAll()
//                 .requestMatchers("/images/**").permitAll()
//                 .requestMatchers("/api/brand-showcases/**").permitAll()
//                 .requestMatchers("/api/hero-sections/**").permitAll()
//                 .requestMatchers("/api/gift-boxes/**").permitAll()
//                 .requestMatchers("/api/giftsets/**").permitAll()
//                 .requestMatchers(HttpMethod.POST, "/api/bulk-orders").permitAll()
//                 .requestMatchers("/api/admin/bulk-orders/**").hasRole("ADMIN")

//                 .requestMatchers(HttpMethod.POST, "/api/admin/instagram/refresh-token").permitAll()
//                 .requestMatchers(HttpMethod.POST, "/api/admin/instagram/update-token").permitAll()
//                 .requestMatchers(HttpMethod.POST, "/api/admin/instagram/bootstrap-token").permitAll()
//                 .requestMatchers(HttpMethod.GET, "/api/admin/instagram/ping").permitAll()
//                 .requestMatchers(HttpMethod.GET, "/api/admin/instagram/status").permitAll()
//                 .requestMatchers("/api/instagram/**").permitAll()

//                 .requestMatchers("/api/admin/gift-boxes/**").hasRole("ADMIN")
//                 .requestMatchers("/api/admin/hero-sections/**").hasRole("ADMIN")
//                 .requestMatchers("/api/admin/**").hasRole("ADMIN")

//                 .requestMatchers("/api/addresses/**").authenticated()
//                 .requestMatchers("/api/orders/**").authenticated()
//                 .requestMatchers("/api/cart/**").authenticated()
//                 .requestMatchers("/api/giftset-cart/**").authenticated()
//                 .requestMatchers("/api/user/**").hasAnyRole("USER", "CUSTOMER", "ADMIN")

//                 .anyRequest().authenticated()
//             )
//             .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

//         return http.build();
//     }
// }








































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

//     @Bean
//     public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {

//         http
//             .csrf(csrf -> csrf.disable())
//             .cors(cors -> {})
//             .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//             .httpBasic(b -> b.disable())
//             .formLogin(f -> f.disable())
//             .authorizeHttpRequests(auth -> auth

//                 .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

//                 .requestMatchers("/", "/ping", "/warmup", "/ping-test", "/error").permitAll()

//                 .requestMatchers("/api/auth/**").permitAll()
//                 .requestMatchers("/api/guest-checkout/**").permitAll()

//                 .requestMatchers("/api/products/**").permitAll()
//                 .requestMatchers("/api/categories/**").permitAll()
//                 .requestMatchers("/images/**").permitAll()
//                 .requestMatchers("/api/brand-showcases/**").permitAll()
//                 .requestMatchers("/api/hero-sections/**").permitAll()
//                 .requestMatchers("/api/gift-boxes/**").permitAll()
//                 .requestMatchers("/api/giftsets/**").permitAll()

//                 .requestMatchers(HttpMethod.POST, "/api/bulk-orders").permitAll()
//                 .requestMatchers("/api/admin/bulk-orders/**").hasRole("ADMIN")

//                 .requestMatchers(HttpMethod.POST, "/api/admin/instagram/refresh-token").permitAll()
//                 .requestMatchers(HttpMethod.POST, "/api/admin/instagram/update-token").permitAll()
//                 .requestMatchers(HttpMethod.POST, "/api/admin/instagram/bootstrap-token").permitAll()
//                 .requestMatchers(HttpMethod.GET, "/api/admin/instagram/ping").permitAll()
//                 .requestMatchers(HttpMethod.GET, "/api/admin/instagram/status").permitAll()
//                 .requestMatchers("/api/instagram/**").permitAll()

//                 .requestMatchers("/api/admin/gift-boxes/**").hasRole("ADMIN")
//                 .requestMatchers("/api/admin/hero-sections/**").hasRole("ADMIN")
//                 .requestMatchers("/api/admin/**").hasRole("ADMIN")

//                 .requestMatchers("/api/addresses/**").authenticated()
//                 .requestMatchers("/api/orders/**").authenticated()
//                 .requestMatchers("/api/cart/**").authenticated()
//                 .requestMatchers("/api/giftset-cart/**").authenticated()
//                 .requestMatchers("/api/user/**").hasAnyRole("USER", "CUSTOMER", "ADMIN")

//                 .anyRequest().authenticated()
//             )
//             .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

//         return http.build();
//     }
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
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            JwtAuthFilter jwtAuthFilter
    ) throws Exception {

        http

            /* =========================================
               CSRF / CORS
            ========================================= */

            .csrf(csrf -> csrf.disable())

            .cors(cors -> {})

            /* =========================================
               STATELESS JWT
            ========================================= */

            .sessionManagement(sm ->
                    sm.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )

            .httpBasic(b -> b.disable())

            .formLogin(f -> f.disable())

            /* =========================================
               AUTHORIZATION
            ========================================= */

            .authorizeHttpRequests(auth -> auth

                /* =====================================
                   OPTIONS
                ===================================== */

                .requestMatchers(
                        HttpMethod.OPTIONS,
                        "/**"
                ).permitAll()

                /* =====================================
                   SYSTEM
                ===================================== */

                .requestMatchers(
                        "/",
                        "/ping",
                        "/warmup",
                        "/ping-test",
                        "/error"
                ).permitAll()

                /* =====================================
                   AUTH
                ===================================== */

                .requestMatchers(
                        "/api/auth/**"
                ).permitAll()

                .requestMatchers(
                        "/api/guest-checkout/**"
                ).permitAll()

                /* =====================================
                   PUBLIC SHOP
                ===================================== */

                .requestMatchers(
                        "/api/products/**"
                ).permitAll()

                .requestMatchers(
                        "/api/categories/**"
                ).permitAll()

                .requestMatchers(
                        "/images/**"
                ).permitAll()

                .requestMatchers(
                        "/api/brand-showcases/**"
                ).permitAll()

                .requestMatchers(
                        "/api/hero-sections/**"
                ).permitAll()

                .requestMatchers(
                        "/api/gift-boxes/**"
                ).permitAll()

                .requestMatchers(
                        "/api/giftsets/**"
                ).permitAll()

                .requestMatchers(
                        "/api/instagram/**"
                ).permitAll()

                /* =====================================
                   BULK ORDER
                ===================================== */

                .requestMatchers(
                        HttpMethod.POST,
                        "/api/bulk-orders"
                ).permitAll()

                .requestMatchers(
                        "/api/admin/bulk-orders/**"
                ).hasRole("ADMIN")

                /* =====================================
                   INSTAGRAM ADMIN
                ===================================== */

                .requestMatchers(
                        HttpMethod.POST,
                        "/api/admin/instagram/refresh-token"
                ).permitAll()

                .requestMatchers(
                        HttpMethod.POST,
                        "/api/admin/instagram/update-token"
                ).permitAll()

                .requestMatchers(
                        HttpMethod.POST,
                        "/api/admin/instagram/bootstrap-token"
                ).permitAll()

                .requestMatchers(
                        HttpMethod.GET,
                        "/api/admin/instagram/ping"
                ).permitAll()

                .requestMatchers(
                        HttpMethod.GET,
                        "/api/admin/instagram/status"
                ).permitAll()

                /* =====================================
                   ADMIN
                ===================================== */

                .requestMatchers(
                        "/api/admin/gift-boxes/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                        "/api/admin/hero-sections/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                        "/api/admin/**"
                ).hasRole("ADMIN")

                /* =====================================
                   CUSTOMER AUTH REQUIRED
                ===================================== */

                .requestMatchers(
                        "/api/addresses/**"
                ).authenticated()

                .requestMatchers(
                        "/api/orders/**"
                ).authenticated()

                .requestMatchers(
                        "/api/cart/**"
                ).authenticated()

                .requestMatchers(
                        "/api/giftset-cart/**"
                ).authenticated()

                /* =====================================
                   USER PROFILE
                ===================================== */

                .requestMatchers(
                        "/api/user/**"
                ).hasAnyRole(
                        "CUSTOMER",
                        "ADMIN"
                )

                /* =====================================
                   EVERYTHING ELSE
                ===================================== */

                .anyRequest()
                .authenticated()
            )

            /* =========================================
               JWT FILTER
            ========================================= */

            .addFilterBefore(
                    jwtAuthFilter,
                    UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}