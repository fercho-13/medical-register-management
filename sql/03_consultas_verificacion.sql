-- Consultas de prueba para verificar los datos cargados y las búsquedas de la aplicación.
-- Solo lectura. Ejecutar después de 02_datos_prueba.sql:
--   mysql -u lalcec -p lalcec_db < sql/03_consultas_verificacion.sql

USE lalcec_db;

-- 1) Búsqueda por DNI (prefijo), como el buscador de la pantalla principal. Esperado: 'T1001'...'T1008'
SELECT p.dni, p.apellido, p.nombre, pa.numero_hc
FROM personas p JOIN pacientes pa ON pa.id_paciente = p.id_persona
WHERE p.dni LIKE 'T10%'
ORDER BY p.dni;

-- 2) Búsqueda por apellido (prefijo, sin distinguir mayúsculas). Esperado: Pérez (TST-001)
SELECT p.dni, p.apellido, p.nombre, pa.numero_hc
FROM personas p JOIN pacientes pa ON pa.id_paciente = p.id_persona
WHERE p.apellido LIKE 'pér%';

-- 3) Búsqueda por N° de HC exacto. Esperado: 1 fila (TST-003, Carmen Lopez)
SELECT p.dni, p.apellido, p.nombre, pa.numero_hc, o.nombre AS obra_social
FROM personas p
JOIN pacientes pa ON pa.id_paciente = p.id_persona
LEFT JOIN obras_sociales o ON o.id_obrasocial = pa.fk_obrasocial
WHERE pa.numero_hc = 'TST-003';

-- 4) Historial de consultas de una paciente (igual que la pantalla de historia). Esperado: 4 filas para TST-001 (2 generales y 2 ginecológicas)
SELECT c.id_consulta, c.fecha_hora, c.motivo,
       CONCAT(pm.apellido, ', ', pm.nombre, ' — ', e.nombre) AS medico_especialidad,
       g.id_consulta_gin IS NOT NULL AS tiene_detalle_gin
FROM consultas c
JOIN pacientes pa ON pa.id_paciente = c.fk_paciente
JOIN medico_especialidad me ON me.id_medico_esp = c.fk_medico_esp
JOIN medicos m ON m.id_medico = me.fk_medico
JOIN personas pm ON pm.id_persona = m.id_medico
JOIN especialidades e ON e.id_especialidad = me.fk_especialidad
LEFT JOIN consultas_ginecologia g ON g.id_consulta_gin = c.id_consulta
WHERE pa.numero_hc = 'TST-001'
ORDER BY c.fecha_hora DESC;

-- 5) Detalle ginecológico completo de las consultas con formulario ginecológico. Esperado: 6 filas
SELECT c.id_consulta, pa.numero_hc, c.fecha_hora, g.papanicolau_resultado, g.papanicolau_fecha,
       g.schiller, g.imagen_colposcopica, g.diagnostico, g.plan_tratamiento
FROM consultas_ginecologia g
JOIN consultas c ON c.id_consulta = g.id_consulta_gin
JOIN pacientes pa ON pa.id_paciente = c.fk_paciente
ORDER BY c.fecha_hora;

-- 6) Síntomas marcados por consulta (solo los que están en 1). Esperado: TST-001 (05/2026), TST-002, TST-003 y TST-007
SELECT pa.numero_hc, c.fecha_hora,
       CONCAT_WS(', ',
           IF(g.dolor_bajo_vientre, 'Dolor bajo vientre', NULL),
           IF(g.flujo, 'Flujo', NULL),
           IF(g.prurito, 'Prurito', NULL),
           IF(g.metrorragia, 'Metrorragia', NULL),
           IF(g.alteracion_ciclo, 'Alteración del ciclo', NULL),
           IF(g.patologia_mamaria, 'Patología mamaria', NULL)) AS sintomas
FROM consultas_ginecologia g
JOIN consultas c ON c.id_consulta = g.id_consulta_gin
JOIN pacientes pa ON pa.id_paciente = c.fk_paciente
WHERE g.dolor_bajo_vientre OR g.flujo OR g.prurito OR g.metrorragia
   OR g.alteracion_ciclo OR g.patologia_mamaria
ORDER BY c.fecha_hora;

-- 7) Antecedentes ginecológicos por paciente. Esperado: 6 pacientes con ficha (TST-001, 002, 003, 006, 007 y 008)
SELECT pa.numero_hc, ag.menarca_edad, ag.fecha_ultima_menstruacion, ag.cantidad_embarazos,
       ag.cantidad_partos, ag.cantidad_abortos, ag.metodo_anticonceptivo, ag.antecedentes_familiares
FROM antecedentes_ginecologicos ag
JOIN pacientes pa ON pa.id_paciente = ag.fk_paciente
ORDER BY pa.numero_hc;

-- 8) Cantidad de consultas por médico/especialidad. Esperado: Gómez/Ginecología=4, Ruiz/Clínica General=3, Techo (ambas)=1 cada una
SELECT CONCAT(pm.apellido, ', ', pm.nombre) AS medico, e.nombre AS especialidad, COUNT(c.id_consulta) AS consultas
FROM medico_especialidad me
JOIN medicos m ON m.id_medico = me.fk_medico
JOIN personas pm ON pm.id_persona = m.id_medico
JOIN especialidades e ON e.id_especialidad = me.fk_especialidad
LEFT JOIN consultas c ON c.fk_medico_esp = me.id_medico_esp
GROUP BY me.id_medico_esp, medico, especialidad
ORDER BY medico, especialidad;

-- 9) Pacientes sin ninguna consulta. Esperado: TST-004, TST-006
SELECT pa.numero_hc, p.apellido, p.nombre
FROM pacientes pa
JOIN personas p ON p.id_persona = pa.id_paciente
LEFT JOIN consultas c ON c.fk_paciente = pa.id_paciente
WHERE c.id_consulta IS NULL
ORDER BY pa.numero_hc;

-- 10) Pacientes con obra social y sin obra social. Esperado: 4 con cobertura, 4 sin
SELECT IF(pa.fk_obrasocial IS NULL, 'Sin obra social', 'Con obra social') AS cobertura, COUNT(*) AS pacientes
FROM pacientes pa
GROUP BY cobertura;

-- 11) Control de integridad: todas deben devolver 0
SELECT 'detalles gin sin consulta' AS control, COUNT(*) AS errores
FROM consultas_ginecologia g LEFT JOIN consultas c ON c.id_consulta = g.id_consulta_gin
WHERE c.id_consulta IS NULL
UNION ALL
SELECT 'pacientes sin persona', COUNT(*)
FROM pacientes pa LEFT JOIN personas p ON p.id_persona = pa.id_paciente
WHERE p.id_persona IS NULL
UNION ALL
SELECT 'medicos sin especialidad', COUNT(*)
FROM medicos m LEFT JOIN medico_especialidad me ON me.fk_medico = m.id_medico
WHERE me.id_medico_esp IS NULL
UNION ALL
SELECT 'consultas de paciente inexistente', COUNT(*)
FROM consultas c LEFT JOIN pacientes pa ON pa.id_paciente = c.fk_paciente
WHERE pa.id_paciente IS NULL;
