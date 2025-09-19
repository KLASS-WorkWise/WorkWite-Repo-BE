package com.example.WorkWite_Repo_BE.config;

import com.example.WorkWite_Repo_BE.exceptions.CustomAccessDeniedHandler;
import com.example.WorkWite_Repo_BE.exceptions.CustomAuthenticationEntryPoint;
import com.example.WorkWite_Repo_BE.filters.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

// open api config
                                                .authorizeHttpRequests(auth -> auth
                                                                .requestMatchers(
                                                                        "/swagger-ui.html",
                                                                        "/swagger-ui/**",
                                                                        "/docs/**",
                                                                        "/api-docs/**",
                                                                        "/v3/api-docs.yaml"
                                                                ).permitAll()


                                .requestMatchers("/uploads/**").permitAll()
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/api/public/**").permitAll()
                                .requestMatchers(HttpMethod.PATCH, "/api/users/**").authenticated()
//                                                                .requestMatchers(HttpMethod.PATCH, "/api/users/**").hasAnyRole("ADMIN", "MANAGER")
                                .requestMatchers(HttpMethod.DELETE, "/api/users/**").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/users/**").permitAll()
                                .requestMatchers("/api/employers/**").permitAll()
                                .requestMatchers("/api/candidates/**").permitAll()
                                .requestMatchers("/api/company/**").permitAll()
                                .requestMatchers("/api/roles/**").permitAll()
                                .requestMatchers("/api/resumes/**").permitAll()
//.requestMatchers("/api/job-postings/**").authenticated()

                                        // Job postings
                                        .requestMatchers(HttpMethod.GET, "/api/job-postings/**").permitAll()
                                        .requestMatchers(HttpMethod.PUT, "/api/job-postings/**").permitAll()
                                        .requestMatchers(HttpMethod.DELETE, "/api/job-postings/**").permitAll()
                                        .requestMatchers(HttpMethod.POST, "/api/job-postings/**").authenticated()

                                .requestMatchers("/api/applicant/**").permitAll()
                                .requestMatchers("/api/upload/multiple**").permitAll()
                                .requestMatchers("/api/statistics/**").permitAll()
                                .requestMatchers("/api/admin/**").permitAll()
                                .requestMatchers("/api/saved-jobs/**").permitAll()
                                .requestMatchers("/api/vnpay/**").permitAll()
                                .requestMatchers("/api/recommend/**").permitAll()
                                        .requestMatchers(HttpMethod.PATCH, "/api/banners/**").permitAll()
                                        .requestMatchers(HttpMethod.POST, "/api/banners/**").authenticated()
                                        .requestMatchers(HttpMethod.GET, "/api/banners/**").permitAll()
                                        .requestMatchers(HttpMethod.DELETE, "/api/banners/**").permitAll()
                                        .requestMatchers("/api/banners/**").permitAll()
                                        .requestMatchers("/api/upload/**").permitAll()
                                                                .requestMatchers("/api/system-logs/**").permitAll()
                                .requestMatchers("/api/categories/**").permitAll()
                                .requestMatchers("/api/blogs/**").permitAll()
                                .requestMatchers("/api/aboutus/**").permitAll()
                                .requestMatchers("/api/ourteam/**").permitAll()

                        )



                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
