package com.miaueauau.clinica_veterinaria.config;

import com.miaueauau.clinica_veterinaria.service.UserDetailsServiceImpl;
import com.miaueauau.clinica_veterinaria.repository.UserRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

import java.util.Arrays;

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
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authorize -> authorize
                        // 1. Rotas Públicas (acessíveis sem autenticação)
                        .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/swagger-resources").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Pre-flight requests

                        // 2. Regras de Autorização (requerem autenticação)
                        // Ordem: Mais específica para a mais genérica.

                        // Acesso de Administrador (ROLE_ADMIN): Acesso total para gerenciar tudo
                        // Isso inclui criar, alterar e excluir FUNCIONARIOS (incluindo outros admins)
                        .requestMatchers("/api/funcionarios/**").hasRole("ADMIN") // Apenas ADMIN pode gerenciar Funcionários
                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // Rotas exclusivas para ADMIN
                        // Admin também pode deletar registros (já estava)
                        .requestMatchers(HttpMethod.DELETE, "/api/pacientes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/consultas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/diagnosticos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/exames/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/prescricoes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/arquivos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/procedimentos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/disponibilidades-veterinarios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/tutores/**").hasRole("ADMIN") // Admin também deleta tutores

                        // Acesso de Funcionário (ROLE_FUNCIONARIO):
                        // Podem ALTERAR DADOS DOS USUÁRIOS (Tutores)
                        // Podem gerenciar a clínica (pacientes, consultas, prontuários, etc.)
                        // NÃO podem criar novos funcionários (já coberto acima pelo .hasRole("ADMIN"))
                        .requestMatchers(HttpMethod.GET, "/api/funcionarios/**").hasAnyRole("FUNCIONARIO", "VETERINARIO", "ADMIN") // Podem ver outros funcionários
                        .requestMatchers(HttpMethod.PUT, "/api/funcionarios/{id}").hasAnyRole("FUNCIONARIO", "VETERINARIO", "ADMIN") // Funcionário pode alterar SEU PRÓPRIO DADO (exige lógica no controller)
                        // Podem alterar dados de TUTORES (mas não criar/excluir outros usuários)
                        .requestMatchers(HttpMethod.PUT, "/api/tutores/**").hasAnyRole("FUNCIONARIO", "VETERINARIO", "ADMIN") // Funcionario/Vet podem alterar dados de tutor
                        // Gerenciar clínica (pacientes, consultas, prontuários, etc.)
                        .requestMatchers("/api/pacientes/**").hasAnyRole("FUNCIONARIO", "VETERINARIO", "ADMIN") // CRUD de Pacientes (sem DELETE)
                        .requestMatchers("/api/consultas/**").hasAnyRole("FUNCIONARIO", "VETERINARIO", "ADMIN") // CRUD de Consultas (sem DELETE)
                        .requestMatchers("/api/prontuarios/**").hasAnyRole("FUNCIONARIO", "VETERINARIO", "ADMIN")
                        .requestMatchers("/api/diagnosticos/**").hasAnyRole("FUNCIONARIO", "VETERINARIO", "ADMIN")
                        .requestMatchers("/api/exames/**").hasAnyRole("FUNCIONARIO", "VETERINARIO", "ADMIN")
                        .requestMatchers("/api/prescricoes/**").hasAnyRole("FUNCIONARIO", "VETERINARIO", "ADMIN")
                        .requestMatchers("/api/arquivos/**").hasAnyRole("FUNCIONARIO", "VETERINARIO", "ADMIN")
                        .requestMatchers("/api/procedimentos/**").hasAnyRole("FUNCIONARIO", "VETERINARIO", "ADMIN")
                        .requestMatchers("/api/disponibilidades-veterinarios/**").hasAnyRole("VETERINARIO", "ADMIN") // Vets gerenciam disponibilidades

                        // Acesso de Tutor (ROLE_TUTOR): Restrições específicas (já estava)
                        .requestMatchers(HttpMethod.POST, "/api/pacientes").hasRole("TUTOR") // Tutor pode cadastrar pet novo
                        .requestMatchers(HttpMethod.POST, "/api/consultas").hasRole("TUTOR") // Tutor pode marcar consultas
                        .requestMatchers(HttpMethod.GET, "/api/pacientes/meus-pets").hasRole("TUTOR")
                        .requestMatchers(HttpMethod.GET, "/api/consultas/minhas-consultas").hasRole("TUTOR")
                        .requestMatchers(HttpMethod.GET, "/api/prontuarios/{prontuarioId}").hasRole("TUTOR")
                        .requestMatchers(HttpMethod.GET, "/api/exames/{exameId}").hasRole("TUTOR")


                        // Regra de fallback: Qualquer request não mapeada acima precisa estar autenticada
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
        configuration.setAllowedOrigins(Arrays.asList("http://127.0.0.1:5500", "http://localhost:5500"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}