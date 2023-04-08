package edu.utn.ar.utils;

public class Equipo {
    private String identificacionUnivoca;
    private String nombre;
    private String descripcion;
    public Equipo(){
    }
    public Equipo(String id, String nombre){
        this.identificacionUnivoca = id;
        this.nombre = nombre;
    }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getIdentificacionUnivoca() {
        return identificacionUnivoca;
    }
    public void setIdentificacionUnivoca(String identificacionUnivoca) {
        this.identificacionUnivoca = identificacionUnivoca;
    }
}
