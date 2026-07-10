package com.clinica.agenda.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.clinica.agenda.repository.DoctorRepository;
import com.clinica.agenda.repository.EspecialidadRepository;
import com.clinica.agenda.entities.Doctor;
import com.clinica.agenda.entities.Especialidad;

import java.util.List;  

@Service
public class DoctorServicesImplement implements DoctorServices {
        
        @Autowired 
        private DoctorRepository doctorRepository;
        @Autowired
        private EspecialidadRepository especialidadRepository;

        @Override
        public List<Doctor> listarDoctores() {
                return doctorRepository.findAll();
        }

        @Override
        public Doctor crearDoctor(Doctor doctor) {

                if (doctor.getEspecialidades() != null &&
                !doctor.getEspecialidades().isEmpty()) {

                List<Especialidad> especialidadesExistentes =
                        doctor.getEspecialidades()
                                .stream()
                                .map(especialidad ->
                                        especialidadRepository.findById(
                                                especialidad.getId())
                                                .orElseThrow(() ->
                                                        new RuntimeException(
                                                                "Especialidad no encontrada: "
                                                                + especialidad.getId())))
                                .toList();

                        doctor.setEspecialidades(especialidadesExistentes);
                }

                return doctorRepository.save(doctor);
                }

        @Override
        public Doctor buscarDoctor(Long id) {
                return doctorRepository.findById(id).orElse(null);
        }

        @Override
        public void eliminarDoctor(Long id) {
                doctorRepository.deleteById(id);
        }

        @Override
        public Doctor actualizarDoctor(Long id, Doctor doctorActualizado) {

                Doctor doctorExistente = doctorRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Doctor no encontrado"));

                doctorExistente.setNombre(doctorActualizado.getNombre());
                doctorExistente.setApellido(doctorActualizado.getApellido());
                doctorExistente.setRut(doctorActualizado.getRut());
                doctorExistente.setFechaNac(doctorActualizado.getFechaNac());
                doctorExistente.setCorreo(doctorActualizado.getCorreo());
                doctorExistente.setTelefono(doctorActualizado.getTelefono());
                doctorExistente.setDireccion(doctorActualizado.getDireccion());
                doctorExistente.setSexo(doctorActualizado.getSexo());

                if (doctorActualizado.getEspecialidades() != null) {

                List<Especialidad> especialidadesExistentes =
                        doctorActualizado.getEspecialidades()
                                .stream()
                                .map(e -> especialidadRepository
                                        .findById(e.getId())
                                        .orElseThrow(() ->
                                                new RuntimeException(
                                                        "Especialidad no encontrada: "
                                                        + e.getId())))
                                .collect(java.util.stream.Collectors.toList());

                doctorExistente.getEspecialidades().clear();
                doctorExistente.getEspecialidades().addAll(especialidadesExistentes);
                }

                return doctorRepository.save(doctorExistente);
        }

        @Override
        public List<Doctor> buscarPorEspecialidad(
                Long especialidadId) {

        return doctorRepository
                .findByEspecialidades_Id(
                        especialidadId);}

        @Override
        public List<Especialidad> obtenerEspecialidadesDoctor(
                Long doctorId) {

                Doctor doctor =doctorRepository.findById(doctorId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Doctor no encontrado"));
        return doctor.getEspecialidades();}


        @Override
        public Doctor buscarPorRut(String rut) {

                return doctorRepository
                .findByRut(rut)
                .orElseThrow(
                        () -> new RuntimeException(
                        "Doctor no encontrado"
                        )
                );

}
}
