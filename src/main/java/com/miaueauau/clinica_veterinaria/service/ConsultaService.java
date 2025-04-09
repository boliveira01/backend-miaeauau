package com.miaueauau.clinica_veterinaria.service;

import com.miaueauau.clinica_veterinaria.model.Consulta;
import com.miaueauau.clinica_veterinaria.repository.ConsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConsultaService {

    @Autowired
    private ConsultaRepository consultaRepository;

    public List<Consulta> listarTodasConsultas() {
        return consultaRepository.findAll();
    }

    public Optional<Consulta> buscarConsultaPorId(Long id) {
        return consultaRepository.findById(id);
    }

    public Consulta salvarConsulta(Consulta consulta) {
        return consultaRepository.save(consulta);
    }

    public void deletarConsulta(Long id) {
        consultaRepository.deleteById(id);
    }

    // Podemos adicionar métodos de lógica de negócios específicos para Consulta no futuro
    // Por exemplo: buscar consultas por paciente, por veterinário, por data.
}