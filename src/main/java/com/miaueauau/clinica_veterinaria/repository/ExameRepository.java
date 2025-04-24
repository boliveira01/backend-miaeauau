package com.miaueauau.clinica_veterinaria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.miaueauau.clinica_veterinaria.model.Exame; // Importe a sua entidade Exame

public interface ExameRepository extends JpaRepository<Exame, Long> {
    // Você pode adicionar métodos de consulta personalizados aqui, se necessário
}