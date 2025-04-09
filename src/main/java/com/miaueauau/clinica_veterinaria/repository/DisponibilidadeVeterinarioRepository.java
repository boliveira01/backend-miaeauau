package com.miaueauau.clinica_veterinaria.repository;

import com.miaueauau.clinica_veterinaria.model.DisponibilidadeVeterinario;
import com.miaueauau.clinica_veterinaria.model.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DisponibilidadeVeterinarioRepository extends JpaRepository<DisponibilidadeVeterinario, Long> {

    List<DisponibilidadeVeterinario> findByVeterinarioAndInicioLessThanAndFimGreaterThan(
            Veterinario veterinario, LocalDateTime fim, LocalDateTime inicio);

    // Podemos adicionar outros métodos de consulta específicos no futuro
}