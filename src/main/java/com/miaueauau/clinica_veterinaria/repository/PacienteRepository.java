package com.miaueauau.clinica_veterinaria.repository;

import com.miaueauau.clinica_veterinaria.model.Paciente;
import com.miaueauau.clinica_veterinaria.model.Tutor; // Importe a classe Tutor
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Importe List

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    // Podemos adicionar métodos de consulta específicos para Paciente no futuro, se necessário
    // Por exemplo:
    List<Paciente> findByEspecie(String especie); // Mantido o exemplo que você já tinha

    // --- NOVO: Método para buscar pacientes por Tutor ---
    List<Paciente> findByTutor(Tutor tutor);
    // Você também pode buscar por ID do tutor diretamente, se preferir:
    // List<Paciente> findByTutorId(Long tutorId);
}