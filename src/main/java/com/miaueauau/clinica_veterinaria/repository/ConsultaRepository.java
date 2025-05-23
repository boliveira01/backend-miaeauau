package com.miaueauau.clinica_veterinaria.repository;

import com.miaueauau.clinica_veterinaria.model.Consulta;
import com.miaueauau.clinica_veterinaria.model.Paciente;
import com.miaueauau.clinica_veterinaria.model.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    // Podemos adicionar métodos de consulta específicos para Consulta no futuro, se necessário
    // Por exemplo:
    // List<Consulta> findByPaciente(Paciente paciente);
    // List<Consulta> findByVeterinarioAndDataHoraBetween(Veterinario veterinario, LocalDateTime inicio, LocalDateTime fim);

        List<Consulta> findByPaciente(Paciente paciente);
        // Outros métodos do repositório ...

        List<Consulta> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);

    List<Consulta> findByVeterinarioAndDataHoraBetween(Veterinario veterinario, LocalDateTime inicio, LocalDateTime fim);
}