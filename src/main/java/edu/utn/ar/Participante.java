package edu.utn.ar;

public class Participante {
    private static String nombre;
    private static Float puntosAcumulados;
    public Participante(String nombre){
        this.nombre = nombre;
    }
    public static String getNombre() {
        return nombre;
    }
    public void setPuntosAcumulados(Float puntos){
        this.puntosAcumulados = puntos;
    }
    public Float getPuntosAcumulados(){
        return this.puntosAcumulados;
    }
}
