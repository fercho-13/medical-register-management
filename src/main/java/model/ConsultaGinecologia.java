package model;

import java.util.Date;

public class ConsultaGinecologia extends Consulta {
    private boolean dolorBajoVientre;
    private boolean flujo;
    private String papanicolauResultado;
    private Date papanicolauFecha;
    private String examenMamarioInspeccion;
    private String diagnostico;
    // ... Agregar el resto de los booleanos y campos según el diseño

    public ConsultaGinecologia() { super(); }

    public boolean isDolorBajoVientre() { return dolorBajoVientre; }
    public void setDolorBajoVientre(boolean dolorBajoVientre) { this.dolorBajoVientre = dolorBajoVientre; }
    public boolean isFlujo() { return flujo; }
    public void setFlujo(boolean flujo) { this.flujo = flujo; }
    public String getPapanicolauResultado() { return papanicolauResultado; }
    public void setPapanicolauResultado(String papanicolauResultado) { this.papanicolauResultado = papanicolauResultado; }
    public Date getPapanicolauFecha() { return papanicolauFecha; }
    public void setPapanicolauFecha(Date papanicolauFecha) { this.papanicolauFecha = papanicolauFecha; }
    public String getExamenMamarioInspeccion() { return examenMamarioInspeccion; }
    public void setExamenMamarioInspeccion(String examenMamarioInspeccion) { this.examenMamarioInspeccion = examenMamarioInspeccion; }
    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }
}

