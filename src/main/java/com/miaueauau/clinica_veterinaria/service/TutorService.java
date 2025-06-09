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
        // NOVO: Validação de unicidade para CPF e Email antes de salvar
        if (tutorRepository.existsByCpf(tutor.getCpf())) {
            throw new IllegalArgumentException("Já existe um tutor com este CPF.");
        }
        // A validação de email é importante se o campo 'email' existe na sua entidade Tutor
        // Se você adicionou 'email' na entidade Tutor, descomente a linha abaixo
        if (tutor.getEmail() != null && tutorRepository.existsByEmail(tutor.getEmail())) {
            throw new IllegalArgumentException("Já existe um tutor com este e-mail.");
        }
        return tutorRepository.save(tutor);
    }

    // NOVO: Método para atualizar um tutor existente
    public Tutor atualizarTutor(Long id, Tutor tutorAtualizado) {
        return tutorRepository.findById(id)
                .map(tutorExistente -> {
                    // Validação de unicidade para CPF e Email na atualização
                    // Verifica se o CPF foi alterado E se o novo CPF já existe para OUTRO tutor
                    if (!tutorExistente.getCpf().equals(tutorAtualizado.getCpf()) && tutorRepository.existsByCpf(tutorAtualizado.getCpf())) {
                        throw new IllegalArgumentException("CPF já cadastrado para outro tutor.");
                    }
                    // Verifica se o email foi alterado E se o novo email já existe para OUTRO tutor
                    if (tutorAtualizado.getEmail() != null && !tutorExistente.getEmail().equals(tutorAtualizado.getEmail()) && tutorRepository.existsByEmail(tutorAtualizado.getEmail())) {
                        throw new IllegalArgumentException("E-mail já cadastrado para outro tutor.");
                    }

                    // Atualiza os campos
                    tutorExistente.setNome(tutorAtualizado.getNome());
                    tutorExistente.setCpf(tutorAtualizado.getCpf());
                    tutorExistente.setEndereco(tutorAtualizado.getEndereco());
                    tutorExistente.setTelefone(tutorAtualizado.getTelefone());
                    tutorExistente.setDataNascimento(tutorAtualizado.getDataNascimento());
                    // Se você adicionou 'email' na entidade Tutor
                    tutorExistente.setEmail(tutorAtualizado.getEmail());

                    return tutorRepository.save(tutorExistente);
                })
                .orElseThrow(() -> new RuntimeException("Tutor não encontrado com o ID: " + id));
    }

    public void deletarTutor(Long id) {
        // NOVO: Adiciona validação para garantir que o tutor existe antes de deletar
        if (!tutorRepository.existsById(id)) {
            throw new IllegalArgumentException("Tutor com ID " + id + " não encontrado para deleção.");
        }
        tutorRepository.deleteById(id);
    }

    // Podemos adicionar mais métodos de lógica de negócios para Tutor no futuro
}