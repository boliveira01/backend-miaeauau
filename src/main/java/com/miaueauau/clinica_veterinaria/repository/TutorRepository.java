package com.miaueauau.clinica_veterinaria.repository;

import com.miaueauau.clinica_veterinaria.model.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Importe Optional

@Repository
public interface TutorRepository extends JpaRepository<Tutor, Long> {

    // --- NOVO: Métodos para buscar e verificar Tutores por CPF e E-mail ---
    Optional<Tutor> findByCpf(String cpf); // Busca um tutor por CPF
    Optional<Tutor> findByEmail(String email); // Busca um tutor por E-mail

    boolean existsByCpf(String cpf); // Verifica se um tutor com o CPF já existe
    boolean existsByEmail(String email); // Verifica se um tutor com o E-mail já existe
}