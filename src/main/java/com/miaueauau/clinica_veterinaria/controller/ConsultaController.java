package com.miaueauau.clinica_veterinaria.controller;

import com.miaueauau.clinica_veterinaria.model.Consulta;
import com.miaueauau.clinica_veterinaria.model.Veterinario;
import com.miaueauau.clinica_veterinaria.service.ConsultaService;
import com.miaueauau.clinica_veterinaria.service.VeterinarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/consultas")
public class ConsultaController {

    @Autowired
    private ConsultaService consultaService;

    @Autowired
    private VeterinarioService veterinarioService; // Para buscar o veterinario (opcional, dependendo de como você recebe o veterinário)

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
        try {
            Consulta novaConsulta = consultaService.salvarConsulta(consulta);
            return new ResponseEntity<>(novaConsulta, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
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

    @GetMapping("/disponibilidade")
    public ResponseEntity<List<Consulta>> buscarDisponibilidade(
            @RequestParam("veterinarioId") Long veterinarioId,
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam("fim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {

        Optional<Veterinario> veterinarioOptional = veterinarioService.buscarVeterinarioPorId(veterinarioId);
        if (veterinarioOptional.isPresent()) {
            Veterinario veterinario = veterinarioOptional.get();
            List<Consulta> consultas = consultaService.buscarConsultasPorVeterinarioEPeriodo(veterinario, inicio, fim);
            return new ResponseEntity<>(consultas, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Veterinário não encontrado
        }
    }

    @PutMapping("/{id}/confirmar")
    public ResponseEntity<Consulta> confirmarConsulta(@PathVariable Long id) {
        Consulta consultaConfirmada = consultaService.confirmarConsulta(id);
        if (consultaConfirmada != null) {
            return new ResponseEntity<>(consultaConfirmada, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Consulta não encontrada
        }
    }



    // Endpoints para adicionar/remover procedimentos da consulta (se necessário)
    // ...
}