package com.miaueauau.clinica_veterinaria.service;

import com.miaueauau.clinica_veterinaria.model.Funcionario;
import com.miaueauau.clinica_veterinaria.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; // Remova o import do PasswordEncoder

import java.util.List;
import java.util.Optional;

@Service
public class FuncionarioService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    // REMOVA ESTAS LINHAS:
    // @Autowired
    // private PasswordEncoder passwordEncoder;

    public List<Funcionario> listarTodosFuncionarios() {
        return funcionarioRepository.findAll();
    }

    public Optional<Funcionario> buscarFuncionarioPorId(Long id) {
        return funcionarioRepository.findById(id);
    }

    public Funcionario salvarFuncionario(Funcionario funcionario) {
        if (funcionarioRepository.findByEmail(funcionario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Já existe um funcionário com este e-mail.");
        }
        // REMOVA A LINHA DE CRIPTOGRAFIA DE SENHA AQUI:
        // funcionario.setSenha(passwordEncoder.encode(funcionario.getSenha()));
        return funcionarioRepository.save(funcionario);
    }

    public Funcionario atualizarFuncionario(Long id, Funcionario funcionarioAtualizado) {
        return funcionarioRepository.findById(id)
                .map(funcionarioExistente -> {
                    funcionarioExistente.setNome(funcionarioAtualizado.getNome());
                    funcionarioExistente.setEmail(funcionarioAtualizado.getEmail());
                    funcionarioExistente.setCargo(funcionarioAtualizado.getCargo());
                    // Adicione outros campos que podem ser atualizados
                    // funcionarioExistente.setTelefone(funcionarioAtualizado.getTelefone());
                    funcionarioExistente.setAdministrador(funcionarioAtualizado.isAdministrador());
                    return funcionarioRepository.save(funcionarioExistente);
                })
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado com o ID: " + id));
    }

    // REMOVA ESTE MÉTODO COMPLETO (não é mais necessário sem PasswordEncoder):
    // public Funcionario atualizarSenhaFuncionario(Long id, String novaSenha) {
    //     return funcionarioRepository.findById(id)
    //             .map(funcionarioExistente -> {
    //                 funcionarioExistente.setSenha(passwordEncoder.encode(novaSenha));
    //                 return funcionarioRepository.save(funcionarioExistente);
    //             })
    //             .orElseThrow(() -> new RuntimeException("Funcionário não encontrado com o ID: " + id));
    // }

    public void deletarFuncionario(Long id) {
        if (!funcionarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Funcionário com ID " + id + " não encontrado para deleção.");
        }
        funcionarioRepository.deleteById(id);
    }
}