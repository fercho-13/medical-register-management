package com.lalcec.model;

/** Combinación médico + especialidad (tabla medico_especialidad), usada al registrar consultas. */
public class MedicoEspecialidad {
    private final int id;
    private final String medico;
    private final String especialidad;

    public MedicoEspecialidad(int id, String medico, String especialidad) {
        this.id = id; this.medico = medico; this.especialidad = especialidad;
    }
    public int getId() { return id; }
    public String getMedico() { return medico; }
    public String getEspecialidad() { return especialidad; }

    /** Ginecología y Patología Cervical usan el formulario detallado. */
    public boolean usaFormularioGinecologico() {
        String e = especialidad.toLowerCase();
        return e.contains("ginecolog") || e.contains("cervical");
    }
    @Override public String toString() { return medico + " — " + especialidad; }
}
