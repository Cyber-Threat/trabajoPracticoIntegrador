package edu.utn.ar;

public class Partido {
    private Equipo equipoLocal;
    private int golesDelLocal;
    private int golesDelVisitante;
    private Equipo equipoVisitante;
    public Partido(Equipo equipoLocal, int golesDelLocal, int golesDelVisitante, Equipo equipoVisitante){
        this.equipoLocal = equipoLocal;
        this.golesDelLocal = golesDelLocal;
        this.golesDelVisitante = golesDelVisitante;
        this.equipoVisitante = equipoVisitante;
    }
    public Equipo getEquipoLocal() { return equipoLocal; }
    public void setEquipoLocal(Equipo equipoLocal) { this.equipoLocal = equipoLocal; }
    public int getGolesDelLocal() { return golesDelLocal; }
    public void setGolesDelLocal(int golesDelLocal) { this.golesDelLocal = golesDelLocal; }
    public int getGolesDelVisitante() { return golesDelVisitante; }
    public void setGolesDelVisitante(int golesDelVisitante) { this.golesDelVisitante = golesDelVisitante; }
    public Equipo getEquipoVisitante() { return equipoVisitante; }
    public void setEquipoVisitante(Equipo equipoVisitante) { this.equipoVisitante = equipoVisitante; }
}
