package edu.utn.ar.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public abstract class validacionDeDatos {
    // TABLA DE VALORES BOOLEANOS AUXILIARES PARA CONTROLAR EL FLUJO DEL PROGRAMA EN FUNCION DE LA VALIDEZ DE LOS ARGUMENTOS PASADOS COMO PARAMETROS AL METODO MAIN
    private static boolean rutaValidaDeResultadosCSV = true;
    private static boolean rutaValidaDePronosticosCSV = true;
    private static boolean archivoValidoDeResultados = true;
    private static boolean archivoValidoDePronosticos = true;
    public static boolean rutaEspecificadaEsDirectorio(Path rutaDeArchivoPronosticos, Path rutaDeArchivoResultados){
        // VALIDACION DE LOS DATOS INTRODUCIDOS POR CONSOLA! SI LAS RUTAS INTRODUCIDAS SON SOLO DIRECTORIOS NO SIGO CON EL PROGRAMA Y SE LO INFORMO AL USUARIO!
        if (Files.isDirectory(rutaDeArchivoPronosticos)) {
            rutaValidaDePronosticosCSV = false;
        }
        if (Files.isDirectory(rutaDeArchivoResultados)) {
            rutaValidaDeResultadosCSV = false;
        }
        // INFORMO DEL ERROR EN CUESTION!
        if (!rutaValidaDePronosticosCSV || !rutaValidaDeResultadosCSV) {
            if (!rutaValidaDePronosticosCSV) {
                System.out.println(TextFormat.icons.error + "Error. La ruta especificada es directorio: " + TextFormat.colors.red + rutaDeArchivoPronosticos + TextFormat.colors.reset);
            }
            if (!rutaValidaDeResultadosCSV) {
                System.out.println(TextFormat.icons.error + "Error. La ruta especificada es directorio: " + TextFormat.colors.red + rutaDeArchivoResultados + TextFormat.colors.reset);
            }
            return true;
        }
    return false;
    }
    public static boolean losArchivosNoExisten(Path rutaDeArchivoPronosticos, Path rutaDeArchivoResultados, File pronosticosArchivoCSV, File resultadosArchivoCSV) {
        if (!pronosticosArchivoCSV.exists()) {
            archivoValidoDePronosticos = false;
        }
        if (!resultadosArchivoCSV.exists()) {
            archivoValidoDeResultados = false;
        }
        // INFORMO DEL ERROR EN CUESTION!
        if (!archivoValidoDePronosticos || !archivoValidoDeResultados) {
            if (!archivoValidoDePronosticos) {
                System.out.println(TextFormat.icons.error + "El archivo especificado no existe: " + TextFormat.colors.red + rutaDeArchivoPronosticos + TextFormat.colors.reset);
            }
            if (!archivoValidoDeResultados) {
                System.out.println(TextFormat.icons.error + "El archivo especificado no existe: " + TextFormat.colors.red + rutaDeArchivoResultados + TextFormat.colors.reset);
            }
            return true;
        }
        return false;
    }
    public static void validacionDelPronosticoLeido(List<Pronostico> pronosticos, String[] line, String[] finalLine) throws Exception {
        if (line.length != 6) {
            throw new Exception("La cantidad de campos introducidos es incorrecta.");
        }
        // ME ASEGURO QUE EXISTA EL NOMBRE DEL PARTICIPANTE
        if (line[0].equals("")) {
            throw new Exception("El campo correspondiente al nombre del participante esta vacio.");
        }
        // ME ASEGURO QUE SE HAYA INTRODUCIDO EL NOMBRE DE LOS EQUIPOS
        if (line[1].equals("") || line[5].equals("")) {
            throw new Exception("El campo correspondiente a uno de los equipos esta vacio.");
        }
        // ME ASEGURO QUE EXISTA EL VALOR QUE DICTA EL RESULTADO DEL PARTIDO
        if (line[2].equals("") && line[3].equals("") && line[4].equals("")) {
            throw new Exception("Los campos que pronostican el resultado del partido están vacios.");
        }
        // ENUMERADOR VALIDO (SOLO DEBE SER UNA "X" MAYUSCULA O MINUSCULA!
        if (!enumeradorValido(line[2], line[3], line[4])) {
            throw new Exception("El enumerador usado en los campos donde se pronostica el resultado del partido, es invalido.");
        }
        // ME ASEGURO EN ULTIMA INSTANCIA QUE EL PRONOSTICO QUE ESTOY POR LEER NO EXISTA!
        if (pronosticos.stream().anyMatch(p -> p.getNombreParticipante().equals(finalLine[0])
                && p.getPartido().getEquipoLocal().getNombre().equals(finalLine[1])
                && p.getPartido().getEquipoVisitante().getNombre().equals(finalLine[5]))) {
            throw new Exception("El pronostico " + TextFormat.colors.red + finalLine[1] + TextFormat.colors.reset
                    + " vs. " + TextFormat.colors.red + finalLine[5] + TextFormat.colors.reset
                    + " hecho por " + TextFormat.colors.red + finalLine[0] + TextFormat.colors.reset + " ya existe.");
        }
    }
    // METODO AUXILIAR PARA DETERMINAR SI EL ENUMERADOR USADO EN EL ARCHIVO DE PRONOSTICOS ES VALIDO, SOLO DEBE SER UNA X MAYUSCULA O MINUSCULA!
    private static boolean enumeradorValido(String s0, String s1, String s2) {
        boolean enum0 = (s0.equals("x") || s0.equals("X")) && (s1.equals("")) && (s2.equals(""));
        boolean enum1 = (s0.equals("")) && (s1.equals("x") || s1.equals("X")) && (s2.equals(""));
        boolean enum2 = (s0.equals("")) && (s1.equals("")) && (s2.equals("x") || s2.equals("X"));
        if (enum0 && !enum1 && !enum2) {
            return true;
        }
        if (!enum0 && enum1 && !enum2) {
            return true;
        }
        if (!enum0 && !enum1 && enum2) {
            return true;
        }
        return false;
    }

    public static void validacionDePartidoLeido(String[] line) throws IOException {
        // ME ASEGURO DE QUE HAYAN LLEGADO LA CANTIDAD DE CAMPOS NECESARIOS!
        if (line.length != 5) {
            throw new IOException("La cantidad de campos introducidos es incorrecta.");
        }
        // ME ASEGURO QUE LOS CAMPOS CORRESPONDIENTES A LOS NOMBRES DE LOS EQUIPOS NO ESTEN VACIOS!
        if (line[1].equals("") || line[4].equals("")) {
            throw new IOException("El campo correspondiente al nombre de uno de los equipos esta vacio.");
        }
        // ME ASEGURO QUE EL CAMPO DONDE SE ENUMERA LA RONDA NO ESTE VACIO NI CONTENGA VALORES NEGATIVOS!
        if (Integer.parseInt(line[0]) < 0) {
            throw new NumberFormatException("El campo correspondiente al numero de ronda contiene informacion invalida.");
        }
        // ME ASEGURO QUE LOS CAMPOS CORRESPONDIENTES A LOS GOLES NO ESTEN VACIOS!
        if (line[2].equals("") || line[3].equals("")) {
            throw new NumberFormatException("El campo correspondiente a los goles de un equipo no contiene un valor para leer.");
        }
        // ME ASEGURO QUE LOS CAMPOS CORRESPONDIENTES A LOS GOLES NO TENGAN VALORES NEGATIVOS!
        if ((Integer.parseInt(line[2]) < 0) || Integer.parseInt(line[3]) < 0) {
            throw new NumberFormatException("El campo correspondiente a los goles de un equipo contiene un valor negativo.");
        }
    }
}
