CREATE DATABASE IF NOT EXISTS lalcec_db DEFAULT CHARACTER SET utf8mb4;
USE lalcec_db;

CREATE TABLE obras_sociales (
    id_obrasocial INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE personas (
    id_persona INT AUTO_INCREMENT PRIMARY KEY,
    dni VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE,
    sexo CHAR(1),
    direccion VARCHAR(200),
    telefono VARCHAR(50),
    INDEX idx_personas_dni (dni),
    INDEX idx_personas_apellido (apellido)
) ENGINE=InnoDB;

CREATE TABLE pacientes (
    id_paciente INT PRIMARY KEY,
    fk_obrasocial INT NULL,
    numero_afiliado VARCHAR(50),
    numero_hc VARCHAR(20),
    nacionalidad VARCHAR(50),
    religion VARCHAR(50),
    estado_civil VARCHAR(50),
    allegado_nombre VARCHAR(100),
    allegado_domicilio VARCHAR(200),
    allegado_telefono VARCHAR(50),
    antecedentes_clinicos TEXT,
    CONSTRAINT fk_pacientes_persona FOREIGN KEY (id_paciente) REFERENCES personas(id_persona) ON DELETE CASCADE,
    CONSTRAINT fk_pacientes_obrasocial FOREIGN KEY (fk_obrasocial) REFERENCES obras_sociales(id_obrasocial) ON DELETE SET NULL,
    INDEX idx_pacientes_hc (numero_hc)
) ENGINE=InnoDB;

CREATE TABLE medicos (
    id_medico INT PRIMARY KEY,
    matricula VARCHAR(50) NOT NULL,
    CONSTRAINT fk_medicos_persona FOREIGN KEY (id_medico) REFERENCES personas(id_persona) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE especialidades (
    id_especialidad INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE medico_especialidad (
    id_medico_esp INT AUTO_INCREMENT PRIMARY KEY,
    fk_medico INT NOT NULL,
    fk_especialidad INT NOT NULL,
    CONSTRAINT fk_med_esp_medico FOREIGN KEY (fk_medico) REFERENCES medicos(id_medico) ON DELETE CASCADE,
    CONSTRAINT fk_med_esp_especialidad FOREIGN KEY (fk_especialidad) REFERENCES especialidades(id_especialidad) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE antecedentes_ginecologicos (
    id_antecedente_gin INT AUTO_INCREMENT PRIMARY KEY,
    fk_paciente INT NOT NULL UNIQUE,
    menarca_edad INT,
    menopausia_edad INT,
    fecha_ultima_menstruacion DATE,
    inicio_relaciones_sexuales_edad INT,
    cantidad_embarazos INT DEFAULT 0,
    cantidad_partos INT DEFAULT 0,
    cantidad_abortos INT DEFAULT 0,
    uso_anticonceptivos TINYINT(1) DEFAULT 0,
    metodo_anticonceptivo VARCHAR(100),
    medicacion_hormonal TINYINT(1) DEFAULT 0,
    medicacion_hormonal_detalle VARCHAR(150),
    electrocoagulacion_cuello TINYINT(1) DEFAULT 0,
    electrocoagulacion_cuantas INT DEFAULT 0,
    ultima_coagulacion DATE,
    otras_coagulaciones VARCHAR(150),
    antecedentes_familiares TEXT,
    antecedentes_personales TEXT,
    CONSTRAINT fk_ant_gin_paciente FOREIGN KEY (fk_paciente) REFERENCES pacientes(id_paciente) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE consultas (
    id_consulta INT AUTO_INCREMENT PRIMARY KEY,
    fk_paciente INT NOT NULL,
    fk_medico_esp INT NOT NULL,
    fecha_hora DATETIME NOT NULL,
    motivo VARCHAR(200),
    observaciones TEXT,
    CONSTRAINT fk_consultas_paciente FOREIGN KEY (fk_paciente) REFERENCES pacientes(id_paciente) ON DELETE CASCADE,
    CONSTRAINT fk_consultas_medico_esp FOREIGN KEY (fk_medico_esp) REFERENCES medico_especialidad(id_medico_esp),
    INDEX idx_consultas_paciente (fk_paciente),
    INDEX idx_consultas_fecha (fecha_hora)
) ENGINE=InnoDB;

CREATE TABLE consultas_ginecologia (
    id_consulta_gin INT PRIMARY KEY,
    dolor_bajo_vientre TINYINT(1) DEFAULT 0,
    flujo TINYINT(1) DEFAULT 0,
    prurito TINYINT(1) DEFAULT 0,
    sinusorragia TINYINT(1) DEFAULT 0,
    algopaurenia TINYINT(1) DEFAULT 0,
    metrorragia TINYINT(1) DEFAULT 0,
    prolapso TINYINT(1) DEFAULT 0,
    amenorrea TINYINT(1) DEFAULT 0,
    tension_premenstrual TINYINT(1) DEFAULT 0,
    incontinencia_orina TINYINT(1) DEFAULT 0,
    lumbalgia TINYINT(1) DEFAULT 0,
    esteril TINYINT(1) DEFAULT 0,
    patologia_mamaria TINYINT(1) DEFAULT 0,
    alteracion_ciclo TINYINT(1) DEFAULT 0,
    tumor_abdominal TINYINT(1) DEFAULT 0,
    tumor_mamario TINYINT(1) DEFAULT 0,
    papanicolau_resultado VARCHAR(20),
    papanicolau_fecha DATE,
    examen_flujo_bacterial VARCHAR(50),
    trichomonas TINYINT(1) DEFAULT 0,
    monilias TINYINT(1) DEFAULT 0,
    diagnostico TEXT,
    plan_tratamiento TEXT,
    imagen_colposcopica TEXT,
    schiller VARCHAR(100),
    examen_mamario_inspeccion TEXT,
    examen_mamario_palpacion TEXT,
    derrame_pezon TINYINT(1) DEFAULT 0,
    adenopatias TEXT,
    CONSTRAINT fk_gin_consulta FOREIGN KEY (id_consulta_gin) REFERENCES consultas(id_consulta) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Datos de referencia
INSERT INTO especialidades (nombre) VALUES
  ('Clínica General'), ('Ginecología'), ('Patología Cervical'), ('Mastología');
