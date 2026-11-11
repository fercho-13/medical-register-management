-- Datos de prueba (solo desarrollo). Ejecutar después de schema.sql.
USE lalcec_db;
INSERT INTO obras_sociales (nombre) VALUES ('PAMI'), ('OSDE'), ('IOMA');
INSERT INTO personas (dni, nombre, apellido) VALUES ('20111222', 'Ana', 'Médica');
SET @id = LAST_INSERT_ID();
INSERT INTO medicos (id_medico, matricula) VALUES (@id, 'MP-0001');
INSERT INTO medico_especialidad (fk_medico, fk_especialidad)
  SELECT @id, id_especialidad FROM especialidades WHERE nombre IN ('Ginecología', 'Clínica General');
