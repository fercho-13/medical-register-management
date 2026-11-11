# medical-register-management

Sistema de gestión clínica de escritorio para el consultorio de LALCEC (Olavarría).
Java 8 + Swing + MySQL 5.7 (JDBC, Connector/J 5.1.49), arquitectura cliente-servidor de 2 capas en LAN, patrón MVC + DAO.

## Estructura
- `src/main/resources/schema.sql` – DDL de MySQL 5.7 y especialidades iniciales.
- `src/main/resources/seed_demo.sql` – datos de prueba (obras sociales y un médico), solo desarrollo.
- `com.lalcec.model` – POJOs (`Persona`, `Paciente`, `Consulta`, `ConsultaGinecologia`, ...).
- `com.lalcec.dao` – `ConexionDB` (singleton), `PacienteDAO`, `ConsultaDAO` (transacción manual con clave primaria compartida), `MaestrosDAO`.
- `com.lalcec.controller.Async` – ejecuta las consultas a la BD en `SwingWorker`.
- `com.lalcec.view` – ventanas Swing: búsqueda, alta/edición de pacientes, historia clínica y nueva consulta.

## Puesta en marcha
1. En el servidor: ejecutar `schema.sql` (y opcionalmente `seed_demo.sql`) y crear un usuario de BD para la app.
2. Copiar `db.properties.example` a `db.properties` junto al jar (o definir `DB_URL`, `DB_USER`, `DB_PASSWORD`).
3. Compilar y ejecutar:

```bash
mvn package
java -jar target/myapp.jar
```

## Pendiente (según el documento del proyecto)
Gestión de turnos (Fase 2), carga de médicos/obras sociales desde la UI, y edición de `antecedentes_ginecologicos`.
