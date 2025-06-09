package com.miaueauau.clinica_veterinaria.model;

import jakarta.persistence.*;
import java.time.DayOfWeek; // Importante: use DayOfWeek
import java.time.LocalTime; // Importante: use LocalTime para horários, NÃO LocalDateTime

@Entity
@Table(name = "disponibilidades_veterinarios")
public class DisponibilidadeVeterinario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Veterinario veterinario;

    @Enumerated(EnumType.STRING) // Salva o dia da semana como String (ex: MONDAY)
    @Column(nullable = false)
    private DayOfWeek diaSemana; // Campo para o dia da semana

    @Column(nullable = false)
    private LocalTime inicio; // <--- CORRIGIDO: Deve ser LocalTime

    @Column(nullable = false)
    private LocalTime fim;    // <--- CORRIGIDO: Deve ser LocalTime

    private String observacoes;

    // Construtor vazio (obrigatório para JPA)
    public DisponibilidadeVeterinario() {}

    // Construtor com campos
    public DisponibilidadeVeterinario(Veterinario veterinario, DayOfWeek diaSemana, LocalTime inicio, LocalTime fim, String observacoes) {
        this.veterinario = veterinario;
        this.diaSemana = diaSemana;
        this.inicio = inicio;
        this.fim = fim;
        this.observacoes = observacoes;
    }

    // --- Getters e Setters (Certifique-se de que retornam/recebem LocalTime para inicio e fim) ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Veterinario getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(Veterinario veterinario) {
        this.veterinario = veterinario;
    }

    public DayOfWeek getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(DayOfWeek diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getInicio() { // <--- Retorna LocalTime
        return inicio;
    }

    public void setInicio(LocalTime inicio) { // <--- Recebe LocalTime
        this.inicio = inicio;
    }

    public LocalTime getFim() { // <--- Retorna LocalTime
        return fim;
    }

    public void setFim(LocalTime fim) { // <--- Recebe LocalTime
        this.fim = fim;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    @Override
    public String toString() {
        return "DisponibilidadeVeterinario{" +
                "id=" + id +
                ", veterinario=" + (veterinario != null ? veterinario.getNome() : "null") +
                ", diaSemana=" + diaSemana +
                ", inicio=" + inicio +
                ", fim=" + fim +
                ", observacoes='" + observacoes + '\'' +
                '}';
    }
}