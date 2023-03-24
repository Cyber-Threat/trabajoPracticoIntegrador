package edu.utn.ar;

import javax.xml.transform.Result;

public class Partido {
    private Equipo equipoLocal;
    private int golesDelLocal;
    private int golesDelVisitante;
    private ResultadoEnum resultado;
    private Equipo equipoVisitante;
    public Partido(Equipo equipoLocal, Equipo equipoVisitante){
        this.equipoLocal = equipoLocal;
        //this.golesDelLocal = (golesDelLocal == null) ? golesDelLocal : null;
        //this.golesDelVisitante = (golesDelVisitante == null) ? golesDelVisitante : null;
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
    public ResultadoEnum getResultado() { return resultado; }
    private ResultadoEnum setResultado() {
        if (this.golesDelVisitante < this.golesDelLocal) return ResultadoEnum.GANADOR;
        if (this.golesDelVisitante > this.golesDelLocal) return ResultadoEnum.PERDEDOR;
        return ResultadoEnum.EMPATE;
    }
}
