package com.miaueauau.clinica_veterinaria.service;

import com.miaueauau.clinica_veterinaria.model.Paciente;
import com.miaueauau.clinica_veterinaria.model.Tutor; // Importe a classe Tutor
import com.miaueauau.clinica_veterinaria.repository.PacienteRepository;
import com.miaueauau.clinica_veterinaria.repository.TutorRepository; // Importe o TutorRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private TutorRepository tutorRepository; // NOVO: Injeção do TutorRepository

    public List<Paciente> listarTodosPacientes() {
        return pacienteRepository.findAll();
    }

    public Optional<Paciente> buscarPacientePorId(Long id) {
        return pacienteRepository.findById(id);
    }

    public Paciente salvarPaciente(Paciente paciente) {
        // NOVO: Validação e associação do Tutor ao Paciente
        if (paciente.getTutor() == null || paciente.getTutor().getId() == null) {
            throw new IllegalArgumentException("O paciente deve estar associado a um tutor existente (informe o ID do tutor).");
        }

        // Busca o tutor no banco para garantir que ele existe
        Tutor tutor = tutorRepository.findById(paciente.getTutor().getId())
                .orElseThrow(() -> new RuntimeException("Tutor não encontrado com o ID: " + paciente.getTutor().getId()));

        paciente.setTutor(tutor); // Garante que o objeto Tutor completo está associado ao paciente

        return pacienteRepository.save(paciente);
    }

    // NOVO: Método para atualizar um paciente (com lógica de atualização de tutor se necessário)
    public Paciente atualizarPaciente(Long id, Paciente pacienteAtualizado) {
        return pacienteRepository.findById(id)
                .map(pacienteExistente -> {
                    // Atualiza os campos do paciente existente
                    pacienteExistente.setNome(pacienteAtualizado.getNome());
                    pacienteExistente.setEspecie(pacienteAtualizado.getEspecie());
                    pacienteExistente.setRaca(pacienteAtualizado.getRaca());
                    pacienteExistente.setDataNascimento(pacienteAtualizado.getDataNascimento()); // Se você mudou para dataNascimento
                    // ou: pacienteExistente.setIdade(pacienteAtualizado.getIdade()); // Se você manteve 'idade'
                    pacienteExistente.setPeso(pacienteAtualizado.getPeso());

                    // Se o tutor puder ser alterado durante a atualização:
                    if (pacienteAtualizado.getTutor() != null && pacienteAtualizado.getTutor().getId() != null) {
                        Tutor novoTutor = tutorRepository.findById(pacienteAtualizado.getTutor().getId())
                                .orElseThrow(() -> new RuntimeException("Novo Tutor não encontrado com o ID: " + pacienteAtualizado.getTutor().getId()));
                        pacienteExistente.setTutor(novoTutor);
                    }
                    // Adicione uma lógica se você quiser permitir desassociar um tutor (ex: paciente.setTutor(null);)
                    // Mas, dado que tutor_id é nullable=false, você não pode setar null.

                    return pacienteRepository.save(pacienteExistente);
                })
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado com o ID: " + id));
    }


    public void deletarPaciente(Long id) {
        // NOVO: Adiciona validação para garantir que o paciente existe antes de deletar
        if (!pacienteRepository.existsById(id)) {
            throw new IllegalArgumentException("Paciente com ID " + id + " não encontrado para deleção.");
        }
        pacienteRepository.deleteById(id);
    }

    // NOVO: Método para buscar pacientes por tutor (exemplo, como sugerido no PacienteRepository)
    public List<Paciente> buscarPacientesPorTutor(Tutor tutor) {
        return pacienteRepository.findByTutor(tutor);
    }

    // Ou se preferir buscar por ID do tutor
    public List<Paciente> buscarPacientesPorTutorId(Long tutorId) {
        Tutor tutor = tutorRepository.findById(tutorId)
                .orElseThrow(() -> new RuntimeException("Tutor não encontrado com o ID: " + tutorId));
        return pacienteRepository.findByTutor(tutor);
    }
}