package model;

import java.util.Date;

public class Consulta {
    private int idConsulta;
    private int fkPaciente;
    private int fkMedicoEsp;
    private Date fechaHora;
    private String motivo;
    private String observaciones;

    public Consulta() {}

    public Consulta(int idConsulta, int fkPaciente, int fkMedicoEsp, Date fechaHora, String motivo, String observaciones) {
        this.idConsulta = idConsulta;
        this.fkPaciente = fkPaciente;
        this.fkMedicoEsp = fkMedicoEsp;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.observaciones = observaciones;
    }

    public int getIdConsulta() { return idConsulta; }
    public void setIdConsulta(int idConsulta) { this.idConsulta = idConsulta; }
    public int getFkPaciente() { return fkPaciente; }
    public void setFkPaciente(int fkPaciente) { this.fkPaciente = fkPaciente; }
    public int getFkMedicoEsp() { return fkMedicoEsp; }
    public void setFkMedicoEsp(int fkMedicoEsp) { this.fkMedicoEsp = fkMedicoEsp; }
    public Date getFechaHora() { return fechaHora; }
    public void setFechaHora(Date fechaHora) { this.fechaHora = fechaHora; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}

