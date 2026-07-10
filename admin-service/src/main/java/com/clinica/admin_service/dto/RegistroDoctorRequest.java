package com.clinica.admin_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroDoctorRequest {
    private String nombre;
    private String rut;
    private String email;
    private String password;
    private String telefono;
}
