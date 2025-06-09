package com.miaueauau.clinica_veterinaria.service;

import com.miaueauau.clinica_veterinaria.model.Funcionario; // Importe Funcionario
import com.miaueauau.clinica_veterinaria.model.Veterinario;
import com.miaueauau.clinica_veterinaria.repository.FuncionarioRepository; // Importe FuncionarioRepository
import com.miaueauau.clinica_veterinaria.repository.VeterinarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VeterinarioService {

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository; // NOVO: Injeção do FuncionarioRepository

    public List<Veterinario> listarTodosVeterinarios() {
        return veterinarioRepository.findAll();
    }

    public Optional<Veterinario> buscarVeterinarioPorId(Long id) {
        return veterinarioRepository.findById(id);
    }

    public Veterinario salvarVeterinario(Veterinario veterinario) {
        // NOVO: Validação e associação do Funcionário ao Veterinário
        if (veterinario.getFuncionario() == null || veterinario.getFuncionario().getId() == null) {
            throw new IllegalArgumentException("O veterinário deve ser associado a um funcionário existente (informe o ID do funcionário).");
        }

        // Busca o funcionário no banco para garantir que ele existe
        Funcionario funcionario = funcionarioRepository.findById(veterinario.getFuncionario().getId())
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado com o ID: " + veterinario.getFuncionario().getId()));

        // NOVO: Verifica se o funcionário já está associado a outro veterinário
        if (funcionario.getVeterinario() != null && !funcionario.getVeterinario().getId().equals(veterinario.getId())) {
            throw new IllegalArgumentException("Este funcionário já está cadastrado como veterinário.");
        }

        // NOVO: Validação de unicidade para CRMV antes de salvar
        if (veterinarioRepository.existsByCrmv(veterinario.getCrmv())) {
            throw new IllegalArgumentException("Já existe um veterinário com este CRMV.");
        }

        // Associa o objeto Funcionario completo ao Veterinario
        veterinario.setFuncionario(funcionario);

        // Define o ID do veterinário para ser o mesmo do funcionário, conforme o @MapsId
        veterinario.setId(funcionario.getId());

        // Se o funcionário ainda não tem uma referência ao veterinário, defina-a
        if (funcionario.getVeterinario() == null) {
            funcionario.setVeterinario(veterinario);
            // Salvar o funcionário é importante para persistir a associação bidirecional
            // e garantir que o cascade para Veterinario funcione corretamente.
            // No entanto, como o Veterinario.id já mapeia o Funcionario.id e
            // Funcionario.veterinario tem cascade=ALL e orphanRemoval=true,
            // salvar o Veterinario diretamente pode ser suficiente dependendo da ordem.
            // Vamos testar salvando o veterinario e, se houver problemas de inconsistência,
            // podemos adicionar funcionarioRepository.save(funcionario); aqui.
        }

        return veterinarioRepository.save(veterinario);
    }

    // NOVO: Método para atualizar um veterinário
    public Veterinario atualizarVeterinario(Long id, Veterinario veterinarioAtualizado) {
        return veterinarioRepository.findById(id)
                .map(veterinarioExistente -> {
                    // NOVO: Validação de unicidade para CRMV na atualização
                    if (!veterinarioExistente.getCrmv().equals(veterinarioAtualizado.getCrmv()) && veterinarioRepository.existsByCrmv(veterinarioAtualizado.getCrmv())) {
                        throw new IllegalArgumentException("CRMV já cadastrado para outro veterinário.");
                    }

                    // Atualiza os campos específicos do veterinário
                    veterinarioExistente.setCrmv(veterinarioAtualizado.getCrmv());
                    veterinarioExistente.setEspecialidade(veterinarioAtualizado.getEspecialidade());
                    // Consultas e disponibilidades seriam gerenciadas através de seus próprios serviços ou por aqui,
                    // mas geralmente não são substituídas diretamente no PUT do Veterinario.

                    // Se você permitir a alteração do funcionário associado a um veterinário existente
                    // (o que é menos comum com @MapsId, pois o ID do veterinário é o ID do funcionário),
                    // a lógica seria similar ao salvar: buscar e associar o novo funcionário.
                    // Por simplicidade, vamos assumir que o funcionário associado não muda via PUT aqui.
                    // Caso mude, o ID do veterinário também teria que mudar, o que complica o PUT no mesmo ID.

                    return veterinarioRepository.save(veterinarioExistente);
                })
                .orElseThrow(() -> new RuntimeException("Veterinário não encontrado com o ID: " + id));
    }

    public void deletarVeterinario(Long id) {
        // NOVO: Adiciona validação para garantir que o veterinário existe antes de deletar
        if (!veterinarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Veterinário com ID " + id + " não encontrado para deleção.");
        }
        // Se houver cascade na Consulta para Veterinario, o ideal seria tratar aqui
        // para não deletar consultas indevidamente. Como está com orphanRemoval = true
        // em Veterinario para Consultas e Disponibilidade, a exclusão em cascata ocorrerá.
        veterinarioRepository.deleteById(id);
    }

    // Método para buscar veterinários por especialidade (como sugerido no repositório)
    public List<Veterinario> buscarVeterinariosPorEspecialidade(String especialidade) {
        return veterinarioRepository.findByEspecialidade(especialidade);
    }
}