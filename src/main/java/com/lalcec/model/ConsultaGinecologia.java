package com.lalcec.model;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/** Detalle ginecológico (1 a 1 con Consulta). Los síntomas/flags se guardan por nombre de columna. */
public class ConsultaGinecologia {
    /** Columnas booleanas (TINYINT) de consultas_ginecologia, con su etiqueta para la UI. */
    public static final Map<String, String> FLAGS = new LinkedHashMap<>();
    static {
        FLAGS.put("dolor_bajo_vientre", "Dolor bajo vientre");
        FLAGS.put("flujo", "Flujo");
        FLAGS.put("prurito", "Prurito");
        FLAGS.put("sinusorragia", "Sinusorragia");
        FLAGS.put("algopaurenia", "Algopaurenia");
        FLAGS.put("metrorragia", "Metrorragia");
        FLAGS.put("prolapso", "Prolapso");
        FLAGS.put("amenorrea", "Amenorrea");
        FLAGS.put("tension_premenstrual", "Tensión premenstrual");
        FLAGS.put("incontinencia_orina", "Incontinencia de orina");
        FLAGS.put("lumbalgia", "Lumbalgia");
        FLAGS.put("esteril", "Estéril");
        FLAGS.put("patologia_mamaria", "Patología mamaria");
        FLAGS.put("alteracion_ciclo", "Alteración del ciclo");
        FLAGS.put("tumor_abdominal", "Tumor abdominal");
        FLAGS.put("tumor_mamario", "Tumor mamario");
        FLAGS.put("trichomonas", "Trichomonas");
        FLAGS.put("monilias", "Monilias");
        FLAGS.put("derrame_pezon", "Derrame por pezón");
    }

    private final Map<String, Boolean> flags = new LinkedHashMap<>();
    private String papanicolauResultado;
    private LocalDate papanicolauFecha;
    private String examenFlujoBacterial;
    private String diagnostico;
    private String planTratamiento;
    private String imagenColposcopica;
    private String schiller;
    private String examenMamarioInspeccion;
    private String examenMamarioPalpacion;
    private String adenopatias;

    public Map<String, Boolean> getFlags() { return flags; }
    public boolean isFlag(String col) { return Boolean.TRUE.equals(flags.get(col)); }
    public void setFlag(String col, boolean v) { flags.put(col, v); }

    public String getPapanicolauResultado() { return papanicolauResultado; }
    public void setPapanicolauResultado(String v) { this.papanicolauResultado = v; }
    public LocalDate getPapanicolauFecha() { return papanicolauFecha; }
    public void setPapanicolauFecha(LocalDate v) { this.papanicolauFecha = v; }
    public String getExamenFlujoBacterial() { return examenFlujoBacterial; }
    public void setExamenFlujoBacterial(String v) { this.examenFlujoBacterial = v; }
    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String v) { this.diagnostico = v; }
    public String getPlanTratamiento() { return planTratamiento; }
    public void setPlanTratamiento(String v) { this.planTratamiento = v; }
    public String getImagenColposcopica() { return imagenColposcopica; }
    public void setImagenColposcopica(String v) { this.imagenColposcopica = v; }
    public String getSchiller() { return schiller; }
    public void setSchiller(String v) { this.schiller = v; }
    public String getExamenMamarioInspeccion() { return examenMamarioInspeccion; }
    public void setExamenMamarioInspeccion(String v) { this.examenMamarioInspeccion = v; }
    public String getExamenMamarioPalpacion() { return examenMamarioPalpacion; }
    public void setExamenMamarioPalpacion(String v) { this.examenMamarioPalpacion = v; }
    public String getAdenopatias() { return adenopatias; }
    public void setAdenopatias(String v) { this.adenopatias = v; }
}
