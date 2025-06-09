package com.miaueauau.clinica_veterinaria.repository;

import com.miaueauau.clinica_veterinaria.model.Consulta;
import com.miaueauau.clinica_veterinaria.model.Paciente;
import com.miaueauau.clinica_veterinaria.model.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    // Buscar consultas por paciente
    List<Consulta> findByPaciente(Paciente paciente);

    // Buscar consultas por um veterinário em um período específico
    List<Consulta> findByVeterinarioAndDataHoraBetween(Veterinario veterinario, LocalDateTime inicio, LocalDateTime fim);

    // Buscar consultas em um período específico (geral, sem filtrar por vet ou paciente)
    List<Consulta> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);

    // NOVO (OU CORRIGIDO): Buscar consultas por paciente em um período específico
    List<Consulta> findByPacienteAndDataHoraBetween(Paciente paciente, LocalDateTime inicio, LocalDateTime fim); // ESSA É A LINHA QUE FALTAVA/PROBLEMA

    // Buscar consultas por veterinário e data/hora exata (útil para verificar conflitos)
    Optional<Consulta> findByVeterinarioAndDataHora(Veterinario veterinario, LocalDateTime dataHora);

    // Buscar consultas por paciente e data/hora exata (útil para verificar conflitos)
    Optional<Consulta> findByPacienteAndDataHora(Paciente paciente, LocalDateTime dataHora);

    // Verificar se existe alguma consulta para um veterinário em um determinado horário, excluindo uma consulta específica
    boolean existsByVeterinarioAndDataHoraAndIdNot(Veterinario veterinario, LocalDateTime dataHora, Long id);

    // Verificar se existe alguma consulta para um paciente em um determinado horário, excluindo uma consulta específica
    boolean existsByPacienteAndDataHoraAndIdNot(Paciente paciente, LocalDateTime dataHora, Long id);

    // Verificar se existe alguma consulta para um veterinário em um determinado horário (ao CRIAR uma consulta)
    boolean existsByVeterinarioAndDataHora(Veterinario veterinario, LocalDateTime dataHora);

    // Verificar se existe alguma consulta para um paciente em um determinado horário (ao CRIAR uma consulta)
    boolean existsByPacienteAndDataHora(Paciente paciente, LocalDateTime dataHora);

    // Buscar todas as consultas confirmadas para um veterinário em um período
    List<Consulta> findByVeterinarioAndConfirmadaTrueAndDataHoraBetween(Veterinario veterinario, LocalDateTime inicio, LocalDateTime fim);

    // Buscar todas as consultas pendentes (não confirmadas) para um veterinário
    List<Consulta> findByVeterinarioAndConfirmadaFalse(Veterinario veterinario);

    // Buscar consultas confirmadas ou não confirmadas para um paciente
    List<Consulta> findByPacienteAndConfirmada(Paciente paciente, boolean confirmada);
}