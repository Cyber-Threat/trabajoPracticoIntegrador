package edu.utn.ar.utils;

public class Equipo {
    private final int identificacionUnivoca;
    private final String nombre;
    public Equipo(int id, String nombre){
        this.identificacionUnivoca = id;
        this.nombre = nombre;
    }
    public String getNombre() { return nombre; }
    public int getIdentificacionUnivoca() {
        return identificacionUnivoca;
    }
}
