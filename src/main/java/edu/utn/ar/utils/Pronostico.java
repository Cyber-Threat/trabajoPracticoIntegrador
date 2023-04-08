package edu.utn.ar.utils;

public class Pronostico {
    private String nombreParticipante;
    private Partido partido;
    private Equipo equipoLocal;
    private Equipo equipoVisitante;
    private ResultadoEnum pronosticoEquipoLocal;
    private ResultadoEnum pronosticoEquipoVisitante;
    public Pronostico(){
    }
    public Pronostico(String n, Partido p, Equipo eL, Equipo eV, ResultadoEnum pronosticoEquipoLocal, ResultadoEnum pronosticoEquipoVisitante){
        this.nombreParticipante = n;
        this.partido = p;
        this.equipoLocal = eL;
        this.equipoVisitante = eV;
        this.pronosticoEquipoLocal = pronosticoEquipoLocal;
        this.pronosticoEquipoVisitante = pronosticoEquipoVisitante;
    }
    public Partido getPartido() { return partido; }
    public void setPartido(Partido partido) { this.partido = partido; }
    public Equipo getEquipoLocal() { return equipoLocal; }
    public void setEquipoLocal(Equipo equipo) { this.equipoLocal = equipo; }
    public Equipo getEquipoVisitante() { return equipoVisitante; }
    public void setEquipoVisitante(Equipo equipoVisitante) { this.equipoVisitante = equipoVisitante; }
    public String getNombreParticipante(){ return this.nombreParticipante; }
    public ResultadoEnum getPronosticoEquipoLocal (){
        return this.pronosticoEquipoLocal;
    }
    public ResultadoEnum getPronosticoEquipoVisitante (){
        return this.pronosticoEquipoVisitante;
    }
}
