package com.clinica.agenda.entities;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.util.List;
import com.clinica.agenda.enums.Sexo;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.EnumType;

import lombok.Getter;
import lombok.NoArgsConstructor;    
import lombok.Setter;   
import lombok.AllArgsConstructor;   

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="doctor")

public class Doctor {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre; 
    private String apellido;
    private String rut;
    private LocalDate fechaNac;
    private String correo;
    private String telefono;
    private String direccion;

    @Enumerated(EnumType.STRING)
    private Sexo sexo;


    @ManyToMany
    @JsonIgnore
    @JoinTable(
        name = "doctor_especialidad",
        joinColumns = @JoinColumn(name = "doctor_id"),
        inverseJoinColumns = @JoinColumn(name = "especialidad_id")
    )
    private List<Especialidad> especialidades;
    

}
