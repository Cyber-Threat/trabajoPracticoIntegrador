package edu.utn.ar.utils;

public class Participante {
    private final Integer identificacionUnivoca;
    private final String nombre;
    private int puntosAcumulados;
    public Participante(Integer id, String nombre){
        this.identificacionUnivoca = id;
        this.nombre = nombre;
        this.puntosAcumulados = 0;
    }
    public String getNombre() {
        return this.nombre;
    }
    public int getPuntosAcumulados(){
        return this.puntosAcumulados;
    }
    public void adicionarPuntos(int p){
        this.puntosAcumulados = this.getPuntosAcumulados() + p;
    }
    public Integer getIdentificacionUnivoca() {
        return identificacionUnivoca;
    }
}
