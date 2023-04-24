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
        String formatSpecifier = "%1$-64s";
        if (partidos != null) {
            int iP = 0;
            do {
                System.out.println(TextFormat.colors.cyan + String.format("%1$-128s", " ").replace(' ', '─') + TextFormat.colors.reset);
                System.out.println(TextFormat.icons.info + "Informacion del partido instanciado!"
                                 + TextFormat.colors.blue + "\n\t ├─ " + TextFormat.colors.reset +  String.format(formatSpecifier, "Identificacion univoca del partido: ")
                                 + TextFormat.colors.blue + partidos.get(iP).getIdentificacionUnivoca() + TextFormat.colors.reset
                                 + TextFormat.colors.blue + "\n\t ├─ " + TextFormat.colors.reset +  String.format(formatSpecifier, "Ronda: ") + TextFormat.colors.blue + partidos.get(iP).getRondaCorrespondiente() + TextFormat.colors.reset
                                 + TextFormat.colors.blue + "\n\t ├─ " + TextFormat.colors.reset +  String.format(formatSpecifier, "Equipo local: ") + TextFormat.colors.green + partidos.get(iP).getEquipoLocal().getNombre() + TextFormat.colors.reset
                                 + "(" + partidos.get(iP).getEquipoLocal().getIdentificacionUnivoca() + ")"
                                 + TextFormat.colors.blue + "\n\t ├─ " + TextFormat.colors.reset +  String.format(formatSpecifier, "Resultado del partido para el equipo local: ") + TextFormat.colors.green + partidos.get(iP).getResultadoEquipoLocal() + TextFormat.colors.reset
                                 + TextFormat.colors.blue + "\n\t ├─ " + TextFormat.colors.reset +  String.format(formatSpecifier, "Equipo visitante: ") + TextFormat.colors.green + partidos.get(iP).getEquipoVisitante().getNombre() + TextFormat.colors.reset
                                 + "(" + partidos.get(iP).getEquipoVisitante().getIdentificacionUnivoca() + ")"
                                 + TextFormat.colors.blue + "\n\t └─ " + TextFormat.colors.reset +  String.format(formatSpecifier, "Resultado del partido para el equipo visitante: ") + TextFormat.colors.green + partidos.get(iP).getResultadoEquipoVisitante() + TextFormat.colors.reset);
                iP++;
            } while (iP < partidos.size());
        }
    }
    public static void imprimirPronosticoDelParticipante(List<Pronostico> listaPronosticos, int iterador, boolean condicionDeCarga) {
        String formatSpecifier = "%1$-64s";
        System.out.println(icons.info + String.format(formatSpecifier, "Informacion del pronostico ID: ") + colors.blue + listaPronosticos.get(iterador).getIdUnivoca() + colors.reset
                                      + TextFormat.colors.blue + "\n\t ├─ " + colors.reset
                                      + String.format(formatSpecifier, "Nombre del participante: ") + colors.cyan
                                      + listaPronosticos.get(iterador).getObjParticipante().getNombre() + colors.reset
                                      + TextFormat.colors.blue + "\n\t ├─ " + colors.reset
                                      + String.format(formatSpecifier, "Identificacion univoca del participante: ") + colors.cyan
                                      + listaPronosticos.get(iterador).getObjParticipante().getIdentificacionUnivoca() + colors.reset
                                      + TextFormat.colors.blue + "\n\t ├─ " + colors.reset
                                      + String.format(formatSpecifier, "Partido al que se refiere: ")+ "ID: " + colors.green
                                      + listaPronosticos.get(iterador).getPartido().getIdentificacionUnivoca() + colors.reset + " ◄► "
                                      + listaPronosticos.get(iterador).getPartido().getEquipoLocal().getNombre()+ " " + colors.blue + listaPronosticos.get(iterador).getPartido().getResultadoEquipoLocal() + colors.reset
                                      + " vs. " + listaPronosticos.get(iterador).getPartido().getEquipoVisitante().getNombre()+ " " + colors.blue + listaPronosticos.get(iterador).getPartido().getResultadoEquipoVisitante() + colors.reset
                                      + TextFormat.colors.blue + "\n\t └─ " + TextFormat.colors.reset
                                      + String.format(formatSpecifier, "Pronostico hecho: ")
                                      + ((condicionDeCarga) ? colors.purple + "CORRECTO! " + colors.reset + "◄► " + colors.reset: colors.red + "INCORRECTO! " + colors.reset + "◄► " + colors.reset)
                                      + listaPronosticos.get(iterador).getEquipoLocal().getNombre() + " " + colors.green
                                      + listaPronosticos.get(iterador).getPronosticoEquipoLocal() + colors.reset + " vs. "
                                      + listaPronosticos.get(iterador).getEquipoVisitante().getNombre() + " " + colors.green
                                      + listaPronosticos.get(iterador).getPronosticoEquipoVisitante() + colors.reset);
    }
    public static void helpBanner() {
        System.out.println(icons.help + effects.bold + "Mensaje de ayuda:");
        System.out.println(colors.green + "\t ├─ " + colors.reset + "El programa esta pensado para analizar un archivo \"" + colors.green + "resultados.csv" + colors.reset + "\".");
        System.out.println(colors.green + "\t ├─ " + colors.reset + "Ese archivo se va a utilizar para interpretar que partidos y sus respectivos equipos jugaron, ");
        System.out.println(colors.green + "\t ├─ " + colors.reset + "mientras que \"" + colors.green + "pronosticos.csv" + colors.reset + "\" se va a utilizar para interpretar que pronosticos hicieron los participantes. ");
        System.out.println(colors.green + "\t ├─ " + colors.reset + "El uso es el siguiente, notese que el parametro " + colors.purple + effects.bold + "-p" + colors.reset + " es opcional unicamente cuando");
        System.out.println(colors.green + "\t ├─ " + colors.purple + effects.bold + "-d = remota " + colors.reset + "dado que si no se especifica un archivo CSV se intenta buscar los pronosticos en una ");
        System.out.println(colors.green + "\t ├─ " + colors.reset + "base de datos el link de acceso y las credenciales tomadas del archivo de configuracion especificado. Entonces los parametros son los siguientes: ");
        System.out.println(colors.green + "\t │ " + colors.reset);
        System.out.println(colors.green + "\t ├─ " + colors.purple + effects.italic + "pronosticosDeportivos -d <local | remota> -r <rutaDelArchivoResultadosCSV> -c <rutaDelArchivoConfigCSV> [Opcional] -p <rutaDelArchivoPronosticosCSV> -h" + colors.reset);
        System.out.println(colors.green + "\t ├─ " + colors.purple + String.format("%1$-50s", "-p, pronosticos <rutaDelArchivoPronosticosCSV> ") + colors.reset + "\t■ Ruta del archivo donde se encuentran los pronosticos hechos por los participantes.");
        System.out.println(colors.green + "\t ├─ " + colors.purple + String.format("%1$-50s", "-r, resultados <rutaDelArchivoResultadosCSV> ") + colors.reset + "\t■ Ruta del archivo donde se encuentran los resultados de los partidos.");
        System.out.println(colors.green + "\t ├─ " + colors.purple + String.format("%1$-50s", "-d, database <local | remota> ") + colors.reset + "\t■ Ruta donde se va a almacenar la tabla de puntuaciones que produce este programa como salida.");
        System.out.println(colors.green + "\t ├─ " + colors.purple + String.format("%1$-50s", "-c, config <rutaDelArchivoConfigCSV> ") + colors.reset + "\t■ Ruta del archivo donde se encuentra la configuracion necesaria para conectarse la DB o la carga de puntos a los participantes.");
        System.out.println(colors.green + "\t ├─ " + colors.purple + String.format("%1$-50s", "-h, help ") + colors.reset + "\t■ Muestra este mensaje de ayuda.");
        System.out.println(colors.green + "\t │ " + colors.reset);
        System.out.println(colors.green + "\t ├─ " + colors.reset + "Una vez especificada la ruta al archivo de resultados, y una vez especificado de donde se dispone de los pronosticos,");
        System.out.println(colors.green + "\t ├─ " + colors.reset + "el programa ya esta listo para operar, dando como resultado una salida de texto con");
        System.out.println(colors.green + "\t └─ " + colors.reset + "una tabla de puntuaciones y el ganador.");
    }
    public static void informarError(File ArchivoCSV, int lineNumber, Exception e) {
        System.out.println(TextFormat.icons.error + "Lectura erronea del archivo: " + TextFormat.colors.red + ArchivoCSV.toPath().getFileName() + TextFormat.colors.reset);
        System.out.println(TextFormat.icons.error + "En el renglon: " + TextFormat.colors.red + (lineNumber + 1) + TextFormat.colors.reset);
        System.out.println(TextFormat.icons.error + "Excepcion: " + e.getMessage());
        System.out.println(TextFormat.icons.error + e.getMessage());
    }
    public static void informarError(Exception e){
        System.out.println(TextFormat.icons.error + "Algo salio mal:"
                + String.format("%1$-32s", "\n\t\tClase de excepcion: ") + "× " + TextFormat.colors.red + e.getClass() + TextFormat.colors.reset
                + String.format("%1$-32s", "\n\t\tCausa de la excepcion: ") + "× " + TextFormat.colors.red + e.getCause() + TextFormat.colors.reset
                + String.format("%1$-32s", "\n\t\tMensaje: ") + "× " + TextFormat.colors.red + e.getLocalizedMessage() + TextFormat.colors.reset);
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
        System.out.println(TextFormat.colors.cyan + effects.bold + String.format("%1$-114s", "\t\t\t\t\t► ► ► TABLA DE PUNTUACIONES ◄ ◄ ◄") + TextFormat.colors.reset);
        System.out.println(TextFormat.colors.cyan + "┌" + String.format("%1$-128s", " ").replace(' ', '─') + "┐");
        do {
            System.out.println(TextFormat.colors.cyan + "│" + TextFormat.colors.reset + "\t\tPuesto (" + TextFormat.colors.cyan + (iP + 1) + TextFormat.colors.reset + "): "
                    + TextFormat.colors.green + String.format("%1$-48s", participantes.get(iP).getNombre()) + TextFormat.colors.reset
                    + String.format("%-79s", " Numero de DNI/Pasaporte: " + TextFormat.colors.blue + participantes.get(iP).getIdentificacionUnivoca() + TextFormat.colors.reset
                    + " Puntos: (" + TextFormat.colors.purple + participantes.get(iP).getPuntosAcumulados() + TextFormat.colors.reset + ")") + TextFormat.colors.cyan + "│");
            iP++;
        } while (iP < participantes.size());
        System.out.println(TextFormat.colors.cyan + "└" + String.format("%1$-128s", " ").replace(' ', '─') + "┘" + TextFormat.colors.reset);
    }
}