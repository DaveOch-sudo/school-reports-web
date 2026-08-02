package org.andali.schoolreportsweb.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig  {


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity,
            JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        // disable csrf because we are using JWT
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .authorizeHttpRequests(auth -> auth

                        // Public endpoints
                        .requestMatchers(
                                "/api/v1/auth/**"
                        ).permitAll()

                        // Platform management
                        .requestMatchers(
                                "/api/v1/super-admin/**"
                        ).hasRole("SUPER_ADMIN")

                        // Academic setup (managed by school administrators)
                        .requestMatchers(
                                "/api/v1/schools/**",
                                "/api/v1/academic-years/**",
                                "/api/v1/classes/**",
                                "/api/v1/students/**",
                                "/api/v1/subjects/**",
                                "/api/v1/report-templates/**"
                        ).hasAnyRole(
                                "SUPER_ADMIN",
                                "SCHOOL_ADMIN"
                        )

                        // Assessment & report management
                        .requestMatchers(
                                "/api/v1/marksheets/**",
                                "/api/v1/general-marksheets/**"
                        ).hasAnyRole(
                                "SUPER_ADMIN",
                                "SCHOOL_ADMIN",
                                "CLASS_TEACHER",
                                "SUBJECT_TEACHER"
                        )
                        .requestMatchers(
                                "/api/v1/report-runs/**",
                                "/api/v1/report-cards/**"
                        ).hasAnyRole(
                                "SUPER_ADMIN",
                                "SCHOOL_ADMIN",
                                "CLASS_TEACHER"
                        )

                        // Subject teacher specific endpoints
                        .requestMatchers(
                                "/api/v1/subjects/class/**"
                        ).hasRole("SUBJECT_TEACHER")

                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );
        return httpSecurity.build();
    }
}
