package com.lalcec.model;

import java.time.LocalDateTime;

public class Consulta {
    private int id;
    private int pacienteId;
    private int medicoEspId;
    private String medicoEspecialidad; // solo lectura, para listados
    private LocalDateTime fechaHora;
    private String motivo;
    private String observaciones;
    private ConsultaGinecologia ginecologia; // null para especialidades generales

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPacienteId() { return pacienteId; }
    public void setPacienteId(int pacienteId) { this.pacienteId = pacienteId; }
    public int getMedicoEspId() { return medicoEspId; }
    public void setMedicoEspId(int medicoEspId) { this.medicoEspId = medicoEspId; }
    public String getMedicoEspecialidad() { return medicoEspecialidad; }
    public void setMedicoEspecialidad(String s) { this.medicoEspecialidad = s; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public ConsultaGinecologia getGinecologia() { return ginecologia; }
    public void setGinecologia(ConsultaGinecologia ginecologia) { this.ginecologia = ginecologia; }
}
