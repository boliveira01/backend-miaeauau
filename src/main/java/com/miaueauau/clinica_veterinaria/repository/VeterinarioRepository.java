package com.miaueauau.clinica_veterinaria.repository;

import com.miaueauau.clinica_veterinaria.model.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    // Podemos adicionar métodos de consulta específicos para Veterinario no futuro, se necessário
    // Por exemplo:
    // List<Veterinario> findByEspecialidade(String especialidade);
    // Veterinario findByCrmv(String crmv);
}