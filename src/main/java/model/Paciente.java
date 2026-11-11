package model;

import java.util.Date;

public class Paciente extends Persona {
    private Integer fkObraSocial;
    private String numeroAfiliado;
    private String numeroHc;
    private String nacionalidad;
    private String religion;
    private String estadoCivil;
    private String allegadoNombre;
    private String allegadoDomicilio;
    private String allegadoTelefono;
    private String antecedentesClinicos;

    public Paciente() { super(); }

    public Paciente(int idPersona, String dni, String nombre, String apellido, Date fechaNacimiento, String sexo, String direccion, String telefono,
                    Integer fkObraSocial, String numeroAfiliado, String numeroHc, String nacionalidad, String religion, String estadoCivil,
                    String allegadoNombre, String allegadoDomicilio, String allegadoTelefono, String antecedentesClinicos) {
        super(idPersona, dni, nombre, apellido, fechaNacimiento, sexo, direccion, telefono);
        this.fkObraSocial = fkObraSocial;
        this.numeroAfiliado = numeroAfiliado;
        this.numeroHc = numeroHc;
        this.nacionalidad = nacionalidad;
        this.religion = religion;
        this.estadoCivil = estadoCivil;
        this.allegadoNombre = allegadoNombre;
        this.allegadoDomicilio = allegadoDomicilio;
        this.allegadoTelefono = allegadoTelefono;
        this.antecedentesClinicos = antecedentesClinicos;
    }

    public Integer getFkObraSocial() { return fkObraSocial; }
    public void setFkObraSocial(Integer fkObraSocial) { this.fkObraSocial = fkObraSocial; }
    public String getNumeroAfiliado() { return numeroAfiliado; }
    public void setNumeroAfiliado(String numeroAfiliado) { this.numeroAfiliado = numeroAfiliado; }
    public String getNumeroHc() { return numeroHc; }
    public void setNumeroHc(String numeroHc) { this.numeroHc = numeroHc; }
    public String getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }
    public String getReligion() { return religion; }
    public void setReligion(String religion) { this.religion = religion; }
    public String getEstadoCivil() { return estadoCivil; }
    public void setEstadoCivil(String estadoCivil) { this.estadoCivil = estadoCivil; }
    public String getAllegadoNombre() { return allegadoNombre; }
    public void setAllegadoNombre(String allegadoNombre) { this.allegadoNombre = allegadoNombre; }
    public String getAllegadoDomicilio() { return allegadoDomicilio; }
    public void setAllegadoDomicilio(String allegadoDomicilio) { this.allegadoDomicilio = allegadoDomicilio; }
    public String getAllegadoTelefono() { return allegadoTelefono; }
    public void setAllegadoTelefono(String allegadoTelefono) { this.allegadoTelefono = allegadoTelefono; }
    public String getAntecedentesClinicos() { return antecedentesClinicos; }
    public void setAntecedentesClinicos(String antecedentesClinicos) { this.antecedentesClinicos = antecedentesClinicos; }
}
