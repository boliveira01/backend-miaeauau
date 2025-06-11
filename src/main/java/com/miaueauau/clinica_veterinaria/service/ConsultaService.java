package com.miaueauau.clinica_veterinaria.service;

import com.miaueauau.clinica_veterinaria.model.Consulta;
import com.miaueauau.clinica_veterinaria.model.DisponibilidadeVeterinario;
import com.miaueauau.clinica_veterinaria.model.Veterinario;
import com.miaueauau.clinica_veterinaria.model.Procedimento;
import com.miaueauau.clinica_veterinaria.model.Paciente;
import com.miaueauau.clinica_veterinaria.model.Tutor;
import com.miaueauau.clinica_veterinaria.repository.ConsultaRepository;
import com.miaueauau.clinica_veterinaria.repository.DisponibilidadeVeterinarioRepository;
import com.miaueauau.clinica_veterinaria.repository.TutorRepository;
import com.miaueauau.clinica_veterinaria.repository.PacienteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar
import org.hibernate.Hibernate; // Importar Hibernate


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private PacienteRepository pacienteRepository;


    public List<Consulta> listarTodasConsultas() {
        List<Consulta> consultas = consultaRepository.findAll();
        // Inicializa lazy collections para que Jackson possa serializar
        for (Consulta consulta : consultas) {
            Hibernate.initialize(consulta.getPaciente()); // Garante Paciente EAGER
            Hibernate.initialize(consulta.getVeterinario()); // Garante Veterinario EAGER
            // Se Veterinario tem listas LAZY, inicialize-as se precisar
            if (consulta.getVeterinario() != null) {
                Hibernate.initialize(consulta.getVeterinario().getConsultas());
                Hibernate.initialize(consulta.getVeterinario().getDisponibilidades());
            }
            // Se Paciente tem listas LAZY, inicialize-as se precisar
            if (consulta.getPaciente() != null) {
                Hibernate.initialize(consulta.getPaciente().getConsultas());
                if (consulta.getPaciente().getTutor() != null) { // Tutor é EAGER no Paciente
                    Hibernate.initialize(consulta.getPaciente().getTutor().getPacientes()); // Se Tutor tem Pacientes LAZY
                }
            }
            Hibernate.initialize(consulta.getProcedimentos()); // Inicializa lista de Procedimentos
        }
        return consultas;
    }

    public Optional<Consulta> buscarConsultaPorId(Long id) {
        Optional<Consulta> consultaOptional = consultaRepository.findById(id);
        consultaOptional.ifPresent(consulta -> {
            Hibernate.initialize(consulta.getPaciente());
            Hibernate.initialize(consulta.getVeterinario());
            if (consulta.getVeterinario() != null) {
                Hibernate.initialize(consulta.getVeterinario().getConsultas());
                Hibernate.initialize(consulta.getVeterinario().getDisponibilidades());
            }
            if (consulta.getPaciente() != null) {
                Hibernate.initialize(consulta.getPaciente().getConsultas());
                if (consulta.getPaciente().getTutor() != null) {
                    Hibernate.initialize(consulta.getPaciente().getTutor().getPacientes());
                }
            }
            Hibernate.initialize(consulta.getProcedimentos());
        });
        return consultaOptional;
    }

    @Transactional
    public Consulta salvarConsulta(Consulta consulta) {
        if (isVeterinarioDisponivel(consulta.getVeterinario(), consulta.getDataHora(), consulta.getProcedimentos())) {
            Consulta novaConsulta = consultaRepository.save(consulta);
            // NOVO: Inicializar coleções após salvar para garantir serialização de retorno
            Hibernate.initialize(novaConsulta.getPaciente());
            Hibernate.initialize(novaConsulta.getVeterinario());
            if (novaConsulta.getVeterinario() != null) {
                Hibernate.initialize(novaConsulta.getVeterinario().getConsultas());
                Hibernate.initialize(novaConsulta.getVeterinario().getDisponibilidades());
            }
            if (novaConsulta.getPaciente() != null) {
                Hibernate.initialize(novaConsulta.getPaciente().getConsultas());
                if (novaConsulta.getPaciente().getTutor() != null) {
                    Hibernate.initialize(novaConsulta.getPaciente().getTutor().getPacientes());
                }
            }
            Hibernate.initialize(novaConsulta.getProcedimentos());
            return novaConsulta;
        } else {
            throw new IllegalArgumentException("Veterinário não disponível para a duração total da consulta.");
        }
    }

    @Transactional
    public void deletarConsulta(Long id) {
        consultaRepository.deleteById(id);
    }

    public List<Consulta> buscarConsultasPorVeterinarioEPeriodo(Veterinario veterinario, LocalDateTime inicio, LocalDateTime fim) {
        List<Consulta> consultas = consultaRepository.findByVeterinarioAndDataHoraBetween(veterinario, inicio, fim);
        // Inicializar coleções aqui também se o retorno precisar
        for (Consulta consulta : consultas) {
            Hibernate.initialize(consulta.getPaciente());
            Hibernate.initialize(consulta.getVeterinario());
            if (consulta.getVeterinario() != null) {
                Hibernate.initialize(consulta.getVeterinario().getConsultas());
                Hibernate.initialize(consulta.getVeterinario().getDisponibilidades());
            }
            if (consulta.getPaciente() != null) {
                Hibernate.initialize(consulta.getPaciente().getConsultas());
                if (consulta.getPaciente().getTutor() != null) {
                    Hibernate.initialize(consulta.getPaciente().getTutor().getPacientes());
                }
            }
            Hibernate.initialize(consulta.getProcedimentos());
        }
        return consultas;
    }

    public Consulta confirmarConsulta(Long id) {
        Optional<Consulta> consultaOptional = consultaRepository.findById(id);
        if (consultaOptional.isPresent()) {
            Consulta consulta = consultaOptional.get();
            consulta.setConfirmada(true);
            Consulta confirmedConsulta = consultaRepository.save(consulta);
            // Inicializar para retorno
            Hibernate.initialize(confirmedConsulta.getPaciente());
            Hibernate.initialize(confirmedConsulta.getVeterinario());
            if (confirmedConsulta.getVeterinario() != null) {
                Hibernate.initialize(confirmedConsulta.getVeterinario().getConsultas());
                Hibernate.initialize(confirmedConsulta.getVeterinario().getDisponibilidades());
            }
            if (confirmedConsulta.getPaciente() != null) {
                Hibernate.initialize(confirmedConsulta.getPaciente().getConsultas());
                if (confirmedConsulta.getPaciente().getTutor() != null) {
                    Hibernate.initialize(confirmedConsulta.getPaciente().getTutor().getPacientes());
                }
            }
            Hibernate.initialize(confirmedConsulta.getProcedimentos());
            return confirmedConsulta;
        }
        return null;
    }

    private boolean isVeterinarioDisponivel(Veterinario veterinario, LocalDateTime dataHora, List<Procedimento> procedimentos) {
        LocalDateTime inicioConsulta = dataHora.minusMinutes(30);
        LocalDateTime fimConsulta = dataHora.plusMinutes(30);

        if (procedimentos != null && !procedimentos.isEmpty()) {
            int duracaoProcedimentos = procedimentos.size() * 15;
            inicioConsulta = inicioConsulta.minusMinutes(duracaoProcedimentos / 2);
            fimConsulta = fimConsulta.plusMinutes(duracaoProcedimentos / 2);
        }

        // Antes de buscar, inicialize as coleções do veterinário se ele tiver procedimentos ou consultas lá.
        // Forçar inicialização para o objeto veterinário para garantir que a consulta funcione
        if (veterinario != null) {
            Hibernate.initialize(veterinario.getConsultas());
            Hibernate.initialize(veterinario.getDisponibilidades());
        }


        List<DisponibilidadeVeterinario> disponibilidades = disponibilidadeVeterinarioRepository
                .findByVeterinarioAndInicioLessThanAndFimGreaterThan(veterinario, fimConsulta, inicioConsulta);

        return !disponibilidades.isEmpty();
    }

    private boolean isVeterinarioDisponivel(Veterinario veterinario, LocalDateTime dataHora) {
        return isVeterinarioDisponivel(veterinario, dataHora, null);
    }

    @Transactional
    public List<Consulta> buscarConsultasPorTutor(Tutor tutor) {
        List<Paciente> petsDoTutor = pacienteService.buscarPacientesPorTutor(tutor);
        List<Consulta> consultasDoTutor = new ArrayList<>();
        for (Paciente pet : petsDoTutor) {
            consultasDoTutor.addAll(consultaRepository.findByPaciente(pet));
        }
        // Inicializar coleções aqui também se o retorno precisar delas completas
        for (Consulta consulta : consultasDoTutor) {
            Hibernate.initialize(consulta.getPaciente());
            Hibernate.initialize(consulta.getVeterinario());
            if (consulta.getVeterinario() != null) {
                Hibernate.initialize(consulta.getVeterinario().getConsultas());
                Hibernate.initialize(consulta.getVeterinario().getDisponibilidades());
            }
            if (consulta.getPaciente() != null) {
                Hibernate.initialize(consulta.getPaciente().getConsultas());
                if (consulta.getPaciente().getTutor() != null) {
                    Hibernate.initialize(consulta.getPaciente().getTutor().getPacientes());
                }
            }
            Hibernate.initialize(consulta.getProcedimentos());
        }
        return consultasDoTutor;
    }

    @Transactional
    public List<Consulta> buscarConsultasPorPaciente(Long pacienteId) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado com o ID: " + pacienteId));
        List<Consulta> consultas = consultaRepository.findByPaciente(paciente);
        // Inicializar coleções aqui também
        for (Consulta consulta : consultas) {
            Hibernate.initialize(consulta.getPaciente());
            Hibernate.initialize(consulta.getVeterinario());
            if (consulta.getVeterinario() != null) {
                Hibernate.initialize(consulta.getVeterinario().getConsultas());
                Hibernate.initialize(consulta.getVeterinario().getDisponibilidades());
            }
            if (consulta.getPaciente() != null) {
                Hibernate.initialize(consulta.getPaciente().getConsultas());
                if (consulta.getPaciente().getTutor() != null) {
                    Hibernate.initialize(consulta.getPaciente().getTutor().getPacientes());
                }
            }
            Hibernate.initialize(consulta.getProcedimentos());
        }
        return consultas;
    }

    public Double calcularFaturamentoPorPeriodo(LocalDate inicioPeriodo, LocalDate fimPeriodo) {
        LocalDateTime inicioDateTime = inicioPeriodo.atStartOfDay();
        LocalDateTime fimDateTime = fimPeriodo.atTime(LocalTime.MAX);

        List<Consulta> consultasConfirmadas = consultaRepository.findByDataHoraBetweenAndConfirmadaTrue(inicioDateTime, fimDateTime);

        return consultasConfirmadas.stream()
                .filter(consulta -> consulta.getProcedimentos() != null)
                .flatMap(consulta -> consulta.getProcedimentos().stream())
                .mapToDouble(Procedimento::getPreco)
                .sum();
    }
}