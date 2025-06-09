package com.miaueauau.clinica_veterinaria.controller;

import com.miaueauau.clinica_veterinaria.model.Consulta;
import com.miaueauau.clinica_veterinaria.model.Paciente;
import com.miaueauau.clinica_veterinaria.service.ConsultaService;
import com.miaueauau.clinica_veterinaria.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
// import java.util.Optional; // Já não precisamos mais do Optional neste trecho específico

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private final PacienteService pacienteService;
    private final ConsultaService consultaService;

    @Autowired
    public RelatorioController(PacienteService pacienteService, ConsultaService consultaService) {
        this.pacienteService = pacienteService;
        this.consultaService = consultaService;
    }

    @GetMapping("/faturamento")
    public ResponseEntity<Map<LocalDate, Double>> obterFaturamentoPorPeriodo(
            @RequestParam(value = "dataInicio") LocalDate dataInicio,
            @RequestParam(value = "dataFim") LocalDate dataFim) {
        Map<LocalDate, Double> faturamento = consultaService.calcularFaturamentoPorPeriodo(dataInicio, dataFim);
        return new ResponseEntity<>(faturamento, HttpStatus.OK);
    }

    @GetMapping("/pacientes")
    public ResponseEntity<List<Paciente>> listarPacientes() {
        List<Paciente> pacientes = pacienteService.listarTodosPacientes();
        return new ResponseEntity<>(pacientes, HttpStatus.OK);
    }

    @GetMapping("/pacientes/{pacienteId}/consultas")
    public ResponseEntity<List<Consulta>> historicoConsultasPorPaciente(@PathVariable Long pacienteId) {
        // CORRIGIDO: Passando o 'pacienteId' (Long) diretamente para o serviço
        // O serviço já é responsável por buscar o paciente pelo ID e lançar erro se não encontrar.
        List<Consulta> consultas = consultaService.buscarConsultasPorPaciente(pacienteId);
        return new ResponseEntity<>(consultas, HttpStatus.OK);
    }
}