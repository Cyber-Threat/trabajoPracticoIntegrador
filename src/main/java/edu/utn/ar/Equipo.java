package edu.utn.ar;

import com.opencsv.bean.CsvBindByPosition;

public class Equipo {
    @CsvBindByPosition(position = 0)
    private static String nombre;
    @CsvBindByPosition(position = 1)
    private static int goles;
    private static String descripcion;

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
