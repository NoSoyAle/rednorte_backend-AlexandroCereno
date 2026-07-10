package com.clinica.agenda.services;
import java.util.List;
import com.clinica.agenda.entities.Doctor;
import com.clinica.agenda.entities.Especialidad;


public interface DoctorServices {
    List<Doctor> listarDoctores();

    Doctor crearDoctor(Doctor doctor);
    Doctor actualizarDoctor(Long id, Doctor doctor);
    Doctor buscarDoctor(Long id);
    void eliminarDoctor(Long id);
    //Este metodo no devuelve nada, solo elimina
    List<Doctor> buscarPorEspecialidad(
        Long especialidadId);
    Doctor buscarPorRut(String rut);

    List<Especialidad> obtenerEspecialidadesDoctor(
        Long doctorId);



}
