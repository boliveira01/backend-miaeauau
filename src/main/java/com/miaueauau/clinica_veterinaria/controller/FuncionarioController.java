package com.miaueauau.clinica_veterinaria.controller;

import com.miaueauau.clinica_veterinaria.model.Funcionario;
import com.miaueauau.clinica_veterinaria.service.FuncionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioController {

    @Autowired
    private FuncionarioService funcionarioService;

    @GetMapping
    public ResponseEntity<List<Funcionario>> listarTodosFuncionarios() {
        List<Funcionario> funcionarios = funcionarioService.listarTodosFuncionarios();
        return new ResponseEntity<>(funcionarios, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Funcionario> buscarFuncionarioPorId(@PathVariable Long id) {
        Optional<Funcionario> funcionario = funcionarioService.buscarFuncionarioPorId(id);
        return funcionario.map(f -> new ResponseEntity<>(f, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)); // Retorna 404 se não encontrado
    }

    @PostMapping
    public ResponseEntity<Funcionario> salvarFuncionario(@RequestBody Funcionario funcionario) {
        try {
            Funcionario novoFuncionario = funcionarioService.salvarFuncionario(funcionario);
            return new ResponseEntity<>(novoFuncionario, HttpStatus.CREATED); // Retorna 201 Created
        } catch (IllegalArgumentException e) {
            // Captura exceções como "Já existe um funcionário com este e-mail."
            return new ResponseEntity<>(HttpStatus.CONFLICT); // Retorna 409 Conflict
        } catch (Exception e) {
            // Captura outras exceções inesperadas ao salvar (ex: campos inválidos)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Retorna 400 Bad Request
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Funcionario> atualizarFuncionario(@PathVariable Long id, @RequestBody Funcionario funcionarioAtualizado) {
        try {
            Funcionario funcionarioSalvo = funcionarioService.atualizarFuncionario(id, funcionarioAtualizado);
            return new ResponseEntity<>(funcionarioSalvo, HttpStatus.OK); // Retorna 200 OK
        } catch (IllegalArgumentException e) { // <-- AGORA ESTÁ NA ORDEM CORRETA: ESPECÍFICO PRIMEIRO
            // Captura exceções de validação, como e-mail já existente para outro funcionário
            return new ResponseEntity<>(HttpStatus.CONFLICT); // Retorna 409 Conflict
        } catch (RuntimeException e) { // <-- MAIS GENÉRICO DEPOIS
            // Captura exceções como "Funcionário não encontrado com o ID: "
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Retorna 404 Not Found
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarFuncionario(@PathVariable Long id) {
        try {
            funcionarioService.deletarFuncionario(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Retorna 204 No Content
        } catch (IllegalArgumentException e) {
            // Captura exceções como "Funcionário com ID X não encontrado para deleção."
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Retorna 404 Not Found
        }
    }
}