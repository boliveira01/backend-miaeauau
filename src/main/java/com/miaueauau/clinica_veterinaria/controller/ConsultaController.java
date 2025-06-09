package com.miaueauau.clinica_veterinaria.controller;

import com.miaueauau.clinica_veterinaria.model.Consulta;
import com.miaueauau.clinica_veterinaria.model.Paciente; // NOVO: Importe Paciente
import com.miaueauau.clinica_veterinaria.model.Veterinario;
import com.miaueauau.clinica_veterinaria.service.ConsultaService;
import jakarta.validation.Valid; // NOVO: Importe para validação
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult; // NOVO: Importe para capturar erros de validação
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // NOVO: Importe para coletar mensagens de erro

@RestController
@RequestMapping("/api/consultas")
public class ConsultaController {

    @Autowired
    private ConsultaService consultaService;

    // Remova a injeção direta de VeterinarioService aqui, pois ConsultaService já gerencia isso
    // @Autowired
    // private VeterinarioService veterinarioService;

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
    public ResponseEntity<?> salvarConsulta(@Valid @RequestBody Consulta consulta, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST); // Retorna 400 com erros de validação
        }
        try {
            Consulta novaConsulta = consultaService.salvarConsulta(consulta);
            return new ResponseEntity<>(novaConsulta, HttpStatus.CREATED); // Retorna 201 Created
        } catch (IllegalArgumentException e) {
            // Captura exceções como "Veterinário não disponível", "Já existe consulta neste horário"
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // Retorna 400 Bad Request com a mensagem
        } catch (RuntimeException e) {
            // Captura exceções como "Paciente não encontrado", "Veterinário não encontrado"
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // Retorna 404 Not Found
        } catch (Exception e) {
            return new ResponseEntity<>("Erro interno do servidor ao salvar consulta.", HttpStatus.INTERNAL_SERVER_ERROR); // Retorna 500
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarConsulta(@PathVariable Long id, @Valid @RequestBody Consulta consultaAtualizada, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST); // Retorna 400 com erros de validação
        }
        try {
            // NOVO: Chama o método 'atualizarConsulta' do serviço
            Consulta consultaSalva = consultaService.atualizarConsulta(id, consultaAtualizada);
            return new ResponseEntity<>(consultaSalva, HttpStatus.OK); // Retorna 200 OK
        } catch (IllegalArgumentException e) {
            // Captura exceções de lógica de negócio (conflitos de horário, indisponibilidade)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // Retorna 400 Bad Request
        } catch (RuntimeException e) {
            // Captura exceções como "Consulta não encontrada", "Paciente não encontrado", "Veterinário não encontrado"
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // Retorna 404 Not Found
        } catch (Exception e) {
            return new ResponseEntity<>("Erro interno do servidor ao atualizar consulta.", HttpStatus.INTERNAL_SERVER_ERROR); // Retorna 500
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarConsulta(@PathVariable Long id) {
        try {
            consultaService.deletarConsulta(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Retorna 204 No Content
        } catch (IllegalArgumentException e) {
            // Captura exceções como "Consulta com ID X não encontrada para deleção."
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // Retorna 404 Not Found
        } catch (Exception e) {
            return new ResponseEntity<>("Erro interno do servidor ao deletar consulta.", HttpStatus.INTERNAL_SERVER_ERROR); // Retorna 500
        }
    }

    @GetMapping("/disponibilidade")
    public ResponseEntity<?> buscarDisponibilidade(
            @RequestParam("veterinarioId") Long veterinarioId,
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam("fim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        try {
            // NOVO: O serviço já lida com a busca e validação do veterinário
            List<Consulta> consultas = consultaService.buscarConsultasPorVeterinarioEPeriodo(veterinarioId, inicio, fim);
            return new ResponseEntity<>(consultas, HttpStatus.OK);
        } catch (RuntimeException e) {
            // Captura "Veterinário não encontrado" do serviço
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // Retorna 404 Not Found
        } catch (Exception e) {
            return new ResponseEntity<>("Erro interno do servidor ao buscar disponibilidade.", HttpStatus.INTERNAL_SERVER_ERROR); // Retorna 500
        }
    }

    @PutMapping("/{id}/confirmar")
    public ResponseEntity<?> confirmarConsulta(@PathVariable Long id) {
        try {
            Consulta consultaConfirmada = consultaService.confirmarConsulta(id);
            return new ResponseEntity<>(consultaConfirmada, HttpStatus.OK); // Retorna 200 OK
        } catch (IllegalArgumentException e) {
            // Captura "A consulta já está confirmada."
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // Retorna 400 Bad Request
        } catch (RuntimeException e) {
            // Captura "Consulta não encontrada"
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // Retorna 404 Not Found
        } catch (Exception e) {
            return new ResponseEntity<>("Erro interno do servidor ao confirmar consulta.", HttpStatus.INTERNAL_SERVER_ERROR); // Retorna 500
        }
    }

    // NOVO: Endpoint para buscar consultas por paciente
    @GetMapping("/por-paciente/{pacienteId}")
    public ResponseEntity<?> buscarConsultasPorPaciente(@PathVariable Long pacienteId) {
        try {
            List<Consulta> consultas = consultaService.buscarConsultasPorPaciente(pacienteId);
            if (consultas.isEmpty()) {
                return new ResponseEntity<>("Nenhuma consulta encontrada para o paciente com ID: " + pacienteId, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(consultas, HttpStatus.OK);
        } catch (RuntimeException e) {
            // Captura "Paciente não encontrado" do serviço
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // Retorna 404 Not Found
        } catch (Exception e) {
            return new ResponseEntity<>("Erro interno do servidor ao buscar consultas do paciente.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoints para adicionar/remover procedimentos da consulta (se necessário)
    // Esses seriam mais complexos, pois você precisaria de um DTO para receber os IDs dos procedimentos a serem adicionados/removidos.
    // Exemplo:
    /*
    @PutMapping("/{consultaId}/procedimentos")
    public ResponseEntity<?> adicionarProcedimentos(@PathVariable Long consultaId, @RequestBody List<Long> procedimentoIds) {
        try {
            Consulta consultaAtualizada = consultaService.adicionarProcedimentosAConsulta(consultaId, procedimentoIds);
            return new ResponseEntity<>(consultaAtualizada, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao adicionar procedimentos.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    */
}