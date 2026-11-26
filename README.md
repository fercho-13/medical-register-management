# medical-register-management

Sistema de gestión clínica de escritorio para el consultorio de LALCEC (Olavarría).
Java 8 + Swing + MySQL 5.7 (JDBC, Connector/J 5.1.49), arquitectura cliente-servidor de 2 capas en LAN, patrón MVC + DAO.

## Estructura
- `sql/schema.sql` – DDL de MySQL 5.7 y especialidades iniciales.
- `sql/vaciar_base_de_datos.sql` – borra TODOS los datos de todas las tablas y reinicia los contadores (solo desarrollo).
- `sql/datos_equipo_medico.sql` – carga el equipo médico de LALCEC (según la imagen oficial) y pacientes ficticios de prueba.
- `com.lalcec.model` – POJOs (`Persona`, `Paciente`, `Consulta`, `ConsultaGinecologia`, ...).
- `com.lalcec.dao` – `ConexionDB` (singleton), `PacienteDAO`, `ConsultaDAO` (transacción manual con clave primaria compartida), `MaestrosDAO`.
- `com.lalcec.controller.Async` – ejecuta las consultas a la BD en `SwingWorker`.
- `com.lalcec.view` – ventanas Swing: búsqueda, alta/edición de pacientes, historia clínica y nueva consulta.

## Puesta en marcha
1. En el servidor: ejecutar `sql/schema.sql` (y, para pruebas, `sql/datos_equipo_medico.sql`) y crear un usuario de BD para la app.
   Los DNI del equipo médico son provisorios (`PEND-<matrícula>`): reemplazarlos por los reales (ver el encabezado del script).
2. Copiar `db.properties.example` a `db.properties` junto al jar (o definir `DB_URL`, `DB_USER`, `DB_PASSWORD`).
3. Compilar y ejecutar:

```bash
mvn package
java -jar target/myapp.jar
```

## Pendiente (según el documento del proyecto)
Gestión de turnos (Fase 2), carga de médicos/obras sociales desde la UI, y edición de `antecedentes_ginecologicos`.
