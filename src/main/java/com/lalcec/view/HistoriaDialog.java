package com.lalcec.view;

import com.lalcec.controller.Async;
import com.lalcec.dao.ConsultaDAO;
import com.lalcec.model.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/** Visor de historia clínica: antecedentes del paciente + listado histórico de consultas. */
public class HistoriaDialog extends JDialog {
    private final Frame owner;
    private final Paciente paciente;
    private final DefaultListModel<Consulta> model = new DefaultListModel<>();
    private final JList<Consulta> lista = new JList<>(model);
    private final JTextArea detalle = new JTextArea();

    public HistoriaDialog(Frame owner, Paciente p) {
        super(owner, "Historia clínica — " + p.getNombreCompleto(), true);
        this.owner = owner;
        this.paciente = p;

        JTextArea cabecera = new JTextArea(
                "DNI: " + p.getDni() + "    HC: " + nz(p.getNumeroHc()) + "    Nacimiento: " + Fechas.fmt(p.getFechaNacimiento())
                + "\nTeléfono: " + nz(p.getTelefono()) + "    Dirección: " + nz(p.getDireccion())
                + "\nAntecedentes clínicos: " + nz(p.getAntecedentesClinicos()));
        cabecera.setEditable(false); cabecera.setLineWrap(true); cabecera.setWrapStyleWord(true);
        cabecera.setBackground(getBackground());
        cabecera.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        lista.setCellRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                Consulta c = (Consulta) v;
                String t = c.getFechaHora().format(Fechas.FMT_HORA) + (c.getGinecologia() != null ? "  [Gin]" : "");
                return super.getListCellRendererComponent(l, t, i, s, f);
            }
        });
        lista.addListSelectionListener(e -> mostrar(lista.getSelectedValue()));
        detalle.setEditable(false); detalle.setLineWrap(true); detalle.setWrapStyleWord(true);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(lista), new JScrollPane(detalle));
        split.setDividerLocation(200);

        JButton nueva = new JButton("Nueva consulta"), cerrar = new JButton("Cerrar");
        nueva.addActionListener(e -> {
            ConsultaDialog d = new ConsultaDialog(owner, paciente);
            d.setVisible(true);
            if (d.isGuardado()) cargar();
        });
        cerrar.addActionListener(e -> dispose());
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(nueva); btns.add(cerrar);

        add(cabecera, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
        add(btns, BorderLayout.SOUTH);
        setSize(800, 520);
        setLocationRelativeTo(owner);
        cargar();
    }

    private void cargar() {
        Async.run(this, () -> new ConsultaDAO().listarPorPaciente(paciente.getId()), (List<Consulta> l) -> {
            model.clear();
            for (Consulta c : l) model.addElement(c);
            if (!l.isEmpty()) lista.setSelectedIndex(0); else detalle.setText("Sin consultas registradas.");
        });
    }

    private static String nz(String s) { return s == null ? "-" : s; }

    private void mostrar(Consulta c) {
        if (c == null) return;
        StringBuilder sb = new StringBuilder();
        sb.append(c.getFechaHora().format(Fechas.FMT_HORA)).append("  ").append(c.getMedicoEspecialidad()).append('\n');
        sb.append("Motivo: ").append(nz(c.getMotivo())).append("\n\nObservaciones:\n").append(nz(c.getObservaciones())).append('\n');
        ConsultaGinecologia g = c.getGinecologia();
        if (g != null) {
            sb.append("\n--- Ginecología ---\nPresentes: ");
            StringBuilder pres = new StringBuilder();
            for (Map.Entry<String, String> e : ConsultaGinecologia.FLAGS.entrySet()) {
                if (g.isFlag(e.getKey())) pres.append(pres.length() > 0 ? ", " : "").append(e.getValue());
            }
            sb.append(pres.length() > 0 ? pres : "ninguno").append('\n');
            sb.append("PAP: ").append(nz(g.getPapanicolauResultado())).append(" (").append(Fechas.fmt(g.getPapanicolauFecha())).append(")\n");
            sb.append("Flujo bacterial: ").append(nz(g.getExamenFlujoBacterial())).append("   Schiller: ").append(nz(g.getSchiller())).append('\n');
            sb.append("Imagen colposcópica: ").append(nz(g.getImagenColposcopica())).append('\n');
            sb.append("Mama inspección: ").append(nz(g.getExamenMamarioInspeccion())).append('\n');
            sb.append("Mama palpación: ").append(nz(g.getExamenMamarioPalpacion())).append('\n');
            sb.append("Adenopatías: ").append(nz(g.getAdenopatias())).append('\n');
            sb.append("Diagnóstico: ").append(nz(g.getDiagnostico())).append('\n');
            sb.append("Plan: ").append(nz(g.getPlanTratamiento())).append('\n');
        }
        detalle.setText(sb.toString());
        detalle.setCaretPosition(0);
    }
}
