package edu.utn.ar.utils;

public class Partido {

    // ATRIBUTOS DEL OBJETO PARTIDO
    private final int rondaCorrespondiente;
    private final int identificacionUnivoca;
    private final Equipo equipoLocal;
    private final int golesDelLocal;
    private final ResultadoEnum resultadoEquipoLocal;
    private final int golesDelVisitante;
    private final ResultadoEnum resultadoEquipoVisitante;
    private final Equipo equipoVisitante;
    // CONSTRUCTOR
    // ESTE CONSTRUCTOR LO USO A LA HORA DE LEER EL ARCHIVO RESULTADOS.CSV E INSTANCIAR LOS PARTIDOS!
    public Partido(int rondaCorrespondiente, int id, Equipo equipoLocal, int gL, int gV, Equipo equipoVisitante){
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
    public int getIdentificacionUnivoca() {
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