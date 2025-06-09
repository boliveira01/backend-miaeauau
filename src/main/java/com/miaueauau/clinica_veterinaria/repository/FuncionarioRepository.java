package com.miaueauau.clinica_veterinaria.repository;

import com.miaueauau.clinica_veterinaria.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Importe Optional

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    // Método para buscar um funcionário pelo e-mail
    // Retorna um Optional para lidar com a possibilidade de o e-mail não existir
    Optional<Funcionario> findByEmail(String email);
}