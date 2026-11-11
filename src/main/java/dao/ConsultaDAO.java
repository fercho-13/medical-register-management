package dao;

import com.lalcec.model.ConsultaGinecologia;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConsultaDAO {

    public boolean insertarConsultaGinecologica(ConsultaGinecologia consulta) {
        String sqlGeneral = "INSERT INTO consultas (fk_paciente, fk_medico_esp, fecha_hora, motivo, observaciones) VALUES (?, ?, ?, ?, ?)";
        String sqlGine = "INSERT INTO consultas_ginecologia (id_consulta_gin, dolor_bajo_vientre, flujo, papanicolau_resultado, diagnostico) VALUES (?, ?, ?, ?, ?)";
        
        Connection conn = ConexionDB.getConnection();
        PreparedStatement psGeneral = null;
        PreparedStatement psGine = null;
        ResultSet rs = null;

        try {
            // Desactivar autocommit para manejar la transacción manualmente
            conn.setAutoCommit(false);

            // 1. Insertar en tabla madre 'consultas' y recuperar el ID generado
            psGeneral = conn.prepareStatement(sqlGeneral, Statement.RETURN_GENERATED_KEYS);
            psGeneral.setInt(1, consulta.getFkPaciente());
            psGeneral.setInt(2, consulta.getFkMedicoEsp());
            psGeneral.setTimestamp(3, new java.sql.Timestamp(consulta.getFechaHora().getTime()));
            psGeneral.setString(4, consulta.getMotivo());
            psGeneral.setString(5, consulta.getObservaciones());
            
            int rowsGeneral = psGeneral.executeUpdate();
            if (rowsGeneral == 0) {
                throw new SQLException("Fallo la creación de la consulta general.");
            }

            // Obtener el ID generado por MySQL
            int idConsultaGenerada = 0;
            rs = psGeneral.getGeneratedKeys();
            if (rs.next()) {
                idConsultaGenerada = rs.getInt(1);
            } else {
                throw new SQLException("Fallo al obtener el ID de la consulta general.");
            }

            // 2. Insertar en tabla hija 'consultas_ginecologia' usando el ID recuperado
            psGine = conn.prepareStatement(sqlGine);
            psGine.setInt(1, idConsultaGenerada);
            psGine.setBoolean(2, consulta.isDolorBajoVientre());
            psGine.setBoolean(3, consulta.isFlujo());
            psGine.setString(4, consulta.getPapanicolauResultado());
            psGine.setString(5, consulta.getDiagnostico());
            
            psGine.executeUpdate();

            // Si todo salió bien, confirmar la transacción
            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error en transacción, haciendo rollback: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            // Restaurar autocommit y cerrar recursos
            try {
                if (rs != null) rs.close();
                if (psGeneral != null) psGeneral.close();
                if (psGine != null) psGine.close();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}

