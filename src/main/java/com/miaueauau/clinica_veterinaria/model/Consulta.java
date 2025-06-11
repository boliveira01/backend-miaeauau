package com.miaueauau.clinica_veterinaria.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
@Table(name = "consultas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FetchType.EAGER para Paciente (sempre queremos o paciente na consulta)
    @ManyToOne(fetch = FetchType.EAGER) // <<< MUDANÇA AQUI
    @JoinColumn(name = "paciente_id", nullable = false)
    @JsonIgnoreProperties("consultas") // Ignora a lista de Consultas dentro do Paciente
    private Paciente paciente;

    // FetchType.EAGER para Veterinario (sempre queremos o veterinário na consulta)
    @ManyToOne(fetch = FetchType.EAGER) // <<< MUDANÇA AQUI
    @JoinColumn(name = "veterinario_id", nullable = false)
    @JsonIgnoreProperties({"consultas", "disponibilidades"}) // Ignora as listas do Veterinario
    private Veterinario veterinario;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    private String motivo;

    private String diagnostico;

    private String tratamento;

    private boolean confirmada;

    @Column(nullable = false)
    private String tipoAtendimento;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "consulta_procedimento",
            joinColumns = @JoinColumn(name = "consulta_id"),
            inverseJoinColumns = @JoinColumn(name = "procedimento_id")
    )
    @JsonIgnoreProperties("consultas") // Ignora a lista de Consultas dentro do Procedimento
    private List<Procedimento> procedimentos;
}