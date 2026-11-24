package com.lalcec.view;

import com.lalcec.controller.Async;
import com.lalcec.dao.ConsultaDAO;
import com.lalcec.dao.MaestrosDAO;
import com.lalcec.model.*;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Registro de consulta: texto libre para especialidades generales, formulario detallado para ginecología. */
public class ConsultaDialog extends JDialog {
    private final Paciente paciente;
    private boolean guardado;

    private final JComboBox<MedicoEspecialidad> medico = new JComboBox<>();
    private final JTextField motivo = new JTextField(30);
    private final JTextArea observaciones = new JTextArea(5, 40);

    private final JPanel panelGin = new JPanel(new BorderLayout());
    private final Map<String, JCheckBox> checks = new LinkedHashMap<>();
    private final JTextField papResultado = new JTextField(12), papFecha = new JTextField(10),
            flujoBact = new JTextField(14), schiller = new JTextField(14);
    private final JTextArea diagnostico = area(), plan = area(), colpo = area(), mamaInsp = area(),
            mamaPalp = area(), adenopatias = area();

    public ConsultaDialog(Frame owner, Paciente paciente) {
        super(owner, "Nueva consulta — " + paciente.getNombreCompleto(), true);
        this.paciente = paciente;

        JPanel top = new JPanel(new GridBagLayout());
        top.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(3, 3, 3, 3); g.anchor = GridBagConstraints.WEST;
        g.gridy = 0; g.gridx = 0; top.add(new JLabel("Médico / especialidad"), g);
        g.gridx = 1; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1; top.add(medico, g);
        g.gridy = 1; g.gridx = 0; g.fill = GridBagConstraints.NONE; g.weightx = 0; top.add(new JLabel("Motivo"), g);
        g.gridx = 1; g.fill = GridBagConstraints.HORIZONTAL; top.add(motivo, g);
        g.gridy = 2; g.gridx = 0; g.fill = GridBagConstraints.NONE; g.anchor = GridBagConstraints.NORTHWEST;
        top.add(new JLabel("Observaciones"), g);
        g.gridx = 1; g.fill = GridBagConstraints.BOTH; top.add(new JScrollPane(observaciones), g);

        panelGin.add(construirPanelGin(), BorderLayout.CENTER);
        panelGin.setVisible(false);

        JButton ok = new JButton("Guardar"), cancel = new JButton("Cancelar");
        ok.addActionListener(e -> guardar());
        cancel.addActionListener(e -> dispose());
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(cancel); btns.add(ok);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(panelGin), BorderLayout.CENTER);
        add(btns, BorderLayout.SOUTH);
        medico.addActionListener(e -> actualizarModo());

        setSize(760, 480);
        setLocationRelativeTo(owner);

        Async.run(this, () -> new MaestrosDAO().listarMedicoEspecialidades(), (List<MedicoEspecialidad> l) -> {
            if (l.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay médicos con especialidad cargados en la base de datos.");
            }
            for (MedicoEspecialidad me : l) medico.addItem(me);
        });
    }

    private static JTextArea area() {
        JTextArea a = new JTextArea(2, 24);
        a.setLineWrap(true); a.setWrapStyleWord(true);
        return a;
    }

    private JPanel construirPanelGin() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createTitledBorder("Detalle ginecológico"));

        JPanel sint = new JPanel(new GridLayout(0, 3));
        sint.setBorder(BorderFactory.createTitledBorder("Síntomas, hallazgos y estudios"));
        for (Map.Entry<String, String> e : ConsultaGinecologia.FLAGS.entrySet()) {
            JCheckBox cb = new JCheckBox(e.getValue());
            checks.put(e.getKey(), cb);
            sint.add(cb);
        }
        p.add(sint);

        JPanel est = new JPanel(new FlowLayout(FlowLayout.LEFT));
        est.add(new JLabel("PAP resultado")); est.add(papResultado);
        est.add(new JLabel("PAP fecha")); est.add(papFecha);
        est.add(new JLabel("Flujo bacterial")); est.add(flujoBact);
        est.add(new JLabel("Schiller")); est.add(schiller);
        p.add(est);

        p.add(labeled("Imagen colposcópica", colpo));
        p.add(labeled("Examen mamario: inspección", mamaInsp));
        p.add(labeled("Examen mamario: palpación", mamaPalp));
        p.add(labeled("Adenopatías", adenopatias));
        p.add(labeled("Diagnóstico", diagnostico));
        p.add(labeled("Plan de tratamiento", plan));
        return p;
    }

    private static JPanel labeled(String l, JTextArea a) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel(l), BorderLayout.NORTH);
        p.add(new JScrollPane(a), BorderLayout.CENTER);
        return p;
    }

    private void actualizarModo() {
        MedicoEspecialidad me = (MedicoEspecialidad) medico.getSelectedItem();
        boolean gin = me != null && me.usaFormularioGinecologico();
        panelGin.setVisible(gin);
        setSize(760, gin ? 780 : 480);
        revalidate();
    }

    private void guardar() {
        MedicoEspecialidad me = (MedicoEspecialidad) medico.getSelectedItem();
        if (me == null) { JOptionPane.showMessageDialog(this, "Seleccione médico/especialidad."); return; }

        Consulta c = new Consulta();
        c.setPacienteId(paciente.getId());
        c.setMedicoEspId(me.getId());
        c.setFechaHora(LocalDateTime.now());
        c.setMotivo(Fechas.nullIfEmpty(motivo.getText()));
        c.setObservaciones(Fechas.nullIfEmpty(observaciones.getText()));

        if (me.usaFormularioGinecologico()) {
            ConsultaGinecologia g = new ConsultaGinecologia();
            try {
                g.setPapanicolauFecha(Fechas.parse(papFecha.getText(), "PAP fecha"));
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
                return;
            }
            for (Map.Entry<String, JCheckBox> e : checks.entrySet()) g.setFlag(e.getKey(), e.getValue().isSelected());
            g.setPapanicolauResultado(Fechas.nullIfEmpty(papResultado.getText()));
            g.setExamenFlujoBacterial(Fechas.nullIfEmpty(flujoBact.getText()));
            g.setSchiller(Fechas.nullIfEmpty(schiller.getText()));
            g.setImagenColposcopica(Fechas.nullIfEmpty(colpo.getText()));
            g.setExamenMamarioInspeccion(Fechas.nullIfEmpty(mamaInsp.getText()));
            g.setExamenMamarioPalpacion(Fechas.nullIfEmpty(mamaPalp.getText()));
            g.setAdenopatias(Fechas.nullIfEmpty(adenopatias.getText()));
            g.setDiagnostico(Fechas.nullIfEmpty(diagnostico.getText()));
            g.setPlanTratamiento(Fechas.nullIfEmpty(plan.getText()));
            c.setGinecologia(g);
        }
        Async.run(this, () -> { new ConsultaDAO().insertar(c); return Boolean.TRUE; },
                ok -> { guardado = true; dispose(); });
    }

    public boolean isGuardado() { return guardado; }
}
