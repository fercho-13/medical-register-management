package com.lalcec.view;

import com.lalcec.controller.Async;
import com.lalcec.dao.PacienteDAO;
import com.lalcec.model.Paciente;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {
    private final PacienteDAO dao = new PacienteDAO();
    private final PacientesTableModel tableModel = new PacientesTableModel();
    private final JTable tabla = new JTable(tableModel);
    private final JTextField busqueda = new JTextField(24);

    public MainFrame() {
        super("LALCEC — Gestión clínica");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JButton buscar = new JButton("Buscar");
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("DNI, apellido o N° HC:"));
        top.add(busqueda); top.add(buscar);
        buscar.addActionListener(e -> buscar());
        busqueda.addActionListener(e -> buscar());

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        tabla.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { if (e.getClickCount() == 2) abrirHistoria(); }
        });

        JButton historia = new JButton("Ver historia"), nuevo = new JButton("Nuevo paciente"),
                editar = new JButton("Editar"), eliminar = new JButton("Eliminar");
        historia.addActionListener(e -> abrirHistoria());
        nuevo.addActionListener(e -> { PacienteDialog d = new PacienteDialog(this, null); d.setVisible(true); if (d.isGuardado()) buscar(); });
        editar.addActionListener(e -> editar());
        eliminar.addActionListener(e -> eliminar());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(historia); bottom.add(nuevo); bottom.add(editar); bottom.add(eliminar);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        setSize(820, 480);
        setLocationRelativeTo(null);
        buscar();
    }

    private void buscar() {
        Async.run(this, () -> dao.buscar(busqueda.getText()), tableModel::setData);
    }

    private Paciente seleccionado() {
        int row = tabla.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Seleccione un paciente."); return null; }
        return tableModel.get(tabla.convertRowIndexToModel(row));
    }

    private void abrirHistoria() {
        Paciente p = seleccionado();
        if (p != null) new HistoriaDialog(this, p).setVisible(true);
    }

    private void editar() {
        Paciente p = seleccionado();
        if (p == null) return;
        PacienteDialog d = new PacienteDialog(this, p);
        d.setVisible(true);
        if (d.isGuardado()) buscar();
    }

    private void eliminar() {
        Paciente p = seleccionado();
        if (p == null) return;
        int r = JOptionPane.showConfirmDialog(this,
                "¿Eliminar a " + p.getNombreCompleto() + " y TODAS sus consultas? No se puede deshacer.",
                "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r == JOptionPane.YES_OPTION) {
            Async.run(this, () -> { dao.eliminar(p.getId()); return Boolean.TRUE; }, ok -> buscar());
        }
    }

    private static class PacientesTableModel extends AbstractTableModel {
        private static final String[] COLS = {"HC", "DNI", "Apellido", "Nombre", "Teléfono"};
        private List<Paciente> data = new ArrayList<>();

        void setData(List<Paciente> d) { data = d; fireTableDataChanged(); }
        Paciente get(int i) { return data.get(i); }
        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return COLS.length; }
        @Override public String getColumnName(int c) { return COLS[c]; }
        @Override public Object getValueAt(int r, int c) {
            Paciente p = data.get(r);
            switch (c) {
                case 0: return p.getNumeroHc();
                case 1: return p.getDni();
                case 2: return p.getApellido();
                case 3: return p.getNombre();
                default: return p.getTelefono();
            }
        }
    }
}
