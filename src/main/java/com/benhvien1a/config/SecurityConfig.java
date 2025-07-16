
/*
         * @ (#) SecurityConfig.java 1.0 7/12/2025
         *
         * Copyright (c) 2025 IUH. All rights reserved
 */
package com.benhvien1a.config;

import com.benhvien1a.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/*
 * @description: Security configuration for JWT-based authentication and CORS
 * @author: Nguyen Truong An
 * @date: 7/12/2025
 * @version: 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // PUBLIC: Các endpoint không cần đăng nhập
                        .requestMatchers("/api/v1/appointments","/api/v1/auth/login", "/api/v1/auth/forgot-password").permitAll()
                        .requestMatchers( "/api/v1/articles/public").permitAll()
                        .requestMatchers( "/api/v1/doctors/public").permitAll()
                        .requestMatchers( "/api/v1/departments/public").permitAll()
                        // Tất cả GET công khai
                        .requestMatchers(HttpMethod.POST, "/api/v1/appointments").permitAll() // POST đăng ký lịch khám công khai
                        // EDITOR: Quyền truy cập các endpoint liên quan
                        .requestMatchers("/api/v1/articles/**", "/api/v1/departments/**", "/api/v1/doctors/**",
                                "/api/v1/services/**", "/api/v1/menus/**", "/api/v1/service-prices/**",
                                "/api/v1/categories/**")
                        .hasAnyRole("EDITOR", "ADMIN")
                        // AUTH: Giữ nguyên quyền cho các endpoint xác thực
                        .requestMatchers(
                                "/api/v1/auth/resend-verification",
                                "/api/v1/auth/reset-password",
                                "/api/v1/auth/change-password"
                        ).hasAnyRole("EDITOR", "ADMIN")
                        // ADMIN: Toàn quyền cho các endpoint còn lại
                        .requestMatchers("/api/v1/**").hasRole("ADMIN")
                        // Còn lại phải đăng nhập
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}