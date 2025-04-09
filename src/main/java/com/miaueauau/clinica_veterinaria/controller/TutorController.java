package com.miaueauau.clinica_veterinaria.controller;

import com.miaueauau.clinica_veterinaria.model.Tutor;
import com.miaueauau.clinica_veterinaria.service.TutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tutores")
public class TutorController {

    @Autowired
    private TutorService tutorService;

    @GetMapping
    public ResponseEntity<List<Tutor>> listarTodosTutores() {
        List<Tutor> tutores = tutorService.listarTodosTutores();
        return new ResponseEntity<>(tutores, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tutor> buscarTutorPorId(@PathVariable Long id) {
        Optional<Tutor> tutor = tutorService.buscarTutorPorId(id);
        return tutor.map(t -> new ResponseEntity<>(t, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Tutor> salvarTutor(@RequestBody Tutor tutor) {
        Tutor novoTutor = tutorService.salvarTutor(tutor);
        return new ResponseEntity<>(novoTutor, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tutor> atualizarTutor(@PathVariable Long id, @RequestBody Tutor tutorAtualizado) {
        Optional<Tutor> tutorExistente = tutorService.buscarTutorPorId(id);
        if (tutorExistente.isPresent()) {
            tutorAtualizado.setId(id);
            Tutor tutorSalvo = tutorService.salvarTutor(tutorAtualizado);
            return new ResponseEntity<>(tutorSalvo, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTutor(@PathVariable Long id) {
        Optional<Tutor> tutorExistente = tutorService.buscarTutorPorId(id);
        if (tutorExistente.isPresent()) {
            tutorService.deletarTutor(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}