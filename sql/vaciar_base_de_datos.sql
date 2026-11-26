-- =====================================================================
-- VACIAR LA BASE DE DATOS lalcec_db
--
-- ATENCIÓN: borra TODOS los datos de TODAS las tablas (pacientes, médicos, consultas,
-- obras sociales y especialidades) y reinicia los contadores AUTO_INCREMENT en 1.
-- Las tablas y su estructura se conservan. NO se puede deshacer.
-- Úsalo solo en desarrollo/pruebas, o antes de una carga inicial. NUNCA con datos reales de la clínica.
--
-- Incluye los datos que cargaba el antiguo seed_demo.sql (obras sociales PAMI/OSDE/IOMA,
-- la médica de prueba y sus especialidades).
--
-- Después de vaciar, las especialidades se vuelven a crear al ejecutar datos_equipo_medico.sql.
--
-- Uso:
--   mysql -u root -p lalcec_db < sql/vaciar_base_de_datos.sql
-- =====================================================================
USE lalcec_db;

-- Se desactivan las claves foráneas solo durante el vaciado, porque TRUNCATE no funciona
-- sobre tablas referenciadas por otras.
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE consultas_ginecologia;
TRUNCATE TABLE consultas;
TRUNCATE TABLE antecedentes_ginecologicos;
TRUNCATE TABLE medico_especialidad;
TRUNCATE TABLE pacientes;
TRUNCATE TABLE medicos;
TRUNCATE TABLE personas;
TRUNCATE TABLE especialidades;
TRUNCATE TABLE obras_sociales;

SET FOREIGN_KEY_CHECKS = 1;

-- Verificación: todas las tablas deben mostrar 0 filas
SELECT 'obras_sociales' AS tabla, COUNT(*) AS filas FROM obras_sociales
UNION ALL SELECT 'especialidades', COUNT(*) FROM especialidades
UNION ALL SELECT 'personas', COUNT(*) FROM personas
UNION ALL SELECT 'medicos', COUNT(*) FROM medicos
UNION ALL SELECT 'medico_especialidad', COUNT(*) FROM medico_especialidad
UNION ALL SELECT 'pacientes', COUNT(*) FROM pacientes
UNION ALL SELECT 'antecedentes_ginecologicos', COUNT(*) FROM antecedentes_ginecologicos
UNION ALL SELECT 'consultas', COUNT(*) FROM consultas
UNION ALL SELECT 'consultas_ginecologia', COUNT(*) FROM consultas_ginecologia;
