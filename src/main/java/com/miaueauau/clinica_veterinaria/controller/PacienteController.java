package com.miaueauau.clinica_veterinaria.controller;

import com.miaueauau.clinica_veterinaria.model.Paciente;
import com.miaueauau.clinica_veterinaria.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @GetMapping
    public ResponseEntity<List<Paciente>> listarTodosPacientes() {
        List<Paciente> pacientes = pacienteService.listarTodosPacientes();
        return new ResponseEntity<>(pacientes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paciente> buscarPacientePorId(@PathVariable Long id) {
        Optional<Paciente> paciente = pacienteService.buscarPacientePorId(id);
        return paciente.map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)); // Retorna 404 se não encontrado
    }

    @PostMapping
    public ResponseEntity<Paciente> salvarPaciente(@RequestBody Paciente paciente) {
        try {
            Paciente novoPaciente = pacienteService.salvarPaciente(paciente);
            return new ResponseEntity<>(novoPaciente, HttpStatus.CREATED); // Retorna 201 Created
        } catch (IllegalArgumentException e) {
            // Captura exceções como "O paciente deve estar associado a um tutor"
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Retorna 400 Bad Request
        } catch (RuntimeException e) {
            // Captura exceções como "Tutor não encontrado"
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Retorna 404 Not Found (ou 400, dependendo da semântica preferida)
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Paciente> atualizarPaciente(@PathVariable Long id, @RequestBody Paciente pacienteAtualizado) {
        try {
            // Chama o novo método atualizarPaciente do service
            Paciente pacienteSalvo = pacienteService.atualizarPaciente(id, pacienteAtualizado);
            return new ResponseEntity<>(pacienteSalvo, HttpStatus.OK); // Retorna 200 OK
        } catch (RuntimeException e) {
            // Captura exceções como "Paciente não encontrado" ou "Novo Tutor não encontrado"
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Retorna 404 Not Found
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPaciente(@PathVariable Long id) {
        try {
            pacienteService.deletarPaciente(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Retorna 204 No Content
        } catch (IllegalArgumentException e) {
            // Captura exceções como "Paciente com ID X não encontrado para deleção"
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Retorna 404 Not Found
        }
    }
}