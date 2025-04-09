package com.miaueauau.clinica_veterinaria.repository;

import com.miaueauau.clinica_veterinaria.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    // Podemos adicionar métodos de consulta específicos para Paciente no futuro, se necessário
    // Por exemplo:
    // List<Paciente> findByEspecie(String especie);
    // List<Paciente> findByTutor(Tutor tutor);
}