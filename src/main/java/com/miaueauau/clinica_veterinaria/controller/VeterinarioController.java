package com.miaueauau.clinica_veterinaria.controller;

import com.miaueauau.clinica_veterinaria.model.Veterinario;
import com.miaueauau.clinica_veterinaria.service.VeterinarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // Importe para coletar mensagens de erro, se desejar mais detalhes

@RestController
@RequestMapping("/api/veterinarios")
public class VeterinarioController {

    @Autowired
    private VeterinarioService veterinarioService;

    @GetMapping
    public ResponseEntity<List<Veterinario>> listarTodosVeterinarios() {
        List<Veterinario> veterinarios = veterinarioService.listarTodosVeterinarios();
        return new ResponseEntity<>(veterinarios, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Veterinario> buscarVeterinarioPorId(@PathVariable Long id) {
        Optional<Veterinario> veterinario = veterinarioService.buscarVeterinarioPorId(id);
        return veterinario.map(v -> new ResponseEntity<>(v, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)); // Retorna 404 se não encontrado
    }

    @PostMapping
    public ResponseEntity<?> salvarVeterinario(@Valid @RequestBody Veterinario veterinario, BindingResult result) {
        if (result.hasErrors()) {
            // Coleta as mensagens de erro detalhadas das validações @NotBlank, @Size etc.
            List<String> errors = result.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST); // Retorna 400 com as mensagens de erro
        }
        try {
            Veterinario novoVeterinario = veterinarioService.salvarVeterinario(veterinario);
            return new ResponseEntity<>(novoVeterinario, HttpStatus.CREATED); // Retorna 201 Created
        } catch (IllegalArgumentException e) {
            // Captura exceções como "CRMV já existe", "Funcionário já é veterinário", "Funcionário não associado"
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // Retorna 409 Conflict com a mensagem de erro
        } catch (RuntimeException e) {
            // Captura exceções como "Funcionário não encontrado"
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // Retorna 404 Not Found com a mensagem de erro
        } catch (Exception e) {
            // Captura outras exceções inesperadas
            return new ResponseEntity<>("Erro interno do servidor ao salvar veterinário.", HttpStatus.INTERNAL_SERVER_ERROR); // Retorna 500 Internal Server Error
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarVeterinario(@PathVariable Long id, @Valid @RequestBody Veterinario veterinarioAtualizado, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        try {
            // Chama o novo método atualizarVeterinario do service
            Veterinario veterinarioSalvo = veterinarioService.atualizarVeterinario(id, veterinarioAtualizado);
            return new ResponseEntity<>(veterinarioSalvo, HttpStatus.OK); // Retorna 200 OK
        } catch (IllegalArgumentException e) {
            // Captura exceções de validação, como CRMV já existente para outro veterinário
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // Retorna 409 Conflict com a mensagem de erro
        } catch (RuntimeException e) {
            // Captura exceções como "Veterinário não encontrado"
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // Retorna 404 Not Found com a mensagem de erro
        } catch (Exception e) {
            // Captura outras exceções inesperadas
            return new ResponseEntity<>("Erro interno do servidor ao atualizar veterinário.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarVeterinario(@PathVariable Long id) {
        try {
            veterinarioService.deletarVeterinario(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Retorna 204 No Content
        } catch (IllegalArgumentException e) {
            // Captura exceções como "Veterinário com ID X não encontrado para deleção."
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Retorna 404 Not Found
        }
    }
}