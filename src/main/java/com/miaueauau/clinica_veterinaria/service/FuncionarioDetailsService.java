package com.miaueauau.clinica_veterinaria.service;

import com.miaueauau.clinica_veterinaria.model.Funcionario;
import com.miaueauau.clinica_veterinaria.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User; // Importe esta classe do Spring Security
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService; // Importe esta interface
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class FuncionarioDetailsService implements UserDetailsService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Busca o funcionário pelo e-mail (que será o "username" para o Spring Security)
        Funcionario funcionario = funcionarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Funcionário não encontrado com o e-mail: " + email));

        // Cria a lista de GrantedAuthorities (papéis/roles) para o funcionário
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (funcionario.isAdministrador()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN")); // Adiciona a role ADMIN
        }
        // Se você tiver outras roles (ex: veterinario, recepcionista), adicione aqui
        // authorities.add(new SimpleGrantedAuthority("ROLE_USER")); // Exemplo de role padrão para todos

        // Retorna um objeto UserDetails do Spring Security
        return new User(
                funcionario.getEmail(),    // Username: o e-mail do funcionário
                funcionario.getSenha(),    // Password: a senha do funcionário (em texto puro, pois estamos com NoOpPasswordEncoder)
                authorities                // Roles do funcionário
        );
    }
}