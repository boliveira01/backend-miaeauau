package com.miaueauau.clinica_veterinaria.repository;

import com.miaueauau.clinica_veterinaria.model.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TutorRepository extends JpaRepository<Tutor, Long> {

    // Podemos adicionar métodos de consulta específicos para Tutor no futuro, se necessário
    // Por exemplo:
    // Tutor findByCpf(String cpf);
}