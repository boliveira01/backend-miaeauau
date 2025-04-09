package com.miaueauau.clinica_veterinaria.service;

import com.miaueauau.clinica_veterinaria.model.Funcionario;
import com.miaueauau.clinica_veterinaria.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FuncionarioService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    public List<Funcionario> listarTodosFuncionarios() {
        return funcionarioRepository.findAll();
    }

    public Optional<Funcionario> buscarFuncionarioPorId(Long id) {
        return funcionarioRepository.findById(id);
    }

    public Funcionario salvarFuncionario(Funcionario funcionario) {
        return funcionarioRepository.save(funcionario);
    }

    public void deletarFuncionario(Long id) {
        funcionarioRepository.deleteById(id);
    }

    // Podemos adicionar métodos de lógica de negócios específicos para Funcionario no futuro
    // Por exemplo: autenticação, buscar por cargo.
}