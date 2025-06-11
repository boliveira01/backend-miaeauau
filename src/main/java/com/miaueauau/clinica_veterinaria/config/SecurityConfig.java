package com.miaueauau.clinica_veterinaria.config;

import com.miaueauau.clinica_veterinaria.service.UserDetailsServiceImpl;
import com.miaueauau.clinica_veterinaria.repository.UserRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // Importe este
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays; // Importe este

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final UserRepository userRepository;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService, UserRepository userRepository) {
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public com.miaueauau.clinica_veterinaria.service.AuthService authService() {
        return new com.miaueauau.clinica_veterinaria.service.AuthService(userRepository);
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Habilita CORS
                .authorizeHttpRequests(authorize -> authorize
                        // Rotas Públicas (acessíveis sem autenticação)
                        .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/swagger-resources").permitAll()
                        // Permite pre-flight requests (OPTIONS) para todas as URLs - CRUCIAL PARA CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Regras de Autorização
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/funcionarios/**").hasRole("ADMIN") // Apenas ADMIN pode gerenciar Funcionários
                        .requestMatchers("/api/veterinarios/**").hasRole("ADMIN") // Apenas ADMIN pode gerenciar Veterinários
                        .requestMatchers(HttpMethod.DELETE, "/api/pacientes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/consultas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/diagnosticos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/exames/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/prescricoes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/arquivos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/procedimentos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/disponibilidades-veterinarios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/tutores/**").hasRole("ADMIN")

                        // Acesso de Funcionário/Veterinário
                        .requestMatchers("/api/pacientes/**").hasAnyRole("FUNCIONARIO", "VETERINARIO", "ADMIN")
                        .requestMatchers("/api/tutores/**").hasAnyRole("FUNCIONARIO", "VETERINARIO", "ADMIN")
                        .requestMatchers("/api/consultas/**").hasAnyRole("FUNCIONARIO", "VETERINARIO", "ADMIN")
                        .requestMatchers("/api/prontuarios/**").hasAnyRole("FUNCIONARIO", "VETERINARIO", "ADMIN")
                        .requestMatchers("/api/diagnosticos/**").hasAnyRole("FUNCIONARIO", "VETERINARIO", "ADMIN")
                        .requestMatchers("/api/exames/**").hasAnyRole("FUNCIONARIO", "VETERINARIO", "ADMIN")
                        .requestMatchers("/api/prescricoes/**").hasAnyRole("FUNCIONARIO", "VETERINARIO", "ADMIN")
                        .requestMatchers("/api/arquivos/**").hasAnyRole("FUNCIONARIO", "VETERINARIO", "ADMIN")
                        .requestMatchers("/api/procedimentos/**").hasAnyRole("FUNCIONARIO", "VETERINARIO", "ADMIN")
                        .requestMatchers("/api/disponibilidades-veterinarios/**").hasAnyRole("VETERINARIO", "ADMIN")

                        // Acesso de Tutor
                        .requestMatchers(HttpMethod.POST, "/api/pacientes").hasRole("TUTOR")
                        .requestMatchers(HttpMethod.POST, "/api/consultas").hasRole("TUTOR")
                        .requestMatchers(HttpMethod.GET, "/api/pacientes/meus-pets").hasRole("TUTOR")
                        .requestMatchers(HttpMethod.GET, "/api/consultas/minhas-consultas").hasRole("TUTOR")
                        .requestMatchers(HttpMethod.GET, "/api/prontuarios/{prontuarioId}").hasRole("TUTOR")
                        .requestMatchers(HttpMethod.GET, "/api/exames/{exameId}").hasRole("TUTOR")

                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
        return http.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // ADICIONE AQUI A PORTA DO SEU LIVE SERVER: http://127.0.0.1:5501
        // E TAMBÉM O DOMÍNIO localhost:8080 PARA SWAGGER/POSTMAN
        configuration.setAllowedOrigins(Arrays.asList("http://127.0.0.1:5500", "http://localhost:5500", "http://127.0.0.1:5501", "http://localhost:8080"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}