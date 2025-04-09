package com.miaueauau.clinica_veterinaria.service;

import com.miaueauau.clinica_veterinaria.model.Tutor;
import com.miaueauau.clinica_veterinaria.repository.TutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TutorService {

    @Autowired
    private TutorRepository tutorRepository;

    public List<Tutor> listarTodosTutores() {
        return tutorRepository.findAll();
    }

    public Optional<Tutor> buscarTutorPorId(Long id) {
        return tutorRepository.findById(id);
    }

    public Tutor salvarTutor(Tutor tutor) {
        return tutorRepository.save(tutor);
    }

    public void deletarTutor(Long id) {
        tutorRepository.deleteById(id);
    }

    // Podemos adicionar mais métodos de lógica de negócios para Tutor no futuro
    // Por exemplo: validar CPF antes de salvar, etc.
}