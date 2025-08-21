package com.example.WorkWite_Repo_BE.config;

import com.example.WorkWite_Repo_BE.exceptions.CustomAccessDeniedHandler;
import com.example.WorkWite_Repo_BE.exceptions.CustomAuthenticationEntryPoint;
import com.example.WorkWite_Repo_BE.filters.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity()
@RequiredArgsConstructor
public class SecurityConfig {
        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final CustomAccessDeniedHandler customAccessDeniedHandler;
        private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http.csrf(AbstractHttpConfigurer::disable)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .exceptionHandling(exceptionHandlingCustomizer -> exceptionHandlingCustomizer
                                                .authenticationEntryPoint(this.customAuthenticationEntryPoint)
                                                .accessDeniedHandler(this.customAccessDeniedHandler))
//                                .authorizeHttpRequests(auth -> auth
//                                                .requestMatchers("/api/auth/**").permitAll()
//                                                .requestMatchers("/api/public/**").permitAll()
//                                                .requestMatchers("/api/users/**")
//                                                .hasAnyRole("Administrators", "Managers")
//                                                .anyRequest().permitAll())

                        .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/api/public/**").permitAll()
                                .requestMatchers("/api/users/**").permitAll()
                                .requestMatchers("/api/employers/**").permitAll()
                                .requestMatchers("/api/company/**").permitAll()
                                .requestMatchers("/api/roles/**").permitAll()
                                .requestMatchers("/api/resumes/**").permitAll()
                        )
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
