-- =====================================================================
-- CARGA DE DATOS: EQUIPO MÉDICO DE LALCEC OLAVARRÍA (+ pacientes ficticios de prueba)
--
-- Fuente: imagen oficial "Equipo médico" de LALCEC Olavarría (folleto provisto por la institución).
-- Por el momento se usa para testeo.
--
-- Uso (sobre una base vacía):
--   1) mysql -u root -p < sql/schema.sql
--   2) mysql -u root -p lalcec_db < sql/vaciar_base_de_datos.sql      (si ya había datos)
--   3) mysql -u root -p lalcec_db --default-character-set=utf8mb4 < sql/datos_equipo_medico.sql
-- No está pensado para ejecutarse dos veces seguidas: el DNI es único y la segunda corrida fallaría.
-- Para repetirlo, ejecutar antes vaciar_base_de_datos.sql.
--
-- LO QUE VIENE DE LA IMAGEN (sección 1): nombre, matrícula y especialidad de cada profesional.
-- LO QUE NO ESTÁ EN LA IMAGEN y se completó provisoriamente:
--   * DNI: no figura, y la columna es obligatoria y única. Se usa un valor provisorio
--     'PEND-<matrícula>' que hay que reemplazar por el DNI real:
--       UPDATE personas SET dni = '12345678' WHERE dni = 'PEND-81988';
--   * Sexo, teléfono y dirección: se dejan en NULL (no figuran).
--   * Dra. Constanza Lastape (ginecología) aparece en un recuadro agregado, sin matrícula:
--     se guarda la matrícula como 'S/D' (sin dato).
--   * En la imagen hay un recuadro tapado ("XXXXXXX"): ese profesional NO se incluyó.
--
-- Especialidades: "Ginecología y Obstetricia" (Oscar Vera Barros) se carga como dos especialidades,
-- Ginecología y Obstetricia. La aplicación muestra el formulario ginecológico detallado solo para las
-- especialidades cuyo nombre contiene "ginecolog" o "cervical"; Obstetricia usa el formulario general.
--
-- La sección 2 (pacientes ficticios, DNI 'PRUEBA-xxx') es solo para probar la aplicación. Se puede
-- borrar sin afectar a la sección 1.
-- =====================================================================
SET NAMES utf8mb4;
USE lalcec_db;

-- =====================================================================
-- SECCIÓN 1: EQUIPO MÉDICO (datos de la imagen)
-- =====================================================================

-- ---------- Especialidades (no duplica las que ya existan) ----------
INSERT INTO especialidades (nombre)
SELECT v.nombre FROM (
    SELECT 'Ginecología' AS nombre
    UNION ALL SELECT 'Obstetricia'
    UNION ALL SELECT 'Patología Cervical'
    UNION ALL SELECT 'Mastología'
    UNION ALL SELECT 'Clínica General'
    UNION ALL SELECT 'Medicina General'
    UNION ALL SELECT 'Medicina General y Familiar'
    UNION ALL SELECT 'Neumonología'
    UNION ALL SELECT 'Nutrición'
    UNION ALL SELECT 'Dermatología') v
WHERE NOT EXISTS (SELECT 1 FROM especialidades e WHERE e.nombre = v.nombre);

-- ---------- Profesionales (persona + médico) ----------
INSERT INTO personas (dni, nombre, apellido) VALUES
 ('PEND-81988',    'Oscar',     'Vera Barros'),
 ('PEND-81612',    'Virginia',  'Draghi'),
 ('PEND-82112',    'Victoria',  'Nogal'),
 ('PEND-81356',    'Natalia',   'Alvarez'),
 ('PEND-4076',     'Marina',    'Gamondi'),
 ('PEND-229150',   'Carolina',  'Fernández'),
 ('PEND-LASTAPE',  'Constanza', 'Lastape');

INSERT INTO medicos (id_medico, matricula) VALUES
 ((SELECT id_persona FROM personas WHERE dni = 'PEND-81988'),   '81988'),
 ((SELECT id_persona FROM personas WHERE dni = 'PEND-81612'),   '81612'),
 ((SELECT id_persona FROM personas WHERE dni = 'PEND-82112'),   '82112'),
 ((SELECT id_persona FROM personas WHERE dni = 'PEND-81356'),   '81356'),
 ((SELECT id_persona FROM personas WHERE dni = 'PEND-4076'),    '4076'),
 ((SELECT id_persona FROM personas WHERE dni = 'PEND-229150'),  '229150'),
 ((SELECT id_persona FROM personas WHERE dni = 'PEND-LASTAPE'), 'S/D');

-- ---------- Especialidad de cada profesional (según la imagen) ----------
INSERT INTO medico_especialidad (fk_medico, fk_especialidad) VALUES
 -- Oscar Vera Barros: Esp. Ginecología y Obstetricia
 ((SELECT id_persona FROM personas WHERE dni = 'PEND-81988'),   (SELECT id_especialidad FROM especialidades WHERE nombre = 'Ginecología')),
 ((SELECT id_persona FROM personas WHERE dni = 'PEND-81988'),   (SELECT id_especialidad FROM especialidades WHERE nombre = 'Obstetricia')),
 -- Virginia Draghi: Esp. Medicina gral. y Familiar
 ((SELECT id_persona FROM personas WHERE dni = 'PEND-81612'),   (SELECT id_especialidad FROM especialidades WHERE nombre = 'Medicina General y Familiar')),
 -- Victoria Nogal: Medicina gral.
 ((SELECT id_persona FROM personas WHERE dni = 'PEND-82112'),   (SELECT id_especialidad FROM especialidades WHERE nombre = 'Medicina General')),
 -- Natalia Alvarez: Neumonóloga
 ((SELECT id_persona FROM personas WHERE dni = 'PEND-81356'),   (SELECT id_especialidad FROM especialidades WHERE nombre = 'Neumonología')),
 -- Marina Gamondi: Lic. Nutrición
 ((SELECT id_persona FROM personas WHERE dni = 'PEND-4076'),    (SELECT id_especialidad FROM especialidades WHERE nombre = 'Nutrición')),
 -- Carolina Fernández: Dermatología
 ((SELECT id_persona FROM personas WHERE dni = 'PEND-229150'),  (SELECT id_especialidad FROM especialidades WHERE nombre = 'Dermatología')),
 -- Constanza Lastape: ginecología
 ((SELECT id_persona FROM personas WHERE dni = 'PEND-LASTAPE'), (SELECT id_especialidad FROM especialidades WHERE nombre = 'Ginecología'));

-- =====================================================================
-- SECCIÓN 2: PACIENTES FICTICIOS PARA PROBAR LA APLICACIÓN (no son personas reales)
-- =====================================================================

-- ---------- Obras sociales ----------
INSERT INTO obras_sociales (nombre)
SELECT v.nombre FROM (SELECT 'OSDE' AS nombre UNION ALL SELECT 'PAMI' UNION ALL SELECT 'IOMA') v
WHERE NOT EXISTS (SELECT 1 FROM obras_sociales o WHERE o.nombre = v.nombre);

SET @os_osde = (SELECT id_obrasocial FROM obras_sociales WHERE nombre = 'OSDE');
SET @os_pami = (SELECT id_obrasocial FROM obras_sociales WHERE nombre = 'PAMI');
SET @os_ioma = (SELECT id_obrasocial FROM obras_sociales WHERE nombre = 'IOMA');

-- ---------- Pacientes ----------
INSERT INTO personas (dni, nombre, apellido, fecha_nacimiento, sexo, direccion, telefono) VALUES
 ('PRUEBA-001', 'María',   'Pérez',   '1978-05-02', 'F', 'Av. Pellegrini 100', '2284-400001'),
 ('PRUEBA-002', 'Lucía',   'Fernández','1992-11-17', 'F', 'Belgrano 560',      '2284-400002'),
 ('PRUEBA-003', 'Carmen',  'López',   '1955-02-28', 'F', 'Sarmiento 890',      '2284-400003'),
 ('PRUEBA-004', 'Roberto', 'Alonso',  '1969-09-30', 'M', 'Mitre 455',          '2284-400004'),
 ('PRUEBA-005', 'Julieta', 'Sosa',    '2001-07-09', 'F', NULL,                 NULL);

SET @p1 = (SELECT id_persona FROM personas WHERE dni = 'PRUEBA-001');
SET @p2 = (SELECT id_persona FROM personas WHERE dni = 'PRUEBA-002');
SET @p3 = (SELECT id_persona FROM personas WHERE dni = 'PRUEBA-003');
SET @p4 = (SELECT id_persona FROM personas WHERE dni = 'PRUEBA-004');
SET @p5 = (SELECT id_persona FROM personas WHERE dni = 'PRUEBA-005');

INSERT INTO pacientes (id_paciente, fk_obrasocial, numero_afiliado, numero_hc, nacionalidad, religion, estado_civil,
                       allegado_nombre, allegado_domicilio, allegado_telefono, antecedentes_clinicos) VALUES
 (@p1, @os_osde, '50001', 'PRU-001', 'Argentina', 'Católica', 'Casada',  'Jorge Pérez',  'Av. Pellegrini 100', '2284-410001', 'Hipertensión arterial controlada.'),
 (@p2, NULL,     NULL,    'PRU-002', 'Argentina', NULL,       'Soltera', NULL, NULL, NULL, NULL),
 (@p3, @os_pami, '50003', 'PRU-003', 'Argentina', 'Católica', 'Viuda',   'Nicolás López','Sarmiento 890',      '2284-410003', 'Diabetes tipo 2.'),
 (@p4, @os_ioma, '50004', 'PRU-004', 'Argentina', NULL,       'Casado',  NULL, NULL, NULL, 'Asma leve.'),
 (@p5, NULL,     NULL,    'PRU-005', 'Argentina', NULL,       'Soltera', NULL, NULL, NULL, NULL);

-- ---------- Antecedentes ginecológicos (pacientes 1, 2 y 3) ----------
INSERT INTO antecedentes_ginecologicos (fk_paciente, menarca_edad, menopausia_edad, fecha_ultima_menstruacion,
    inicio_relaciones_sexuales_edad, cantidad_embarazos, cantidad_partos, cantidad_abortos,
    uso_anticonceptivos, metodo_anticonceptivo, medicacion_hormonal, medicacion_hormonal_detalle,
    electrocoagulacion_cuello, electrocoagulacion_cuantas, ultima_coagulacion, otras_coagulaciones,
    antecedentes_familiares, antecedentes_personales) VALUES
 (@p1, 12, NULL, '2026-11-02', 19, 2, 2, 0, 1, 'Preservativo', 0, NULL, 1, 1, '2019-04-10', NULL, 'Madre con cáncer de mama.', 'Ninguno relevante.'),
 (@p2, 13, NULL, '2026-11-15', 21, 0, 0, 0, 0, NULL,           0, NULL, 0, 0, NULL,         NULL, NULL,                        NULL),
 (@p3, 11, 52,   '2016-01-15', 18, 3, 3, 0, 0, NULL,           1, 'Estrógenos hasta 2016', 0, 0, NULL, NULL, NULL,              'Menopausia a los 52 años.');

-- ---------- Consultas ----------
-- Atajos: id de "médico + especialidad" según la imagen
SET @me_vera      = (SELECT me.id_medico_esp FROM medico_especialidad me JOIN personas p ON p.id_persona = me.fk_medico JOIN especialidades e ON e.id_especialidad = me.fk_especialidad WHERE p.dni = 'PEND-81988'   AND e.nombre = 'Ginecología');
SET @me_draghi    = (SELECT me.id_medico_esp FROM medico_especialidad me JOIN personas p ON p.id_persona = me.fk_medico JOIN especialidades e ON e.id_especialidad = me.fk_especialidad WHERE p.dni = 'PEND-81612'   AND e.nombre = 'Medicina General y Familiar');
SET @me_nogal     = (SELECT me.id_medico_esp FROM medico_especialidad me JOIN personas p ON p.id_persona = me.fk_medico JOIN especialidades e ON e.id_especialidad = me.fk_especialidad WHERE p.dni = 'PEND-82112'   AND e.nombre = 'Medicina General');
SET @me_alvarez   = (SELECT me.id_medico_esp FROM medico_especialidad me JOIN personas p ON p.id_persona = me.fk_medico JOIN especialidades e ON e.id_especialidad = me.fk_especialidad WHERE p.dni = 'PEND-81356'   AND e.nombre = 'Neumonología');
SET @me_gamondi   = (SELECT me.id_medico_esp FROM medico_especialidad me JOIN personas p ON p.id_persona = me.fk_medico JOIN especialidades e ON e.id_especialidad = me.fk_especialidad WHERE p.dni = 'PEND-4076'    AND e.nombre = 'Nutrición');
SET @me_fernandez = (SELECT me.id_medico_esp FROM medico_especialidad me JOIN personas p ON p.id_persona = me.fk_medico JOIN especialidades e ON e.id_especialidad = me.fk_especialidad WHERE p.dni = 'PEND-229150'  AND e.nombre = 'Dermatología');
SET @me_lastape   = (SELECT me.id_medico_esp FROM medico_especialidad me JOIN personas p ON p.id_persona = me.fk_medico JOIN especialidades e ON e.id_especialidad = me.fk_especialidad WHERE p.dni = 'PEND-LASTAPE'  AND e.nombre = 'Ginecología');

-- Consultas generales (texto libre)
INSERT INTO consultas (fk_paciente, fk_medico_esp, fecha_hora, motivo, observaciones) VALUES
 (@p1, @me_draghi,    '2026-06-03 09:30:00', 'Control de presión',   'TA 140/90. Se ajusta la medicación.'),
 (@p3, @me_nogal,     '2026-08-14 10:00:00', 'Control de diabetes',  'Glucemia en ayunas 140 mg/dl. Continúa tratamiento.'),
 (@p3, @me_alvarez,   '2026-10-02 11:15:00', 'Tos persistente',      'Espirometría solicitada.'),
 (@p4, @me_fernandez, '2026-07-22 15:00:00', 'Lesión en la piel',    'Dermatitis de contacto. Se indica corticoide tópico.'),
 (@p4, @me_gamondi,   '2026-09-30 16:30:00', 'Plan alimentario',     'Se entrega plan hipocalórico. Control en 30 días.');

-- Consultas ginecológicas (consulta + detalle; el detalle reutiliza el id con LAST_INSERT_ID())
INSERT INTO consultas (fk_paciente, fk_medico_esp, fecha_hora, motivo, observaciones)
VALUES (@p1, @me_vera, '2026-09-15 16:00:00', 'Control ginecológico anual', 'Refiere flujo leve.');
INSERT INTO consultas_ginecologia (id_consulta_gin, flujo, prurito, papanicolau_resultado, papanicolau_fecha, examen_flujo_bacterial, diagnostico, plan_tratamiento)
VALUES (LAST_INSERT_ID(), 1, 1, 'NILM', '2026-09-15', 'Flora mixta', 'Vaginitis inespecífica', 'Tratamiento local. Control en 3 meses.');

INSERT INTO consultas (fk_paciente, fk_medico_esp, fecha_hora, motivo, observaciones)
VALUES (@p2, @me_lastape, '2026-10-20 09:00:00', 'Dolor menstrual', NULL);
INSERT INTO consultas_ginecologia (id_consulta_gin, dolor_bajo_vientre, alteracion_ciclo, diagnostico, plan_tratamiento)
VALUES (LAST_INSERT_ID(), 1, 1, 'Dismenorrea primaria', 'Antiinflamatorios en los días de dolor.');

-- ---------- Resumen ----------
SELECT 'obras_sociales' AS tabla, COUNT(*) AS filas FROM obras_sociales
UNION ALL SELECT 'especialidades', COUNT(*) FROM especialidades
UNION ALL SELECT 'personas', COUNT(*) FROM personas
UNION ALL SELECT 'medicos', COUNT(*) FROM medicos
UNION ALL SELECT 'medico_especialidad', COUNT(*) FROM medico_especialidad
UNION ALL SELECT 'pacientes', COUNT(*) FROM pacientes
UNION ALL SELECT 'antecedentes_ginecologicos', COUNT(*) FROM antecedentes_ginecologicos
UNION ALL SELECT 'consultas', COUNT(*) FROM consultas
UNION ALL SELECT 'consultas_ginecologia', COUNT(*) FROM consultas_ginecologia;

-- Equipo médico cargado (para comparar con la imagen)
SELECT CONCAT(p.nombre, ' ', p.apellido) AS profesional, m.matricula, e.nombre AS especialidad
FROM medico_especialidad me
JOIN medicos m ON m.id_medico = me.fk_medico
JOIN personas p ON p.id_persona = m.id_medico
JOIN especialidades e ON e.id_especialidad = me.fk_especialidad
ORDER BY p.apellido, e.nombre;
