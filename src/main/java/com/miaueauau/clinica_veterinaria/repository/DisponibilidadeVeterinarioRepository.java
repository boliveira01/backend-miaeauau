package com.miaueauau.clinica_veterinaria.repository;

import com.miaueauau.clinica_veterinaria.model.DisponibilidadeVeterinario;
import com.miaueauau.clinica_veterinaria.model.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDateTime; // Mantenha este import por segurança, mas o método principal usa DayOfWeek
import java.util.List;

@Repository
public interface DisponibilidadeVeterinarioRepository extends JpaRepository<DisponibilidadeVeterinario, Long> {

    // Este é o método que o ConsultaService espera.
    List<DisponibilidadeVeterinario> findByVeterinarioAndDiaSemana(Veterinario veterinario, DayOfWeek diaSemana);

    // Se por acaso você ainda tem este método e ele está causando o erro,
    // verifique se ele está com LocalDateTime, e não LocalTime, nos parâmetros de tempo.
    // List<DisponibilidadeVeterinario> findByVeterinarioAndInicioLessThanAndFimGreaterThan(
    //     Veterinario veterinario, LocalDateTime fimConsulta, LocalDateTime inicioConsulta
    // );
}