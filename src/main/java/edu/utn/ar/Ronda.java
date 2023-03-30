package edu.utn.ar;

import java.util.ArrayList;
import java.util.List;

public class Ronda {
    private static int numeroDeRonda;
    private static List<Partido> listaDePartidos = new ArrayList<>();
    public Ronda(int n){
        this.numeroDeRonda = n;
    }
    public void añadirPartido(int i, Partido partido){
        this.listaDePartidos.add(i, partido);
    }
    public static int getNumeroDeRonda() {
        return numeroDeRonda;
    }
}
