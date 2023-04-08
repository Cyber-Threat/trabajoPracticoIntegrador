package edu.utn.ar.utils;

public class Partido {

    // ATRIBUTOS DEL OBJETO PARTIDO
    private int rondaCorrespondiente;
    private String identificacionUnivoca;
    private Equipo equipoLocal;
    private int golesDelLocal;
    private ResultadoEnum resultadoEquipoLocal;
    private int golesDelVisitante;
    private ResultadoEnum resultadoEquipoVisitante;
    private Equipo equipoVisitante;
    // CONSTRUCTOR
    // ESTE CONSTRUCTOR LO USO A LA HORA DE LEER EL ARCHIVO RESULTADOS.CSV E INSTANCIAR LOS PARTIDOS!
    public Partido(int rondaCorrespondiente, String id, Equipo equipoLocal, int gL, int gV, Equipo equipoVisitante){
        this.rondaCorrespondiente = rondaCorrespondiente;
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
            resultadoBuffer = ResultadoEnum.VICTORIA;
        }
        if (gL < gV){
            resultadoBuffer = ResultadoEnum.DERROTA;
        }
        return resultadoBuffer;
    }
    public ResultadoEnum getResultadoEquipoLocal(){
        return this.resultadoEquipoLocal;
    }
    private ResultadoEnum setResultadoEquipoVisitante(int gL, int gV){
        ResultadoEnum resultadoBuffer = ResultadoEnum.EMPATE;
        if (gL < gV){
            resultadoBuffer = ResultadoEnum.VICTORIA;
        }
        if (gL > gV){
            resultadoBuffer = ResultadoEnum.DERROTA;
        }
        return resultadoBuffer;
    }
    public ResultadoEnum getResultadoEquipoVisitante(){
        return this.resultadoEquipoVisitante;
    }
    public int getRondaCorrespondiente() {
        return rondaCorrespondiente;
    }
}