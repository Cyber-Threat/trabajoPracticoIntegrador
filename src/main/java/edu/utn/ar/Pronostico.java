package edu.utn.ar;

public class Pronostico {
    private String nombreParticipante;
    private Partido partido;
    private Equipo equipo;
    private ResultadoEnum resultado;
    public Pronostico(String n, Partido p, Equipo e, ResultadoEnum r){
        this.nombreParticipante = n;
        this.partido = p;
        this.equipo = e;
        this.resultado = r;
    }
    public Partido getPartido() { return partido; }
    public void setPartido(Partido partido) { this.partido = partido; }
    public Equipo getEquipo() { return equipo; }
    public void setEquipo(Equipo equipo) { this.equipo = equipo; }
    public ResultadoEnum getResultado() { return resultado; }
    public void setResultado(ResultadoEnum resultado) { this.resultado = resultado; }
    public String getNombreParticipante(){ return this.nombreParticipante; }
}
