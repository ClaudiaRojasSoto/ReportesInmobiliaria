package com.example.demo.config;

import com.example.demo.api.filter.JWTAuthenticationFilter;
import com.example.demo.api.filter.JWTAuthorizationFilter;
import com.example.demo.api.service.UserDetailsServiceImpl;
import com.example.demo.api.util.JWTUtil;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    public SecurityConfig(UserDetailsServiceImpl userDetailsServiceImpl, BCryptPasswordEncoder passwordEncoder, JWTUtil jwtUtil) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter(authenticationManager(http.getSharedObject(AuthenticationConfiguration.class)), jwtUtil);
        JWTAuthorizationFilter jwtAuthorizationFilter = new JWTAuthorizationFilter(jwtUtil);

        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/roles").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/roles/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/roles").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/roles/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/roles/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/projects").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.POST, "/api/projects").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.PUT, "/api/projects/{id}").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.DELETE, "/api/projects/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/reports").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/reports/user/{userId}").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.POST, "/api/reports").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.PUT, "/api/reports/{id}").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.DELETE, "/api/reports/{id}").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.GET, "/api/reports/project/{projectId}").hasAnyRole("ADMIN", "USER")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }



    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
