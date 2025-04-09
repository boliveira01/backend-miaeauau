package com.miaueauau.clinica_veterinaria.service;
import com.miaueauau.clinica_veterinaria.model.Procedimento;
import com.miaueauau.clinica_veterinaria.model.DisponibilidadeVeterinario;
import com.miaueauau.clinica_veterinaria.model.Consulta;
import com.miaueauau.clinica_veterinaria.model.Veterinario;
import com.miaueauau.clinica_veterinaria.repository.ConsultaRepository;
import com.miaueauau.clinica_veterinaria.repository.DisponibilidadeVeterinarioRepository;
import com.miaueauau.clinica_veterinaria.service.VeterinarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ConsultaService {

    @Autowired
    private ConsultaRepository consultaRepository;

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