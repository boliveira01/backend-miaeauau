package com.miaueauau.clinica_veterinaria.service;

import com.miaueauau.clinica_veterinaria.model.*;
import com.miaueauau.clinica_veterinaria.repository.ConsultaRepository;
import com.miaueauau.clinica_veterinaria.repository.DisponibilidadeVeterinarioRepository;
import com.miaueauau.clinica_veterinaria.repository.PacienteRepository;
import com.miaueauau.clinica_veterinaria.repository.ProcedimentoRepository;
import com.miaueauau.clinica_veterinaria.repository.VeterinarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime; // Garanta que LocalTime está importado
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConsultaService {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private ProcedimentoRepository procedimentoRepository;

    @Autowired
    private DisponibilidadeVeterinarioRepository disponibilidadeVeterinarioRepository;

    // Métodos de listagem e busca
    public List<Consulta> listarTodasConsultas() {
        return consultaRepository.findAll();
    }

    public Optional<Consulta> buscarConsultaPorId(Long id) {
        return consultaRepository.findById(id);
    }

    public List<Consulta> buscarConsultasPorPaciente(Long pacienteId) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado com o ID: " + pacienteId));
        return consultaRepository.findByPaciente(paciente);
    }

    public List<Consulta> buscarConsultasPorVeterinarioEPeriodo(Long veterinarioId, LocalDateTime inicio, LocalDateTime fim) {
        Veterinario veterinario = veterinarioRepository.findById(veterinarioId)
                .orElseThrow(() -> new RuntimeException("Veterinário não encontrado com o ID: " + veterinarioId));
        return consultaRepository.findByVeterinarioAndDataHoraBetween(veterinario, inicio, fim);
    }

    // --- Lógica de Negócio para SALVAR Consulta ---
    @Transactional
    public Consulta salvarConsulta(Consulta consulta) {
        // 1. Validar existência e obter objetos relacionados
        Paciente paciente = pacienteRepository.findById(consulta.getPaciente().getId())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado com o ID: " + consulta.getPaciente().getId()));
        Veterinario veterinario = veterinarioRepository.findById(consulta.getVeterinario().getId())
                .orElseThrow(() -> new RuntimeException("Veterinário não encontrado com o ID: " + consulta.getVeterinario().getId()));

        consulta.setPaciente(paciente);
        consulta.setVeterinario(veterinario);

        // 2. Calcular duração total da consulta (incluindo procedimentos)
        int duracaoMinutos = calcularDuracaoTotal(consulta.getProcedimentos());
        LocalDateTime fimPrevistoConsulta = consulta.getDataHora().plusMinutes(duracaoMinutos);

        // 3. Validar a disponibilidade do veterinário (horário de trabalho)
        if (!isVeterinarioDisponivelNoHorarioTrabalho(veterinario, consulta.getDataHora(), fimPrevistoConsulta)) {
            throw new IllegalArgumentException("Veterinário não está disponível neste horário ou a duração da consulta excede seu horário de trabalho.");
        }

        // 4. Validar conflitos de horário com OUTRAS consultas do veterinário
        if (consultaRepository.existsByVeterinarioAndDataHora(veterinario, consulta.getDataHora())) {
            throw new IllegalArgumentException("Já existe uma consulta agendada para este veterinário exatamente neste horário.");
        }
        List<Consulta> conflitosVeterinario = consultaRepository.findByVeterinarioAndDataHoraBetween(veterinario, consulta.getDataHora().plusMinutes(1), fimPrevistoConsulta.minusMinutes(1));
        if (!conflitosVeterinario.isEmpty()) {
            throw new IllegalArgumentException("Este veterinário já possui uma consulta que se sobrepõe ao horário solicitado.");
        }


        // 5. Validar conflitos de horário com OUTRAS consultas do paciente
        if (consultaRepository.existsByPacienteAndDataHora(paciente, consulta.getDataHora())) {
            throw new IllegalArgumentException("O paciente já possui uma consulta agendada exatamente neste horário.");
        }
        List<Consulta> conflitosPaciente = consultaRepository.findByPacienteAndDataHoraBetween(paciente, consulta.getDataHora().plusMinutes(1), fimPrevistoConsulta.minusMinutes(1));
        if (!conflitosPaciente.isEmpty()) {
            throw new IllegalArgumentException("O paciente já possui uma consulta que se sobrepõe ao horário solicitado.");
        }

        // 6. Associar procedimentos, buscando-os do banco (se houver)
        if (consulta.getProcedimentos() != null && !consulta.getProcedimentos().isEmpty()) {
            List<Procedimento> procedimentosAssociados = consulta.getProcedimentos().stream()
                    .map(proc -> procedimentoRepository.findById(proc.getId())
                            .orElseThrow(() -> new RuntimeException("Procedimento não encontrado com o ID: " + proc.getId())))
                    .collect(Collectors.toList());
            consulta.setProcedimentos(procedimentosAssociados);
        } else {
            consulta.setProcedimentos(null);
        }

        // 7. Salvar a consulta
        return consultaRepository.save(consulta);
    }

    // --- Lógica de Negócio para ATUALIZAR Consulta ---
    @Transactional
    public Consulta atualizarConsulta(Long id, Consulta consultaAtualizada) {
        return consultaRepository.findById(id)
                .map(consultaExistente -> {
                    // 1. Validar existência e obter objetos relacionados (se forem alterados)
                    Paciente paciente = pacienteRepository.findById(consultaAtualizada.getPaciente().getId())
                            .orElseThrow(() -> new RuntimeException("Paciente não encontrado com o ID: " + consultaAtualizada.getPaciente().getId()));
                    Veterinario veterinario = veterinarioRepository.findById(consultaAtualizada.getVeterinario().getId())
                            .orElseThrow(() -> new RuntimeException("Veterinário não encontrado com o ID: " + consultaAtualizada.getVeterinario().getId()));

                    consultaExistente.setPaciente(paciente);
                    consultaExistente.setVeterinario(veterinario);

                    // 2. Calcular duração total da consulta (incluindo procedimentos)
                    int duracaoMinutos = calcularDuracaoTotal(consultaAtualizada.getProcedimentos());
                    LocalDateTime fimPrevistoConsulta = consultaAtualizada.getDataHora().plusMinutes(duracaoMinutos);

                    // 3. Validar a disponibilidade do veterinário (horário de trabalho)
                    if (!isVeterinarioDisponivelNoHorarioTrabalho(veterinario, consultaAtualizada.getDataHora(), fimPrevistoConsulta)) {
                        throw new IllegalArgumentException("Veterinário não está disponível neste horário ou a duração da consulta excede seu horário de trabalho.");
                    }

                    // 4. Validar conflitos de horário com OUTRAS consultas do veterinário (excluindo a própria consulta)
                    if (consultaRepository.existsByVeterinarioAndDataHoraAndIdNot(veterinario, consultaAtualizada.getDataHora(), id)) {
                        throw new IllegalArgumentException("Já existe uma consulta agendada para este veterinário exatamente neste novo horário.");
                    }
                    List<Consulta> conflitosVeterinario = consultaRepository.findByVeterinarioAndDataHoraBetween(veterinario, consultaAtualizada.getDataHora().plusMinutes(1), fimPrevistoConsulta.minusMinutes(1));
                    for (Consulta conflito : conflitosVeterinario) {
                        if (!conflito.getId().equals(id)) {
                            throw new IllegalArgumentException("Este veterinário já possui outra consulta que se sobrepõe ao novo horário solicitado.");
                        }
                    }

                    // 5. Validar conflitos de horário com OUTRAS consultas do paciente (excluindo a própria consulta)
                    if (consultaRepository.existsByPacienteAndDataHoraAndIdNot(paciente, consultaAtualizada.getDataHora(), id)) {
                        throw new IllegalArgumentException("O paciente já possui uma consulta agendada exatamente neste novo horário.");
                    }
                    List<Consulta> conflitosPaciente = consultaRepository.findByPacienteAndDataHoraBetween(paciente, consultaAtualizada.getDataHora().plusMinutes(1), fimPrevistoConsulta.minusMinutes(1));
                    for (Consulta conflito : conflitosPaciente) {
                        if (!conflito.getId().equals(id)) {
                            throw new IllegalArgumentException("O paciente já possui outra consulta que se sobrepõe ao novo horário solicitado.");
                        }
                    }

                    // 6. Atualizar os campos da consulta existente
                    consultaExistente.setDataHora(consultaAtualizada.getDataHora());
                    consultaExistente.setMotivo(consultaAtualizada.getMotivo());
                    consultaExistente.setDiagnostico(consultaAtualizada.getDiagnostico());
                    consultaExistente.setTratamento(consultaAtualizada.getTratamento());
                    consultaExistente.setConfirmada(consultaAtualizada.isConfirmada());

                    // 7. Associar procedimentos, buscando-os do banco (se houver)
                    if (consultaAtualizada.getProcedimentos() != null && !consultaAtualizada.getProcedimentos().isEmpty()) {
                        List<Procedimento> procedimentosAssociados = consultaAtualizada.getProcedimentos().stream()
                                .map(proc -> procedimentoRepository.findById(proc.getId())
                                        .orElseThrow(() -> new RuntimeException("Procedimento não encontrado com o ID: " + proc.getId())))
                                .collect(Collectors.toList());
                        consultaExistente.setProcedimentos(procedimentosAssociados);
                    } else {
                        consultaExistente.setProcedimentos(null);
                    }

                    // 8. Salvar a consulta atualizada
                    return consultaRepository.save(consultaExistente);
                })
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada com o ID: " + id));
    }


    // --- Lógica de Negócio para DELETAR Consulta ---
    public void deletarConsulta(Long id) {
        if (!consultaRepository.existsById(id)) {
            throw new IllegalArgumentException("Consulta com ID " + id + " não encontrada para deleção.");
        }
        consultaRepository.deleteById(id);
    }

    // --- Outros Métodos de Lógica de Negócio ---

    public Consulta confirmarConsulta(Long id) {
        return consultaRepository.findById(id)
                .map(consulta -> {
                    if (consulta.isConfirmada()) {
                        throw new IllegalArgumentException("A consulta com ID " + id + " já está confirmada.");
                    }
                    consulta.setConfirmada(true);
                    return consultaRepository.save(consulta);
                })
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada com o ID: " + id));
    }

    public Map<LocalDate, Double> calcularFaturamentoPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        LocalDateTime inicioDoPeriodo = dataInicio.atStartOfDay();
        LocalDateTime fimDoPeriodo = dataFim.plusDays(1).atStartOfDay();

        List<Consulta> consultasNoPeriodo = consultaRepository
                .findByDataHoraBetween(inicioDoPeriodo, fimDoPeriodo);

        return consultasNoPeriodo.stream()
                .collect(Collectors.groupingBy(
                        consulta -> consulta.getDataHora().toLocalDate(),
                        Collectors.summingDouble(consulta ->
                                consulta.getProcedimentos() != null ?
                                        consulta.getProcedimentos().stream()
                                                .mapToDouble(Procedimento::getPreco)
                                                .sum() : 0.0
                        )
                ));
    }

    // NOVO MÉTODO: Adicionar procedimentos a uma consulta existente
    @Transactional
    public Consulta adicionarProcedimentosAConsulta(Long consultaId, List<Long> idsNovosProcedimentos) {
        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada com o ID: " + consultaId));

        List<Procedimento> procedimentosAtuais = consulta.getProcedimentos();
        if (procedimentosAtuais == null) {
            procedimentosAtuais = new java.util.ArrayList<>();
        }

        for (Long procId : idsNovosProcedimentos) {
            Procedimento procedimentoToAdd = procedimentoRepository.findById(procId)
                    .orElseThrow(() -> new RuntimeException("Procedimento não encontrado com o ID: " + procId));

            if (!procedimentosAtuais.contains(procedimentoToAdd)) {
                procedimentosAtuais.add(procedimentoToAdd);
            }
        }
        consulta.setProcedimentos(procedimentosAtuais);
        return consultaRepository.save(consulta);
    }

    // NOVO MÉTODO: Remover procedimentos de uma consulta existente
    @Transactional
    public Consulta removerProcedimentosDaConsulta(Long consultaId, List<Long> idsProcedimentosARemover) {
        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada com o ID: " + consultaId));

        List<Procedimento> procedimentosAtuais = consulta.getProcedimentos();
        if (procedimentosAtuais == null || procedimentosAtuais.isEmpty()) {
            return consulta;
        }

        procedimentosAtuais.removeIf(p -> idsProcedimentosARemover.contains(p.getId()));

        consulta.setProcedimentos(procedimentosAtuais);
        return consultaRepository.save(consulta);
    }


    // --- Métodos Auxiliares de Validação ---

    /**
     * Calcula a duração total de uma consulta em minutos, incluindo a duração base
     * e a duração de cada procedimento.
     *
     * @param procedimentos Lista de procedimentos da consulta.
     * @return Duração total da consulta em minutos.
     */
    private int calcularDuracaoTotal(List<Procedimento> procedimentos) {
        int duracaoBase = 30;
        if (procedimentos == null || procedimentos.isEmpty()) {
            return duracaoBase;
        }
        int duracaoProcedimentos = procedimentos.size() * 15;
        return duracaoBase + duracaoProcedimentos;
    }

    /**
     * Verifica se o veterinário está disponível de acordo com seus horários de trabalho
     * (DisponibilidadeVeterinario).
     *
     * @param veterinario    O veterinário a ser verificado.
     * @param inicioConsulta O horário de início da consulta.
     * @param fimConsulta    O horário de término da consulta.
     * @return true se o veterinário estiver disponível no horário, false caso contrário.
     */
    private boolean isVeterinarioDisponivelNoHorarioTrabalho(Veterinario veterinario, LocalDateTime inicioConsulta, LocalDateTime fimConsulta) {
        DayOfWeek diaDaSemana = inicioConsulta.getDayOfWeek();
        LocalTime horaInicioConsulta = inicioConsulta.toLocalTime();
        LocalTime horaFimConsulta = fimConsulta.toLocalTime();

        List<DisponibilidadeVeterinario> disponibilidades = disponibilidadeVeterinarioRepository
                .findByVeterinarioAndDiaSemana(veterinario, diaDaSemana);

        if (disponibilidades.isEmpty()) {
            return false;
        }

        for (DisponibilidadeVeterinario disp : disponibilidades) {
            // CORREÇÃO AQUI: As variáveis devem ser LocalTime, assim como os getters
            LocalTime inicioDisponibilidade = disp.getInicio(); // AGORA É LocalTime!
            LocalTime fimDisponibilidade = disp.getFim();     // AGORA É LocalTime!

            if (!horaInicioConsulta.isBefore(inicioDisponibilidade) &&
                    !horaFimConsulta.isAfter(fimDisponibilidade)) {
                return true;
            }
        }
        return false;
    }
}