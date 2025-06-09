package com.miaueauau.clinica_veterinaria.model;

import com.fasterxml.jackson.annotation.JsonBackReference; // Já está importado
import com.fasterxml.jackson.annotation.JsonManagedReference; // Certifique-se que este import também está aqui se já não estiver

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "consultas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    @NotNull(message = "O paciente da consulta é obrigatório")
    @JsonBackReference // <-- Esta anotação já deve estar aqui
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinario_id", nullable = false)
    @NotNull(message = "O veterinário da consulta é obrigatório")
    @JsonBackReference // <-- ADICIONE ESTA ANOTAÇÃO AQUI!
    private Veterinario veterinario;

    @Column(nullable = false)
    @NotNull(message = "A data e hora da consulta são obrigatórias")
    @FutureOrPresent(message = "A data e hora da consulta devem ser futuras ou presentes")
    private LocalDateTime dataHora;

    @NotBlank(message = "O motivo da consulta é obrigatório")
    @Size(max = 500, message = "O motivo da consulta não pode ter mais de 500 caracteres")
    private String motivo;

    @Size(max = 2000, message = "O diagnóstico não pode ter mais de 2000 caracteres")
    private String diagnostico;

    @Size(max = 2000, message = "O tratamento não pode ter mais de 2000 caracteres")
    private String tratamento;

    @NotNull(message = "O status de confirmação da consulta é obrigatório")
    private boolean confirmada;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "consulta_procedimento",
            joinColumns = @JoinColumn(name = "consulta_id"),
            inverseJoinColumns = @JoinColumn(name = "procedimento_id")
    )
    private List<Procedimento> procedimentos;
}