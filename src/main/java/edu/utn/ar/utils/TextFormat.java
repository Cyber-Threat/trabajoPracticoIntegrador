package edu.utn.ar.utils;

import java.io.File;
import java.util.List;

public abstract class TextFormat {
    public static class colors {
        public static final String reset = "\u001B[0m";
        public static final String black = "\u001B[30m";
        public static final String red = "\u001B[31m";
        public static final String green = "\u001B[32m";
        public static final String yellow = "\u001B[33m";
        public static final String blue = "\u001B[34m";
        public static final String purple = "\u001B[35m";
        public static final String cyan = "\u001B[36m";
        public static final String white = "\u001B[37m";
    }
    public static class effects {
        public static final String bold = "\u001B[1m";
        public static final String dim = "\u001B[2m";
        public static final String italic = "\u001B[3m";
        public static final String underline = "\u001B[4m";
        public static final String blink = "\u001B[5m";
        public static final String reverse = "\u001B[7m";
        public static final String hidden = "\u001B[8m";
    }
    public static class icons {
        public static final String success = "\t[" + colors.purple + "s" + colors.reset + "] ";
        public static final String info = "\t[" + colors.blue + "i" + colors.reset + "] ";
        public static final String error = "\t[" + colors.red + "e" + colors.reset + "] ";
        public static final String help = "\t[" + colors.green + "h" + colors.reset + "] ";
        public static final String warning = "\t[" + colors.yellow + "w" + colors.reset + "] ";
    }
    public static void imprimirPartidosInstanciados(List<Partido> partidos) {
        if (partidos != null) {
            int iP = 0;
            do {
                System.out.println(TextFormat.icons.info + "Identificacion univoca del partido: " + TextFormat.colors.blue + partidos.get(iP).getIdentificacionUnivoca() + TextFormat.colors.reset
                        + " Ronda: " + TextFormat.colors.blue + partidos.get(iP).getRondaCorrespondiente() + TextFormat.colors.reset
                        + " Equipo local: " + TextFormat.colors.green + partidos.get(iP).getEquipoLocal().getNombre() + TextFormat.colors.reset
                        + "(" + partidos.get(iP).getEquipoLocal().getIdentificacionUnivoca() + ")" + " Resultado del partido para el equipo local: " + TextFormat.colors.green + partidos.get(iP).getResultadoEquipoLocal() + TextFormat.colors.reset
                        + " Equipo visitante: " + TextFormat.colors.green + partidos.get(iP).getEquipoVisitante().getNombre() + TextFormat.colors.reset
                        + "(" + partidos.get(iP).getEquipoVisitante().getIdentificacionUnivoca() + ")" + " Resultado del partido para el equipo visitante: " + TextFormat.colors.green + partidos.get(iP).getResultadoEquipoVisitante() + TextFormat.colors.reset);
                iP++;
            } while (iP < partidos.size());
        }
    }
    public static void imprimirPronosticosDelParticipante(int indiceParticipantes, List<Participante> participantes, List<Pronostico> subListaPronosticos, String nombreBuffer, int i, Partido pBuffer) {
        String formatSpecifier = "%1$-64s";
        System.out.println(TextFormat.icons.info + String.format(formatSpecifier, "Pronosticos correspondientes al participante:") + TextFormat.colors.green + nombreBuffer + TextFormat.colors.reset
                + TextFormat.colors.blue + "\n\t ├─ " + TextFormat.colors.reset + String.format(formatSpecifier, "Identificacion univoca del partido referido:")
                + TextFormat.colors.blue + subListaPronosticos.get(i).getPartido().getIdentificacionUnivoca() + TextFormat.colors.reset
                + TextFormat.colors.blue + "\n\t ├─ " + TextFormat.colors.reset + String.format(formatSpecifier, "Equipo local del pronostico:")
                + TextFormat.colors.green + subListaPronosticos.get(i).getEquipoLocal().getNombre() + TextFormat.colors.reset + ": "
                + TextFormat.colors.cyan + subListaPronosticos.get(i).getPronosticoEquipoLocal() + TextFormat.colors.reset
                + TextFormat.colors.blue + "\n\t ├─ " + TextFormat.colors.reset + String.format(formatSpecifier, "Equipo visitante del pronostico:") + TextFormat.colors.green
                + subListaPronosticos.get(i).getEquipoVisitante().getNombre() + TextFormat.colors.reset + ": "
                + TextFormat.colors.cyan + subListaPronosticos.get(i).getPronosticoEquipoVisitante() + TextFormat.colors.reset
                + TextFormat.colors.blue + "\n\t ├─ " + TextFormat.colors.reset
                + String.format(formatSpecifier, "Resultados del partido referido por el participante:")
                + "Ronda: " + TextFormat.colors.blue + pBuffer.getRondaCorrespondiente() + TextFormat.colors.reset + " ► "
                + TextFormat.colors.green + pBuffer.getEquipoLocal().getNombre() + TextFormat.colors.reset
                + " (" + TextFormat.colors.red + pBuffer.getResultadoEquipoLocal() + TextFormat.colors.reset+ ")"
                + " vs. " + TextFormat.colors.green + pBuffer.getEquipoVisitante().getNombre() + TextFormat.colors.reset
                + " (" + TextFormat.colors.red + pBuffer.getResultadoEquipoVisitante() + TextFormat.colors.reset+ ")"
                + TextFormat.colors.blue + "\n\t └─ " + TextFormat.colors.purple + String.format(formatSpecifier, "Puntos acumulados: ")
                + participantes.get(indiceParticipantes).getPuntosAcumulados() + TextFormat.colors.reset);
    }
    public static void helpBanner() {
        System.out.println(icons.help + "Mensaje de ayuda:");
        System.out.println(colors.green + "\t ├─ " + colors.reset + "El programa esta pensado para leer dos archivos llamados \"pronosticos.csv\" y \"resultados.csv\".");
        System.out.println(colors.green + "\t ├─ " + colors.reset + "Dichos archivos tienen que ser especificados como parametros");
        System.out.println(colors.green + "\t ├─ " + colors.reset + "al momento de ejecutar el programa desde la consola de comandos.\"");
        System.out.println(colors.green + "\t ├─ " + colors.reset + "La sintaxis es la siguiente, primero la ruta del archivo de los pronosticos dados por los participantes, luego");
        System.out.println(colors.green + "\t ├─ " + colors.reset + "la ruta del archivo donde estan escritos los resultados de los partidos:");
        System.out.println(colors.green + "\t ├─ " + "java .\\Main.java <rutaDelArchivoPronosticosCSV> <rutaDelArchivoResultadosCSV>" + colors.reset);
        System.out.println(colors.green + "\t ├─ " + colors.reset + "Una vez especificadas las rutas a los archivos necesarios el programa puede interpretar su contenido");
        System.out.println(colors.green + "\t ├─ " + colors.reset + "y opcionalmente volcando en un archivo \"puntuaciones.csv\" los participantes y sus respectivas puntuaciones, asi");
        System.out.println(colors.green + "\t └─ " + colors.reset + "como tambien el ganador con los mejores pronosticos para los partidos dados.");
    }

    public static void informarError(File ArchivoCSV, int lineNumber, Exception e) {
        System.out.println(TextFormat.icons.error + "Lectura erronea del archivo: " + TextFormat.colors.red + ArchivoCSV.toPath().getFileName() + TextFormat.colors.reset);
        System.out.println(TextFormat.icons.error + "En el renglon: " + TextFormat.colors.red + (lineNumber + 1) + TextFormat.colors.reset);
        System.out.println(TextFormat.icons.error + "Excepcion: " + e.getMessage());
        System.out.println(TextFormat.icons.error + e.getMessage());
    }
    public static void imprimirEquiposInstanciados(List<Equipo> equipos) {
        int iE = 0;
        do {
            System.out.println(TextFormat.icons.info + "Equipo instanciado: " + TextFormat.colors.green + equipos.get(iE).getNombre() + TextFormat.colors.reset
                    + " Identificacion univoca: " + TextFormat.colors.green + equipos.get(iE).getIdentificacionUnivoca() + TextFormat.colors.reset);
            iE++;
        } while (iE < equipos.size());
    }
    public static void imprimirTablaDePuntuaciones(List<Participante> participantes) {
        // IMPRIMO INFORMACION EN PANTALLA PARA PODER VISUALIZARLA!
        int iP = 0;
        System.out.println(TextFormat.colors.cyan + "┌" + String.format("%1$-128s", " ").replace(' ', '─') + "┐");
        System.out.println(TextFormat.colors.cyan + "│" + String.format("%1$-114s", "\t\t\t\t\t► ► ► TABLA DE PUNTUACIONES ◄ ◄ ◄") + "│" + TextFormat.colors.reset);
        do {
            System.out.println(TextFormat.colors.cyan + "│" + TextFormat.colors.reset + "\t\tPuesto (" + TextFormat.colors.cyan + (iP + 1) + TextFormat.colors.reset + "): "
                    + TextFormat.colors.green + String.format("%1$-48s", participantes.get(iP).getNombre()) + TextFormat.colors.reset
                    + String.format("%-79s", " Identificacion univoca: " + TextFormat.colors.blue + participantes.get(iP).getIdentificacionUnivoca() + TextFormat.colors.reset
                    + " Puntos: (" + TextFormat.colors.purple + participantes.get(iP).getPuntosAcumulados() + TextFormat.colors.reset + ")") + TextFormat.colors.cyan + "│");
            iP++;
        } while (iP < participantes.size());
        System.out.println(TextFormat.colors.cyan + "└" + String.format("%1$-128s", " ").replace(' ', '─') + "┘" + TextFormat.colors.reset);
    }
}