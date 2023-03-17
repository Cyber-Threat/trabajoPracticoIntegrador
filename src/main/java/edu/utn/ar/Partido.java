package edu.utn.ar;

import com.opencsv.bean.CsvBindByPosition;

public class Equipo {
    private static String nombre;
    private static int goles;
    private static String descripcion;
    // CONSTRUCTOR
    public Equipo(String nombre, int goles){
        this.nombre = nombre;
        this.goles = goles;
    }
    // GETTERS AND SETTERS

    public static String getNombre() {
        return nombre;
    }

    public static void setNombre(String nombre) {
        Equipo.nombre = nombre;
    }

    public static int getGoles() {
        return goles;
    }

    public static void setGoles(int goles) {
        Equipo.goles = goles;
    }

    public static String getDescripcion() {
        return descripcion;
    }

    public static void setDescripcion(String descripcion) {
        Equipo.descripcion = descripcion;
    }
}
