package com.lalcec.dao;

import com.lalcec.model.Paciente;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAO {
    private static final String SELECT =
            "SELECT p.id_persona, p.dni, p.nombre, p.apellido, p.fecha_nacimiento, p.sexo, p.direccion, p.telefono, "
            + "pa.fk_obrasocial, pa.numero_afiliado, pa.numero_hc, pa.nacionalidad, pa.religion, pa.estado_civil, "
            + "pa.allegado_nombre, pa.allegado_domicilio, pa.allegado_telefono, pa.antecedentes_clinicos "
            + "FROM personas p JOIN pacientes pa ON pa.id_paciente = p.id_persona ";

    /** Busca por DNI (prefijo), apellido (prefijo) o N° de HC exacto. Texto vacío lista los primeros 200. */
    public List<Paciente> buscar(String texto) throws SQLException {
        String t = texto == null ? "" : texto.trim();
        String sql = SELECT + (t.isEmpty() ? "" : "WHERE p.dni LIKE ? OR p.apellido LIKE ? OR pa.numero_hc = ? ")
                + "ORDER BY p.apellido, p.nombre LIMIT 200";
        Connection c = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            if (!t.isEmpty()) {
                String like = escapeLike(t) + "%"; // prefijo: usa los índices
                ps.setString(1, like);
                ps.setString(2, like);
                ps.setString(3, t);
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<Paciente> out = new ArrayList<>();
                while (rs.next()) out.add(map(rs));
                return out;
            }
        }
    }

    public Paciente buscarPorId(int id) throws SQLException {
        Connection c = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = c.prepareStatement(SELECT + "WHERE p.id_persona = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    /** Inserta en personas y pacientes en una sola transacción. */
    public void insertar(Paciente p) throws SQLException {
        Connection c = ConexionDB.getInstance().getConnection();
        boolean prev = c.getAutoCommit();
        c.setAutoCommit(false);
        try {
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO personas (dni, nombre, apellido, fecha_nacimiento, sexo, direccion, telefono) "
                    + "VALUES (?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
                bindPersona(ps, p);
                ps.executeUpdate();
                try (ResultSet k = ps.getGeneratedKeys()) {
                    if (!k.next()) throw new SQLException("No se generó id_persona");
                    p.setId(k.getInt(1));
                }
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO pacientes (fk_obrasocial, numero_afiliado, numero_hc, nacionalidad, religion, estado_civil, "
                    + "allegado_nombre, allegado_domicilio, allegado_telefono, antecedentes_clinicos, id_paciente) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?)")) {
                bindPaciente(ps, p);
                ps.setInt(11, p.getId());
                ps.executeUpdate();
            }
            c.commit();
        } catch (SQLException | RuntimeException e) {
            c.rollback();
            throw e;
        } finally {
            c.setAutoCommit(prev);
        }
    }

    public void actualizar(Paciente p) throws SQLException {
        Connection c = ConexionDB.getInstance().getConnection();
        boolean prev = c.getAutoCommit();
        c.setAutoCommit(false);
        try {
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE personas SET dni=?, nombre=?, apellido=?, fecha_nacimiento=?, sexo=?, direccion=?, telefono=? "
                    + "WHERE id_persona=?")) {
                bindPersona(ps, p);
                ps.setInt(8, p.getId());
                ps.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE pacientes SET fk_obrasocial=?, numero_afiliado=?, numero_hc=?, nacionalidad=?, religion=?, "
                    + "estado_civil=?, allegado_nombre=?, allegado_domicilio=?, allegado_telefono=?, antecedentes_clinicos=? "
                    + "WHERE id_paciente=?")) {
                bindPaciente(ps, p);
                ps.setInt(11, p.getId());
                ps.executeUpdate();
            }
            c.commit();
        } catch (SQLException | RuntimeException e) {
            c.rollback();
            throw e;
        } finally {
            c.setAutoCommit(prev);
        }
    }

    /** Borra la persona; ON DELETE CASCADE elimina paciente, antecedentes y consultas. */
    public void eliminar(int id) throws SQLException {
        Connection c = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM personas WHERE id_persona=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private static String escapeLike(String s) {
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    private static void bindPersona(PreparedStatement ps, Paciente p) throws SQLException {
        ps.setString(1, p.getDni());
        ps.setString(2, p.getNombre());
        ps.setString(3, p.getApellido());
        ps.setDate(4, p.getFechaNacimiento() == null ? null : Date.valueOf(p.getFechaNacimiento()));
        ps.setString(5, p.getSexo());
        ps.setString(6, p.getDireccion());
        ps.setString(7, p.getTelefono());
    }

    private static void bindPaciente(PreparedStatement ps, Paciente p) throws SQLException {
        if (p.getObraSocialId() == null) ps.setNull(1, Types.INTEGER); else ps.setInt(1, p.getObraSocialId());
        ps.setString(2, p.getNumeroAfiliado());
        ps.setString(3, p.getNumeroHc());
        ps.setString(4, p.getNacionalidad());
        ps.setString(5, p.getReligion());
        ps.setString(6, p.getEstadoCivil());
        ps.setString(7, p.getAllegadoNombre());
        ps.setString(8, p.getAllegadoDomicilio());
        ps.setString(9, p.getAllegadoTelefono());
        ps.setString(10, p.getAntecedentesClinicos());
    }

    private static Paciente map(ResultSet rs) throws SQLException {
        Paciente p = new Paciente();
        p.setId(rs.getInt("id_persona"));
        p.setDni(rs.getString("dni"));
        p.setNombre(rs.getString("nombre"));
        p.setApellido(rs.getString("apellido"));
        Date d = rs.getDate("fecha_nacimiento");
        p.setFechaNacimiento(d == null ? null : d.toLocalDate());
        p.setSexo(rs.getString("sexo"));
        p.setDireccion(rs.getString("direccion"));
        p.setTelefono(rs.getString("telefono"));
        int os = rs.getInt("fk_obrasocial");
        p.setObraSocialId(rs.wasNull() ? null : os);
        p.setNumeroAfiliado(rs.getString("numero_afiliado"));
        p.setNumeroHc(rs.getString("numero_hc"));
        p.setNacionalidad(rs.getString("nacionalidad"));
        p.setReligion(rs.getString("religion"));
        p.setEstadoCivil(rs.getString("estado_civil"));
        p.setAllegadoNombre(rs.getString("allegado_nombre"));
        p.setAllegadoDomicilio(rs.getString("allegado_domicilio"));
        p.setAllegadoTelefono(rs.getString("allegado_telefono"));
        p.setAntecedentesClinicos(rs.getString("antecedentes_clinicos"));
        return p;
    }
}
