package com.lalcec.model;

public class ObraSocial {
    private final int id;
    private final String nombre;

    public ObraSocial(int id, String nombre) { this.id = id; this.nombre = nombre; }
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    @Override public String toString() { return nombre; }
}
