package com.lalcec.view;

import com.lalcec.controller.Async;
import com.lalcec.dao.MaestrosDAO;
import com.lalcec.dao.PacienteDAO;
import com.lalcec.model.ObraSocial;
import com.lalcec.model.Paciente;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/** Alta / edición de paciente (datos de filiación + antecedentes clínicos). */
public class PacienteDialog extends JDialog {
    private final PacienteDAO dao = new PacienteDAO();
    private final Paciente paciente;
    private final boolean nuevo;
    private boolean guardado;

    private final JTextField dni = new JTextField(14), nombre = new JTextField(20), apellido = new JTextField(20),
            nacimiento = new JTextField(10), direccion = new JTextField(24), telefono = new JTextField(14),
            hc = new JTextField(10), afiliado = new JTextField(14), nacionalidad = new JTextField(14),
            religion = new JTextField(14), estadoCivil = new JTextField(14), allNombre = new JTextField(20),
            allDomicilio = new JTextField(24), allTelefono = new JTextField(14);
    private final JComboBox<String> sexo = new JComboBox<>(new String[]{"", "F", "M", "X"});
    private final JComboBox<Object> obraSocial = new JComboBox<>();
    private final JTextArea antecedentes = new JTextArea(4, 30);

    public PacienteDialog(Frame owner, Paciente existente) {
        super(owner, existente == null ? "Nuevo paciente" : "Editar paciente", true);
        nuevo = existente == null;
        paciente = nuevo ? new Paciente() : existente;

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        int r = 0;
        r = row(form, r, "DNI *", dni, "N° Historia Clínica", hc);
        r = row(form, r, "Apellido *", apellido, "Nombre *", nombre);
        r = row(form, r, "Nacimiento (dd/mm/aaaa)", nacimiento, "Sexo", sexo);
        r = row(form, r, "Dirección", direccion, "Teléfono", telefono);
        r = row(form, r, "Obra social", obraSocial, "N° afiliado", afiliado);
        r = row(form, r, "Nacionalidad", nacionalidad, "Religión", religion);
        r = row(form, r, "Estado civil", estadoCivil, null, null);
        r = row(form, r, "Allegado: nombre", allNombre, "Allegado: teléfono", allTelefono);
        r = row(form, r, "Allegado: domicilio", allDomicilio, null, null);
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = r; g.anchor = GridBagConstraints.NORTHWEST; g.insets = new Insets(4, 4, 4, 4);
        form.add(new JLabel("Antecedentes clínicos"), g);
        g.gridx = 1; g.gridwidth = 3; g.fill = GridBagConstraints.BOTH; g.weightx = 1;
        antecedentes.setLineWrap(true); antecedentes.setWrapStyleWord(true);
        form.add(new JScrollPane(antecedentes), g);

        JButton ok = new JButton("Guardar"), cancel = new JButton("Cancelar");
        ok.addActionListener(e -> guardar());
        cancel.addActionListener(e -> dispose());
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(cancel); btns.add(ok);

        add(form, BorderLayout.CENTER);
        add(btns, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(ok);
        pack();
        setLocationRelativeTo(owner);

        cargarObrasSociales();
        if (!nuevo) volcar();
    }

    private static int row(JPanel p, int r, String l1, JComponent c1, String l2, JComponent c2) {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4); g.anchor = GridBagConstraints.WEST; g.gridy = r;
        g.gridx = 0; p.add(new JLabel(l1), g);
        g.gridx = 1; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1; p.add(c1, g);
        if (l2 != null) {
            g.fill = GridBagConstraints.NONE; g.weightx = 0;
            g.gridx = 2; p.add(new JLabel(l2), g);
            g.gridx = 3; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1; p.add(c2, g);
        }
        return r + 1;
    }

    private void cargarObrasSociales() {
        obraSocial.addItem("(ninguna)");
        Async.run(this, () -> new MaestrosDAO().listarObrasSociales(), (List<ObraSocial> list) -> {
            for (ObraSocial o : list) {
                obraSocial.addItem(o);
                if (paciente.getObraSocialId() != null && paciente.getObraSocialId() == o.getId()) obraSocial.setSelectedItem(o);
            }
        });
    }

    private void volcar() {
        dni.setText(paciente.getDni()); nombre.setText(paciente.getNombre()); apellido.setText(paciente.getApellido());
        nacimiento.setText(Fechas.fmt(paciente.getFechaNacimiento()));
        sexo.setSelectedItem(paciente.getSexo() == null ? "" : paciente.getSexo());
        direccion.setText(paciente.getDireccion()); telefono.setText(paciente.getTelefono());
        hc.setText(paciente.getNumeroHc()); afiliado.setText(paciente.getNumeroAfiliado());
        nacionalidad.setText(paciente.getNacionalidad()); religion.setText(paciente.getReligion());
        estadoCivil.setText(paciente.getEstadoCivil());
        allNombre.setText(paciente.getAllegadoNombre()); allDomicilio.setText(paciente.getAllegadoDomicilio());
        allTelefono.setText(paciente.getAllegadoTelefono()); antecedentes.setText(paciente.getAntecedentesClinicos());
    }

    private void guardar() {
        if (dni.getText().trim().isEmpty() || nombre.getText().trim().isEmpty() || apellido.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "DNI, apellido y nombre son obligatorios.");
            return;
        }
        try {
            paciente.setFechaNacimiento(Fechas.parse(nacimiento.getText(), "Nacimiento"));
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            return;
        }
        paciente.setDni(dni.getText().trim());
        paciente.setNombre(nombre.getText().trim());
        paciente.setApellido(apellido.getText().trim());
        paciente.setSexo(Fechas.nullIfEmpty((String) sexo.getSelectedItem()));
        paciente.setDireccion(Fechas.nullIfEmpty(direccion.getText()));
        paciente.setTelefono(Fechas.nullIfEmpty(telefono.getText()));
        paciente.setNumeroHc(Fechas.nullIfEmpty(hc.getText()));
        paciente.setNumeroAfiliado(Fechas.nullIfEmpty(afiliado.getText()));
        paciente.setNacionalidad(Fechas.nullIfEmpty(nacionalidad.getText()));
        paciente.setReligion(Fechas.nullIfEmpty(religion.getText()));
        paciente.setEstadoCivil(Fechas.nullIfEmpty(estadoCivil.getText()));
        paciente.setAllegadoNombre(Fechas.nullIfEmpty(allNombre.getText()));
        paciente.setAllegadoDomicilio(Fechas.nullIfEmpty(allDomicilio.getText()));
        paciente.setAllegadoTelefono(Fechas.nullIfEmpty(allTelefono.getText()));
        paciente.setAntecedentesClinicos(Fechas.nullIfEmpty(antecedentes.getText()));
        Object os = obraSocial.getSelectedItem();
        paciente.setObraSocialId(os instanceof ObraSocial ? ((ObraSocial) os).getId() : null);

        Async.run(this, () -> {
            if (nuevo) dao.insertar(paciente); else dao.actualizar(paciente);
            return Boolean.TRUE;
        }, ok -> { guardado = true; dispose(); });
    }

    public boolean isGuardado() { return guardado; }
}
