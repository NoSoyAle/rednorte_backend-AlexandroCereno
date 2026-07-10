package com.clinica.agenda.enums;

public enum EstadoCita {
    CANCELADA, //Por Doctor o Paciente
    CONFIRMADA, //Confirmada por paciente
    REALIZADA, //Doctor confirma que paciente asiste
    Pendiente, //Paciente aún no apreta botón confirmar
    NO_ASISTE, //Doctor confirma que paciente no asiste
}
