package com.miaueauau.clinica_veterinaria.controller;

import com.miaueauau.clinica_veterinaria.model.Veterinario;
import com.miaueauau.clinica_veterinaria.service.VeterinarioService;
import jakarta.validation.Valid; // Importe esta classe
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult; // E esta classe
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Veterinario> salvarVeterinario(@Valid @RequestBody Veterinario veterinario, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        Veterinario novoVeterinario = veterinarioService.salvarVeterinario(veterinario);
        return new ResponseEntity<>(novoVeterinario, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Veterinario> atualizarVeterinario(@PathVariable Long id, @RequestBody Veterinario veterinarioAtualizado) {
        Optional<Veterinario> veterinarioExistente = veterinarioService.buscarVeterinarioPorId(id);
        if (veterinarioExistente.isPresent()) {
            veterinarioAtualizado.setId(id);
            Veterinario veterinarioSalvo = veterinarioService.salvarVeterinario(veterinarioAtualizado);
            return new ResponseEntity<>(veterinarioSalvo, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarVeterinario(@PathVariable Long id) {
        Optional<Veterinario> veterinarioExistente = veterinarioService.buscarVeterinarioPorId(id);
        if (veterinarioExistente.isPresent()) {
            veterinarioService.deletarVeterinario(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}