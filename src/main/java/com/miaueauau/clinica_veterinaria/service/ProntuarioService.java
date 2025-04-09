package com.miaueauau.clinica_veterinaria.service;

import com.miaueauau.clinica_veterinaria.model.Prontuario;
import com.miaueauau.clinica_veterinaria.repository.ProntuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProntuarioService {

    @Autowired
    private ProntuarioRepository prontuarioRepository;

    public List<Prontuario> listarTodosProntuarios() {
        return prontuarioRepository.findAll();
    }

    public Optional<Prontuario> buscarProntuarioPorId(Long id) {
        return prontuarioRepository.findById(id);
    }

    public Prontuario salvarProntuario(Prontuario prontuario) {
        return prontuarioRepository.save(prontuario);
    }

    public void deletarProntuario(Long id) {
        prontuarioRepository.deleteById(id);
    }

    // Outros métodos de lógica de negócios, se necessário
}