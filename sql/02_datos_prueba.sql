-- SOLO DESARROLLO. Carga médicos, pacientes, antecedentes y consultas de prueba.
-- Todos los DNI empiezan con 'T' para poder borrarlos con 01_limpiar_datos_prueba.sql.
-- Ejecutar DESPUÉS de schema.sql. Se puede correr varias veces sólo si antes se ejecutó la limpieza.
USE lalcec_db;

-- ---------- Obras sociales ----------
INSERT INTO obras_sociales (nombre)
SELECT 'TEST OSDE' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM obras_sociales WHERE nombre = 'TEST OSDE');
INSERT INTO obras_sociales (nombre)
SELECT 'TEST PAMI' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM obras_sociales WHERE nombre = 'TEST PAMI');
INSERT INTO obras_sociales (nombre)
SELECT 'TEST IOMA' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM obras_sociales WHERE nombre = 'TEST IOMA');

-- ---------- Médicos (persona + médico) ----------
INSERT INTO personas (dni, nombre, apellido, sexo, telefono) VALUES
 ('T9001', 'Laura', 'Gómez', 'F', '2284-500100'),
 ('T9002', 'Martín', 'Ruiz', 'M', '2284-500200'),
 ('T9003', 'Sofía', 'Techo', 'F', '2284-500300');

INSERT INTO medicos (id_medico, matricula)
SELECT id_persona, 'TMP-1001' FROM personas WHERE dni = 'T9001'
UNION ALL SELECT id_persona, 'TMP-1002' FROM personas WHERE dni = 'T9002'
UNION ALL SELECT id_persona, 'TMP-1003' FROM personas WHERE dni = 'T9003';

-- Especialidades: Laura = Ginecología y Clínica General; Martín = Clínica General; Sofía = Patología Cervical y Mastología
INSERT INTO medico_especialidad (fk_medico, fk_especialidad)
SELECT m.id_medico, e.id_especialidad
FROM medicos m
JOIN personas p ON p.id_persona = m.id_medico
JOIN especialidades e
WHERE (p.dni = 'T9001' AND e.nombre IN ('Ginecología', 'Clínica General'))
   OR (p.dni = 'T9002' AND e.nombre = 'Clínica General')
   OR (p.dni = 'T9003' AND e.nombre IN ('Patología Cervical', 'Mastología'));

-- ---------- Pacientes (persona + paciente) ----------
INSERT INTO personas (dni, nombre, apellido, fecha_nacimiento, sexo, direccion, telefono) VALUES
 ('T1001', 'María',    'Pérez',     '1978-05-02', 'F', 'Av. Bolívar 1234',     '2284-410001'),
 ('T1002', 'Lucía',    'Fernández', '1992-11-17', 'F', 'Belgrano 560',          '2284-410002'),
 ('T1003', 'Carmen',   'Lopez',     '1955-02-28', 'F', 'Sarmiento 890',        '2284-410003'),
 ('T1004', 'Julieta',  'Sosa',      '2001-07-09', 'F', 'Rivadavia 2100',       NULL),
 ('T1005', 'Roberto',  'Alonso',    '1969-09-30', 'M', 'Mitre 455',            '2284-410005'),
 ('T1006', 'Andrea',   'Molina',    '1985-03-14', 'F', 'Pringles 310',         '2284-410006'),
 ('T1007', 'Gabriela', 'Ríos',      '1948-12-01', 'F', 'Azcuénaga 77',         '2284-410007'),
 ('T1008', 'Patricia', 'Herrera',   '1990-06-21', 'F', 'Dorrego 1500',         '2284-410008');

INSERT INTO pacientes (id_paciente, fk_obrasocial, numero_afiliado, numero_hc, nacionalidad, religion, estado_civil,
                       allegado_nombre, allegado_domicilio, allegado_telefono, antecedentes_clinicos)
SELECT p.id_persona,
       (SELECT id_obrasocial FROM obras_sociales WHERE nombre = 'TEST OSDE'),
       '30001', 'TST-001', 'Argentina', 'Católica', 'Casada',
       'Jorge Pérez', 'Av. Bolívar 1234', '2284-420001', 'Hipertensión arterial controlada.'
FROM personas p WHERE p.dni = 'T1001'
UNION ALL
SELECT p.id_persona, NULL, NULL, 'TST-002', 'Argentina', NULL, 'Soltera',
       NULL, NULL, NULL, NULL
FROM personas p WHERE p.dni = 'T1002'
UNION ALL
SELECT p.id_persona,
       (SELECT id_obrasocial FROM obras_sociales WHERE nombre = 'TEST PAMI'),
       '30003', 'TST-003', 'Argentina', 'Católica', 'Viuda',
       'Nicolás Lopez', 'Sarmiento 890', '2284-420003', 'Diabetes tipo 2. Cirugía de cadera (2019).'
FROM personas p WHERE p.dni = 'T1003'
UNION ALL
SELECT p.id_persona, NULL, NULL, 'TST-004', 'Argentina', NULL, 'Soltera',
       'Ana Sosa', 'Rivadavia 2100', NULL, 'Asma leve.'
FROM personas p WHERE p.dni = 'T1004'
UNION ALL
SELECT p.id_persona,
       (SELECT id_obrasocial FROM obras_sociales WHERE nombre = 'TEST IOMA'),
       '30005', 'TST-005', 'Argentina', NULL, 'Casado',
       'Silvia Alonso', 'Mitre 455', '2284-420005', NULL
FROM personas p WHERE p.dni = 'T1005'
UNION ALL
SELECT p.id_persona, NULL, NULL, 'TST-006', 'Uruguaya', 'Ninguna', 'Divorciada',
       NULL, NULL, NULL, 'Hipotiroidismo.'
FROM personas p WHERE p.dni = 'T1006'
UNION ALL
SELECT p.id_persona,
       (SELECT id_obrasocial FROM obras_sociales WHERE nombre = 'TEST PAMI'),
       '30007', 'TST-007', 'Argentina', 'Católica', 'Viuda',
       'Carlos Ríos', 'Azcuénaga 77', '2284-420007', 'Artrosis de rodilla. Cáncer de mama (2015), en seguimiento.'
FROM personas p WHERE p.dni = 'T1007'
UNION ALL
SELECT p.id_persona, NULL, NULL, 'TST-008', 'Argentina', NULL, 'Soltera',
       NULL, NULL, NULL, NULL
FROM personas p WHERE p.dni = 'T1008';

-- ---------- Antecedentes ginecológicos (1 por paciente de ginecología) ----------
INSERT INTO antecedentes_ginecologicos (fk_paciente, menarca_edad, menopausia_edad, fecha_ultima_menstruacion,
    inicio_relaciones_sexuales_edad, cantidad_embarazos, cantidad_partos, cantidad_abortos,
    uso_anticonceptivos, metodo_anticonceptivo, medicacion_hormonal, medicacion_hormonal_detalle,
    electrocoagulacion_cuello, electrocoagulacion_cuantas, ultima_coagulacion, antecedentes_familiares, antecedentes_personales)
SELECT id_paciente, 12, NULL, '2026-10-20', 19, 2, 2, 0, 1, 'Preservativo', 0, NULL,
       1, 1, '2019-04-10', 'Madre con cáncer de mama.', 'Ninguno relevante.'
FROM pacientes WHERE numero_hc = 'TST-001'
UNION ALL
SELECT id_paciente, 13, NULL, '2026-10-28', 21, 0, 0, 0, 0, NULL, 0, NULL,
       0, 0, NULL, NULL, NULL
FROM pacientes WHERE numero_hc = 'TST-002'
UNION ALL
SELECT id_paciente, 11, 52, '2016-01-15', 18, 3, 3, 0, 0, NULL, 1, 'Estrógenos (hasta 2016)',
       0, 0, NULL, NULL, 'Menopausia a los 52 años.'
FROM pacientes WHERE numero_hc = 'TST-003'
UNION ALL
SELECT id_paciente, 14, NULL, '2026-09-30', 20, 1, 1, 0, 1, 'Anillo vaginal', 0, NULL,
       0, 0, NULL, NULL, NULL
FROM pacientes WHERE numero_hc = 'TST-006'
UNION ALL
SELECT id_paciente, 12, 49, '2015-06-01', 22, 1, 1, 0, 0, NULL, 0, NULL,
       0, 0, NULL, 'Hermana con cáncer de mama.', 'Cáncer de mama en 2015, tratada con cirugía y radioterapia.'
FROM pacientes WHERE numero_hc = 'TST-007'
UNION ALL
SELECT id_paciente, 13, NULL, '2026-10-05', 17, 0, 0, 1, 1, 'DIU', 0, NULL,
       1, 2, '2024-02-03', NULL, 'Alteraciones leves en PAP en 2024.'
FROM pacientes WHERE numero_hc = 'TST-008';

-- ---------- Consultas generales (texto libre) ----------
-- Pacientes TST-001 y TST-005 con clínica general
INSERT INTO consultas (fk_paciente, fk_medico_esp, fecha_hora, motivo, observaciones)
SELECT p.id_paciente,
       (SELECT me.id_medico_esp FROM medico_especialidad me
          JOIN medicos m ON m.id_medico = me.fk_medico
          JOIN personas pm ON pm.id_persona = m.id_medico
          JOIN especialidades e ON e.id_especialidad = me.fk_especialidad
         WHERE pm.dni = 'T9002' AND e.nombre = 'Clínica General'),
       '2026-06-03 09:30:00', 'Control de presión', 'TA 140/90. Ajuste de medicación.'
FROM pacientes p WHERE p.numero_hc = 'TST-001'
UNION ALL
SELECT p.id_paciente,
       (SELECT me.id_medico_esp FROM medico_especialidad me
          JOIN medicos m ON m.id_medico = me.fk_medico
          JOIN personas pm ON pm.id_persona = m.id_medico
          JOIN especialidades e ON e.id_especialidad = me.fk_especialidad
         WHERE pm.dni = 'T9002' AND e.nombre = 'Clínica General'),
       '2026-08-14 10:00:00', 'Control de presión', 'TA 130/85. Continúa tratamiento.'
FROM pacientes p WHERE p.numero_hc = 'TST-001';

INSERT INTO consultas (fk_paciente, fk_medico_esp, fecha_hora, motivo, observaciones)
SELECT p.id_paciente,
       (SELECT me.id_medico_esp FROM medico_especialidad me
          JOIN medicos m ON m.id_medico = me.fk_medico
          JOIN personas pm ON pm.id_persona = m.id_medico
          JOIN especialidades e ON e.id_especialidad = me.fk_especialidad
         WHERE pm.dni = 'T9002' AND e.nombre = 'Clínica General'),
       '2026-07-22 11:15:00', 'Dolor lumbar', 'Lumbalgia mecánica. Reposo y analgésicos.'
FROM pacientes p WHERE p.numero_hc = 'TST-005';

-- ---------- Consultas ginecológicas (con detalle) ----------
-- Cada consulta va en dos pasos: el INSERT en consultas y luego el detalle en consultas_ginecologia,
-- usando LAST_INSERT_ID() (por eso son sentencias individuales, no UNION).

-- 1) TST-001 con Laura Gómez (Ginecología): control de rutina, PAP normal
INSERT INTO consultas (fk_paciente, fk_medico_esp, fecha_hora, motivo, observaciones)
SELECT p.id_paciente,
       (SELECT me.id_medico_esp FROM medico_especialidad me
          JOIN medicos m ON m.id_medico = me.fk_medico
          JOIN personas pm ON pm.id_persona = m.id_medico
          JOIN especialidades e ON e.id_especialidad = me.fk_especialidad
         WHERE pm.dni = 'T9001' AND e.nombre = 'Ginecología'),
       '2026-05-10 16:00:00', 'Control ginecológico anual', 'Refiere flujo leve, sin otros síntomas.'
FROM pacientes p WHERE p.numero_hc = 'TST-001';
INSERT INTO consultas_ginecologia (id_consulta_gin, flujo, prurito, papanicolau_resultado, papanicolau_fecha,
    examen_flujo_bacterial, diagnostico, plan_tratamiento)
VALUES (LAST_INSERT_ID(), 1, 1, 'NILM', '2026-05-10', 'Normal',
        'Vaginitis inespecífica', 'Higiene local. Control en 3 meses.');

-- 2) TST-001 con Laura Gómez: PAP con atipia, colposcopía y biopsia indicada
INSERT INTO consultas (fk_paciente, fk_medico_esp, fecha_hora, motivo, observaciones)
SELECT p.id_paciente,
       (SELECT me.id_medico_esp FROM medico_especialidad me
          JOIN medicos m ON m.id_medico = me.fk_medico
          JOIN personas pm ON pm.id_persona = m.id_medico
          JOIN especialidades e ON e.id_especialidad = me.fk_especialidad
         WHERE pm.dni = 'T9001' AND e.nombre = 'Ginecología'),
       '2026-09-15 16:30:00', 'PAP alterado', 'Se deriva a colposcopía.'
FROM pacientes p WHERE p.numero_hc = 'TST-001';
INSERT INTO consultas_ginecologia (id_consulta_gin, papanicolau_resultado, papanicolau_fecha, schiller,
    imagen_colposcopica, examen_mamario_inspeccion, examen_mamario_palpacion, adenopatias, diagnostico, plan_tratamiento)
VALUES (LAST_INSERT_ID(), 'ASCUS', '2026-09-15', 'Positivo (zona yodo negativa)',
        'Zona de transformación tipo 2, bordes definidos', 'Sin lesiones', 'Sin nódulos palpables',
        'No palpables', 'Atipia de significado indeterminado', 'Colposcopía con biopsia dirigida.');

-- 3) TST-002 con Laura Gómez: dolor menstrual
INSERT INTO consultas (fk_paciente, fk_medico_esp, fecha_hora, motivo, observaciones)
SELECT p.id_paciente,
       (SELECT me.id_medico_esp FROM medico_especialidad me
          JOIN medicos m ON m.id_medico = me.fk_medico
          JOIN personas pm ON pm.id_persona = m.id_medico
          JOIN especialidades e ON e.id_especialidad = me.fk_especialidad
         WHERE pm.dni = 'T9001' AND e.nombre = 'Ginecología'),
       '2026-10-30 09:00:00', 'Dolor menstrual intenso', NULL
FROM pacientes p WHERE p.numero_hc = 'TST-002';
INSERT INTO consultas_ginecologia (id_consulta_gin, dolor_bajo_vientre, alteracion_ciclo, diagnostico, plan_tratamiento)
VALUES (LAST_INSERT_ID(), 1, 1, 'Dismenorrea primaria', 'Antiinflamatorios en los días de dolor.');

-- 4) TST-003 con Laura Gómez: sangrado postmenopáusico
INSERT INTO consultas (fk_paciente, fk_medico_esp, fecha_hora, motivo, observaciones)
SELECT p.id_paciente,
       (SELECT me.id_medico_esp FROM medico_especialidad me
          JOIN medicos m ON m.id_medico = me.fk_medico
          JOIN personas pm ON pm.id_persona = m.id_medico
          JOIN especialidades e ON e.id_especialidad = me.fk_especialidad
         WHERE pm.dni = 'T9001' AND e.nombre = 'Ginecología'),
       '2026-08-05 11:00:00', 'Metrorragia', 'Sangrado leve de 3 días.'
FROM pacientes p WHERE p.numero_hc = 'TST-003';
INSERT INTO consultas_ginecologia (id_consulta_gin, metrorragia, papanicolau_resultado, papanicolau_fecha,
    diagnostico, plan_tratamiento)
VALUES (LAST_INSERT_ID(), 1, 'NILM', '2026-08-05', 'Metrorragia postmenopáusica, en estudio.',
        'Ecografía transvaginal y control en 15 días.');

-- 5) TST-007 con Sofía Techo (Mastología): control post oncológico
INSERT INTO consultas (fk_paciente, fk_medico_esp, fecha_hora, motivo, observaciones)
SELECT p.id_paciente,
       (SELECT me.id_medico_esp FROM medico_especialidad me
          JOIN medicos m ON m.id_medico = me.fk_medico
          JOIN personas pm ON pm.id_persona = m.id_medico
          JOIN especialidades e ON e.id_especialidad = me.fk_especialidad
         WHERE pm.dni = 'T9003' AND e.nombre = 'Mastología'),
       '2026-07-01 10:30:00', 'Control mamario', 'Paciente asintomática.'
FROM pacientes p WHERE p.numero_hc = 'TST-007';
INSERT INTO consultas_ginecologia (id_consulta_gin, patologia_mamaria, examen_mamario_inspeccion,
    examen_mamario_palpacion, derrame_pezon, diagnostico, plan_tratamiento)
VALUES (LAST_INSERT_ID(), 1, 'Cicatriz quirúrgica estable', 'Sin nódulos palpables', 0,
        'Control posterior a tratamiento oncológico.', 'Mamografía anual.');

-- 6) TST-008 con Sofía Techo (Patología Cervical): LSIL
INSERT INTO consultas (fk_paciente, fk_medico_esp, fecha_hora, motivo, observaciones)
SELECT p.id_paciente,
       (SELECT me.id_medico_esp FROM medico_especialidad me
          JOIN medicos m ON m.id_medico = me.fk_medico
          JOIN personas pm ON pm.id_persona = m.id_medico
          JOIN especialidades e ON e.id_especialidad = me.fk_especialidad
         WHERE pm.dni = 'T9003' AND e.nombre = 'Patología Cervical'),
       '2026-10-01 15:00:00', 'Seguimiento de PAP', NULL
FROM pacientes p WHERE p.numero_hc = 'TST-008';
INSERT INTO consultas_ginecologia (id_consulta_gin, papanicolau_resultado, papanicolau_fecha, schiller,
    imagen_colposcopica, diagnostico, plan_tratamiento)
VALUES (LAST_INSERT_ID(), 'LSIL', '2026-10-01', 'Positivo',
        'Lesión acetoblanca en cuadrante anterior', 'Lesión intraepitelial de bajo grado (LSIL).',
        'Control con PAP y colposcopía en 6 meses.');
