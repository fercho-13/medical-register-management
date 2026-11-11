package model;

import java.util.Date;

public class Medico extends Persona {
    private String matricula;

    public Medico() { super(); }

    public Medico(int idPersona, String dni, String nombre, String apellido, Date fechaNacimiento, String sexo, String direccion, String telefono, String matricula) {
        super(idPersona, dni, nombre, apellido, fechaNacimiento, sexo, direccion, telefono);
        this.matricula = matricula;
    }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
}
