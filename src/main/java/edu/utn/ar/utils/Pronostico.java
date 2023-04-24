package edu.utn.ar.utils;

public class Pronostico {
    private final int idUnivoca;
    private final Participante ObjParticipante;
    private final Partido partido;
    private final Equipo equipoLocal;
    private final Equipo equipoVisitante;
    private final ResultadoEnum pronosticoEquipoLocal;
    private final ResultadoEnum pronosticoEquipoVisitante;
    public Pronostico(int idUnivoca, Participante participante, Partido p, Equipo eL, Equipo eV, ResultadoEnum pronosticoEquipoLocal, ResultadoEnum pronosticoEquipoVisitante){
        this.idUnivoca = idUnivoca;
        this.ObjParticipante = participante;
        this.partido = p;
        this.equipoLocal = eL;
        this.equipoVisitante = eV;
        this.pronosticoEquipoLocal = pronosticoEquipoLocal;
        this.pronosticoEquipoVisitante = pronosticoEquipoVisitante;
    }
    public Partido getPartido() { return partido; }
//  public void setPartido(Partido partido) { this.partido = partido; }
    public Equipo getEquipoLocal() { return equipoLocal; }
//  public void setEquipoLocal(Equipo equipo) { this.equipoLocal = equipo; }
    public Equipo getEquipoVisitante() { return equipoVisitante; }
//  public void setEquipoVisitante(Equipo equipoVisitante) { this.equipoVisitante = equipoVisitante; }
    public Participante getObjParticipante(){ return this.ObjParticipante; }
    public ResultadoEnum getPronosticoEquipoLocal (){
        return this.pronosticoEquipoLocal;
    }
    public ResultadoEnum getPronosticoEquipoVisitante (){
        return this.pronosticoEquipoVisitante;
    }

    public int getIdUnivoca() {
        return idUnivoca;
    }
}
