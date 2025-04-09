package com.miaueauau.clinica_veterinaria.service;

import com.miaueauau.clinica_veterinaria.model.Paciente;
import com.miaueauau.clinica_veterinaria.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    public List<Paciente> listarTodosPacientes() {
        return pacienteRepository.findAll();
    }

    public Optional<Paciente> buscarPacientePorId(Long id) {
        return pacienteRepository.findById(id);
    }

    public Paciente salvarPaciente(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

    public void deletarPaciente(Long id) {
        pacienteRepository.deleteById(id);
    }

    // Podemos adicionar mais métodos de lógica de negócios para Paciente no futuro
    // Por exemplo: buscar pacientes por tutor, por espécie, etc.
}