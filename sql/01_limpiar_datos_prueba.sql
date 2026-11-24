-- SOLO DESARROLLO. Borra los datos de prueba (DNI que empiezan con 'T').
-- Las consultas se borran primero porque su FK a medico_especialidad no tiene ON DELETE CASCADE.
USE lalcec_db;

DELETE FROM consultas
 WHERE fk_paciente IN (SELECT id_persona FROM personas WHERE dni LIKE 'T%')
    OR fk_medico_esp IN (
        SELECT me.id_medico_esp FROM medico_especialidad me
        JOIN medicos m ON m.id_medico = me.fk_medico
        JOIN personas p ON p.id_persona = m.id_medico
        WHERE p.dni LIKE 'T%');

DELETE FROM personas WHERE dni LIKE 'T%';   -- cascada: pacientes, medicos, antecedentes, medico_especialidad

-- Obras sociales de prueba
DELETE FROM obras_sociales WHERE nombre LIKE 'TEST %';
