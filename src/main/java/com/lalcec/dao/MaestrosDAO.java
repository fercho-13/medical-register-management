package com.lalcec.dao;

import com.lalcec.model.MedicoEspecialidad;
import com.lalcec.model.ObraSocial;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Listas de referencia: obras sociales y combinaciones médico/especialidad. */
public class MaestrosDAO {
    public List<ObraSocial> listarObrasSociales() throws SQLException {
        List<ObraSocial> out = new ArrayList<>();
        Connection c = ConexionDB.getInstance().getConnection();
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT id_obrasocial, nombre FROM obras_sociales ORDER BY nombre")) {
            while (rs.next()) out.add(new ObraSocial(rs.getInt(1), rs.getString(2)));
        }
        return out;
    }

    public List<MedicoEspecialidad> listarMedicoEspecialidades() throws SQLException {
        List<MedicoEspecialidad> out = new ArrayList<>();
        String sql = "SELECT me.id_medico_esp, p.apellido, p.nombre, e.nombre "
                + "FROM medico_especialidad me "
                + "JOIN medicos m ON m.id_medico = me.fk_medico "
                + "JOIN personas p ON p.id_persona = m.id_medico "
                + "JOIN especialidades e ON e.id_especialidad = me.fk_especialidad "
                + "ORDER BY p.apellido, p.nombre, e.nombre";
        Connection c = ConexionDB.getInstance().getConnection();
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                out.add(new MedicoEspecialidad(rs.getInt(1), rs.getString(2) + ", " + rs.getString(3), rs.getString(4)));
            }
        }
        return out;
    }
}
