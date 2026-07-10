DELETE FROM servicios_clinicos;

INSERT INTO servicios_clinicos (nombre, precio_base, categoria, modalidad, disponible) VALUES
('Consulta General', 15000, 'CONSULTA', 'PRESENCIAL', true),
('Consulta de Especialidad', 25000, 'CONSULTA', 'PRESENCIAL', true),
('Telemedicina General', 10000, 'CONSULTA', 'TELEMEDICINA', true),
('Telemedicina Especialidad', 20000, 'CONSULTA', 'TELEMEDICINA', true),
('Hemograma Completo', 8000, 'EXAMEN', 'PRESENCIAL', true),
('Perfil Bioquimico', 12000, 'EXAMEN', 'PRESENCIAL', true),
('Rayos X', 18000, 'EXAMEN', 'PRESENCIAL', true),
('Ecografia', 30000, 'EXAMEN', 'PRESENCIAL', true),
('TAC', 80000, 'EXAMEN', 'PRESENCIAL', true),
('Resonancia Magnetica', 120000, 'EXAMEN', 'PRESENCIAL', true),
('Urgencia Menor', 35000, 'URGENCIA', 'PRESENCIAL', true),
('Urgencia Mayor', 70000, 'URGENCIA', 'PRESENCIAL', true);
