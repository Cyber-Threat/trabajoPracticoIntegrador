package edu.utn.ar.utils;

public class Participante {

    private final Integer identificacionUnivoca;
    private final String nombre;
    private Float puntosAcumulados;
    public Participante(Integer id, String nombre){
        this.identificacionUnivoca = id;
        this.nombre = nombre;
        this.puntosAcumulados = 0.0f;
    }
    public String getNombre() {
        return this.nombre;
    }
    public Float getPuntosAcumulados(){
        return this.puntosAcumulados;
    }
    public void adicionarPuntos(Float p){
        this.puntosAcumulados = this.getPuntosAcumulados() + p;
    }
    public Integer getIdentificacionUnivoca() {
        return identificacionUnivoca;
    }
}
