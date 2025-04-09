package com.miaueauau.clinica_veterinaria.repository;

import com.miaueauau.clinica_veterinaria.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    // Podemos adicionar métodos de consulta específicos para Funcionario no futuro, se necessário
    // Por exemplo:
    // Funcionario findByEmail(String email);
}