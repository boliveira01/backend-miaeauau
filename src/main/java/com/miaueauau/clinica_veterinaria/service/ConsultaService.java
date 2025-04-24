package com.miaueauau.clinica_veterinaria.service;
import com.miaueauau.clinica_veterinaria.model.*;
import com.miaueauau.clinica_veterinaria.repository.ConsultaRepository;
import com.miaueauau.clinica_veterinaria.repository.DisponibilidadeVeterinarioRepository;
import com.miaueauau.clinica_veterinaria.service.VeterinarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConsultaService {

    @Autowired
    private ConsultaRepository consultaRepository;

    // ... outros métodos ...

    public List<Consulta> buscarConsultasPorPaciente(Paciente paciente) {
        return consultaRepository.findByPaciente(paciente);
    }

    @Autowired
    private VeterinarioService veterinarioService;

    @Autowired
    private DisponibilidadeVeterinarioRepository disponibilidadeVeterinarioRepository;

    public List<Consulta> listarTodasConsultas() {
        return consultaRepository.findAll();
    }

    public Optional<Consulta> buscarConsultaPorId(Long id) {
        return consultaRepository.findById(id);
    }

    public Consulta salvarConsulta(Consulta consulta) {
        // Lógica de validação de disponibilidade
        if (isVeterinarioDisponivel(consulta.getVeterinario(), consulta.getDataHora(), consulta.getProcedimentos())) {
            return consultaRepository.save(consulta);
        } else {
            throw new IllegalArgumentException("Veterinário não disponível para a duração total da consulta.");
        }
    }


    public Map<LocalDate, Double> calcularFaturamentoPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        LocalDateTime inicioDoPeriodo = dataInicio.atStartOfDay();
        LocalDateTime fimDoPeriodo = dataFim.plusDays(1).atStartOfDay(); // Inclui todo o dia da dataFim

        List<Consulta> consultasNoPeriodo = consultaRepository
                .findByDataHoraBetween(inicioDoPeriodo, fimDoPeriodo);

        return consultasNoPeriodo.stream()
                .collect(Collectors.groupingBy(
                        consulta -> consulta.getDataHora().toLocalDate(),
                        Collectors.summingDouble(consulta -> {
                            // Lógica para determinar o valor da consulta
                            // Por enquanto, vamos assumir que cada consulta tem um valor fixo de 50.0
                            return 50.0;
                        })
                ));
    }

    public void deletarConsulta(Long id) {
        consultaRepository.deleteById(id);
    }

    public List<Consulta> buscarConsultasPorVeterinarioEPeriodo(Veterinario veterinario, LocalDateTime inicio, LocalDateTime fim) {
        return consultaRepository.findByVeterinarioAndDataHoraBetween(veterinario, inicio, fim);
    }

    public Consulta confirmarConsulta(Long id) {
        Optional<Consulta> consultaOptional = consultaRepository.findById(id);
        if (consultaOptional.isPresent()) {
            Consulta consulta = consultaOptional.get();
            consulta.setConfirmada(true);
            return consultaRepository.save(consulta);
        }
        return null;
    }

    private boolean isVeterinarioDisponivel(Veterinario veterinario, LocalDateTime dataHora, List<Procedimento> procedimentos) {
        LocalDateTime inicioConsulta = dataHora.minusMinutes(30); // Duração mínima
        LocalDateTime fimConsulta = dataHora.plusMinutes(30); // Duração mínima

        if (procedimentos != null && !procedimentos.isEmpty()) {
            // Lógica para calcular a duração adicional dos procedimentos (exemplo simplificado)
            int duracaoProcedimentos = procedimentos.size() * 15; // 15 minutos por procedimento (exemplo)
            inicioConsulta = inicioConsulta.minusMinutes(duracaoProcedimentos / 2);
            fimConsulta = fimConsulta.plusMinutes(duracaoProcedimentos / 2);
        }

        // Verificamos se há disponibilidades que cobrem o horário da consulta
        List<DisponibilidadeVeterinario> disponibilidades = disponibilidadeVeterinarioRepository
                .findByVeterinarioAndInicioLessThanAndFimGreaterThan(veterinario, fimConsulta, inicioConsulta);

        // Se encontrarmos alguma disponibilidade, o veterinário está disponível
        return !disponibilidades.isEmpty();
    }

    private boolean isVeterinarioDisponivel(Veterinario veterinario, LocalDateTime dataHora) {
        return isVeterinarioDisponivel(veterinario, dataHora, null);
    }

    // Métodos para adicionar/remover procedimentos da consulta (se necessário)
    // ...
}