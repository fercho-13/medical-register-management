package com.lalcec.dao;

import com.lalcec.model.Consulta;
import com.lalcec.model.ConsultaGinecologia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultaDAO {

    /**
     * Inserta una consulta. Si trae detalle ginecológico, lo hace en una transacción manual:
     * el id generado en `consultas` se reutiliza como PK/FK de `consultas_ginecologia`.
     */
    public void insertar(Consulta cons) throws SQLException {
        Connection c = ConexionDB.getInstance().getConnection();
        boolean prev = c.getAutoCommit();
        c.setAutoCommit(false);                                     // 1
        try {
            try (PreparedStatement ps = c.prepareStatement(          // 2
                    "INSERT INTO consultas (fk_paciente, fk_medico_esp, fecha_hora, motivo, observaciones) VALUES (?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, cons.getPacienteId());
                ps.setInt(2, cons.getMedicoEspId());
                ps.setTimestamp(3, Timestamp.valueOf(cons.getFechaHora()));
                ps.setString(4, cons.getMotivo());
                ps.setString(5, cons.getObservaciones());
                ps.executeUpdate();
                try (ResultSet k = ps.getGeneratedKeys()) {
                    if (!k.next()) throw new SQLException("No se generó id_consulta");
                    cons.setId(k.getInt(1));
                }
            }
            if (cons.getGinecologia() != null) {                     // 3
                insertarGinecologia(c, cons.getId(), cons.getGinecologia());
            }
            c.commit();
        } catch (SQLException | RuntimeException e) {
            c.rollback();
            throw e;
        } finally {
            c.setAutoCommit(prev);
        }
    }

    private void insertarGinecologia(Connection c, int id, ConsultaGinecologia g) throws SQLException {
        StringBuilder cols = new StringBuilder("id_consulta_gin");
        StringBuilder marks = new StringBuilder("?");
        for (String col : ConsultaGinecologia.FLAGS.keySet()) { cols.append(',').append(col); marks.append(",?"); }
        String[] texts = {"papanicolau_resultado", "papanicolau_fecha", "examen_flujo_bacterial", "diagnostico",
                "plan_tratamiento", "imagen_colposcopica", "schiller", "examen_mamario_inspeccion",
                "examen_mamario_palpacion", "adenopatias"};
        for (String col : texts) { cols.append(',').append(col); marks.append(",?"); }

        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO consultas_ginecologia (" + cols + ") VALUES (" + marks + ")")) {
            int i = 1;
            ps.setInt(i++, id);
            for (String col : ConsultaGinecologia.FLAGS.keySet()) ps.setBoolean(i++, g.isFlag(col));
            ps.setString(i++, g.getPapanicolauResultado());
            ps.setDate(i++, g.getPapanicolauFecha() == null ? null : Date.valueOf(g.getPapanicolauFecha()));
            ps.setString(i++, g.getExamenFlujoBacterial());
            ps.setString(i++, g.getDiagnostico());
            ps.setString(i++, g.getPlanTratamiento());
            ps.setString(i++, g.getImagenColposcopica());
            ps.setString(i++, g.getSchiller());
            ps.setString(i++, g.getExamenMamarioInspeccion());
            ps.setString(i++, g.getExamenMamarioPalpacion());
            ps.setString(i, g.getAdenopatias());
            ps.executeUpdate();
        }
    }

    /** Historial del paciente, más reciente primero. Incluye el detalle ginecológico si existe. */
    public List<Consulta> listarPorPaciente(int pacienteId) throws SQLException {
        String sql = "SELECT c.id_consulta, c.fk_medico_esp, c.fecha_hora, c.motivo, c.observaciones, "
                + "CONCAT(pm.apellido, ', ', pm.nombre, ' — ', e.nombre) AS medico_esp, g.* "
                + "FROM consultas c "
                + "JOIN medico_especialidad me ON me.id_medico_esp = c.fk_medico_esp "
                + "JOIN personas pm ON pm.id_persona = me.fk_medico "
                + "JOIN especialidades e ON e.id_especialidad = me.fk_especialidad "
                + "LEFT JOIN consultas_ginecologia g ON g.id_consulta_gin = c.id_consulta "
                + "WHERE c.fk_paciente = ? ORDER BY c.fecha_hora DESC";
        Connection conn = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pacienteId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Consulta> out = new ArrayList<>();
                while (rs.next()) {
                    Consulta c = new Consulta();
                    c.setId(rs.getInt("id_consulta"));
                    c.setPacienteId(pacienteId);
                    c.setMedicoEspId(rs.getInt("fk_medico_esp"));
                    c.setMedicoEspecialidad(rs.getString("medico_esp"));
                    c.setFechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime());
                    c.setMotivo(rs.getString("motivo"));
                    c.setObservaciones(rs.getString("observaciones"));
                    rs.getInt("id_consulta_gin");
                    if (!rs.wasNull()) c.setGinecologia(mapGin(rs));
                    out.add(c);
                }
                return out;
            }
        }
    }

    private static ConsultaGinecologia mapGin(ResultSet rs) throws SQLException {
        ConsultaGinecologia g = new ConsultaGinecologia();
        for (String col : ConsultaGinecologia.FLAGS.keySet()) g.setFlag(col, rs.getBoolean(col));
        g.setPapanicolauResultado(rs.getString("papanicolau_resultado"));
        Date d = rs.getDate("papanicolau_fecha");
        g.setPapanicolauFecha(d == null ? null : d.toLocalDate());
        g.setExamenFlujoBacterial(rs.getString("examen_flujo_bacterial"));
        g.setDiagnostico(rs.getString("diagnostico"));
        g.setPlanTratamiento(rs.getString("plan_tratamiento"));
        g.setImagenColposcopica(rs.getString("imagen_colposcopica"));
        g.setSchiller(rs.getString("schiller"));
        g.setExamenMamarioInspeccion(rs.getString("examen_mamario_inspeccion"));
        g.setExamenMamarioPalpacion(rs.getString("examen_mamario_palpacion"));
        g.setAdenopatias(rs.getString("adenopatias"));
        return g;
    }
}
