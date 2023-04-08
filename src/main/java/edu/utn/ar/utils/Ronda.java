package edu.utn.ar.utils;

import java.util.List;

public class Ronda {
    private List<Partido> lP;
    public Ronda(List<Partido> lP) {
        this.lP = lP;
    }
    public List<Partido> getPartidos(){
        return this.lP;
    }
}