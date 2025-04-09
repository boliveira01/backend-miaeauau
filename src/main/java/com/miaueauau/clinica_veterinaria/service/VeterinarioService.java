package com.miaueauau.clinica_veterinaria.service;

import com.miaueauau.clinica_veterinaria.model.Veterinario;
import com.miaueauau.clinica_veterinaria.repository.VeterinarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VeterinarioService {

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    public List<Veterinario> listarTodosVeterinarios() {
        return veterinarioRepository.findAll();
    }

    public Optional<Veterinario> buscarVeterinarioPorId(Long id) {
        return veterinarioRepository.findById(id);
    }

    public Veterinario salvarVeterinario(Veterinario veterinario) {
        return veterinarioRepository.save(veterinario);
    }

    public void deletarVeterinario(Long id) {
        veterinarioRepository.deleteById(id);
    }

    // Podemos adicionar métodos de lógica de negócios específicos para Veterinario no futuro
    // Por exemplo: buscar veterinários por especialidade.
}