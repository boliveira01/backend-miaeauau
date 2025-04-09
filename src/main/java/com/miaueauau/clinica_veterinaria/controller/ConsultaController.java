package com.miaueauau.clinica_veterinaria.controller;

import com.miaueauau.clinica_veterinaria.model.Consulta;
import com.miaueauau.clinica_veterinaria.service.ConsultaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/consultas")
public class ConsultaController {

    @Autowired
    private ConsultaService consultaService;

    @GetMapping
    public ResponseEntity<List<Consulta>> listarTodasConsultas() {
        List<Consulta> consultas = consultaService.listarTodasConsultas();
        return new ResponseEntity<>(consultas, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Consulta> buscarConsultaPorId(@PathVariable Long id) {
        Optional<Consulta> consulta = consultaService.buscarConsultaPorId(id);
        return consulta.map(c -> new ResponseEntity<>(c, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Consulta> salvarConsulta(@RequestBody Consulta consulta) {
        Consulta novaConsulta = consultaService.salvarConsulta(consulta);
        return new ResponseEntity<>(novaConsulta, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Consulta> atualizarConsulta(@PathVariable Long id, @RequestBody Consulta consultaAtualizada) {
        Optional<Consulta> consultaExistente = consultaService.buscarConsultaPorId(id);
        if (consultaExistente.isPresent()) {
            consultaAtualizada.setId(id);
            Consulta consultaSalva = consultaService.salvarConsulta(consultaAtualizada);
            return new ResponseEntity<>(consultaSalva, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarConsulta(@PathVariable Long id) {
        Optional<Consulta> consultaExistente = consultaService.buscarConsultaPorId(id);
        if (consultaExistente.isPresent()) {
            consultaService.deletarConsulta(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}