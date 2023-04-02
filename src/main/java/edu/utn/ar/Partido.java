package edu.utn.ar;

import javax.xml.transform.Result;

public class Partido {
    // ATRIBUTOS DEL OBJETO PARTIDO
    private String identificacionUnivoca;
    private Equipo equipoLocal;
    private int golesDelLocal;
    private ResultadoEnum resultadoEquipoLocal;
    private int golesDelVisitante;
    private ResultadoEnum resultadoEquipoVisitante;
    private Equipo equipoVisitante;
    // CONSTRUCTORES
    public Partido(){ }
    public Partido (Equipo equipoLocal, Equipo equipoVisitante){
        this.equipoLocal = equipoLocal;
        this.equipoVisitante = equipoVisitante;
    }
    // ESTE CONSTRUCTOR LO USO A LA HORA DE LEER EL ARCHIVO RESULTADOS.CSV E INSTANCIAR LOS PARTIDOS!
    public Partido(String id, Equipo equipoLocal, int gL, int gV, Equipo equipoVisitante){
        this.identificacionUnivoca = id;
        this.equipoLocal = equipoLocal;
        this.resultadoEquipoLocal = this.setResultadoEquipoLocal(gL, gV);
        this.golesDelVisitante = gL;
        this.golesDelLocal = gV;
        this.equipoVisitante = equipoVisitante;
        this.resultadoEquipoVisitante = this.setResultadoEquipoVisitante(gL, gV);
    }
    // METODOS!
    public Equipo getEquipoLocal() { return equipoLocal; }
    public int getGolesDelLocal() { return golesDelLocal; }
    public int getGolesDelVisitante() { return golesDelVisitante; }
    public Equipo getEquipoVisitante() { return equipoVisitante; }
    public String getIdentificacionUnivoca() {
        return identificacionUnivoca;
    }
    private ResultadoEnum setResultadoEquipoLocal(int gL, int gV){
        ResultadoEnum resultadoBuffer = ResultadoEnum.EMPATE;
        if (gL > gV){
            resultadoBuffer = ResultadoEnum.GANADOR;
        }
        if (gL < gV){
            resultadoBuffer = ResultadoEnum.PERDEDOR;
        }
        return resultadoBuffer;
    }
    public ResultadoEnum getResultadoEquipoLocal(){
        return this.resultadoEquipoLocal;
    }
    private ResultadoEnum setResultadoEquipoVisitante(int gL, int gV){
        ResultadoEnum resultadoBuffer = ResultadoEnum.EMPATE;
        if (gL < gV){
            resultadoBuffer = ResultadoEnum.GANADOR;
        }
        if (gL > gV){
            resultadoBuffer = ResultadoEnum.PERDEDOR;
        }
        return resultadoBuffer;
    }
    public ResultadoEnum getResultadoEquipoVisitante(){
        return this.resultadoEquipoVisitante;
    }
}