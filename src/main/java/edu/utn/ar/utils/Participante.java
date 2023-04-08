package edu.utn.ar.utils;

import java.awt.*;

public class Participante {

    private String identificacionUnivoca;
    private String nombre;
    private Float puntosAcumulados;
    public Participante(String id, String nombre){
        this.identificacionUnivoca = id;
        this.nombre = nombre;
        this.puntosAcumulados = 0.0f;
    }
    public String getNombre() {
        return this.nombre;
    }
    public void setPuntosAcumulados(Float puntos){
        this.puntosAcumulados = puntos;
    }
    public Float getPuntosAcumulados(){
        return this.puntosAcumulados;
    }
    public void adicionarPuntos(Float p){ this.puntosAcumulados = this.getPuntosAcumulados() + p; }
    public String getIdentificacionUnivoca() {
        return identificacionUnivoca;
    }
}
