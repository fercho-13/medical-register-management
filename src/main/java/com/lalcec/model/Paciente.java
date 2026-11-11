package com.lalcec.model;

public class Paciente extends Persona {
    private Integer obraSocialId;
    private String numeroAfiliado;
    private String numeroHc;
    private String nacionalidad;
    private String religion;
    private String estadoCivil;
    private String allegadoNombre;
    private String allegadoDomicilio;
    private String allegadoTelefono;
    private String antecedentesClinicos;

    public Integer getObraSocialId() { return obraSocialId; }
    public void setObraSocialId(Integer obraSocialId) { this.obraSocialId = obraSocialId; }
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
