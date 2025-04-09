package com.miaueauau.clinica_veterinaria.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(HttpMethod.PUT, "/api/pacientes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/pacientes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/tutores/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/tutores/**").hasRole("ADMIN")
                        .anyRequest().permitAll())
                .httpBasic(withDefaults()); // Habilita a autenticação básica
        return http.build();
    }
}