package com.miaueauau.clinica_veterinaria.controller;

import com.miaueauau.clinica_veterinaria.model.Exame;
import com.miaueauau.clinica_veterinaria.service.ExameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/exames")
public class ExameController {

    @Autowired
    private ExameService exameService;

    @GetMapping
    public ResponseEntity<List<Exame>> listarTodosExames() {
        List<Exame> exames = exameService.listarTodosExames();
        return new ResponseEntity<>(exames, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exame> buscarExamePorId(@PathVariable Long id) {
        Optional<Exame> exame = exameService.buscarExamePorId(id);
        return exame.map(e -> new ResponseEntity<>(e, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Exame> salvarExame(@RequestBody Exame exame) {
        Exame novoExame = exameService.salvarExame(exame);
        return new ResponseEntity<>(novoExame, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Exame> atualizarExame(@PathVariable Long id, @RequestBody Exame exameAtualizado) {
        Optional<Exame> exameExistente = exameService.buscarExamePorId(id);
        if (exameExistente.isPresent()) {
            exameAtualizado.setId(id);
            Exame exameSalvo = exameService.salvarExame(exameAtualizado);
            return new ResponseEntity<>(exameSalvo, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarExame(@PathVariable Long id) {
        Optional<Exame> exameExistente = exameService.buscarExamePorId(id);
        if (exameExistente.isPresent()) {
            exameService.deletarExame(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}