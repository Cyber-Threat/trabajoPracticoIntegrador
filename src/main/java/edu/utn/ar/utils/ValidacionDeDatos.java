package edu.utn.ar.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public abstract class ValidacionDeDatos {
    public static boolean rutaEspecificadaEsDirectorio(Path ... paths) {
        boolean validezDeLaRuta = false;
        // VALIDACION DE LOS DATOS INTRODUCIDOS POR CONSOLA! SI LAS RUTAS INTRODUCIDAS SON SOLO DIRECTORIOS NO SIGO CON EL PROGRAMA Y SE LO INFORMO AL USUARIO!
        for (Path p : paths) {
            if (p != null && Files.isDirectory(p)) {
                System.out.println(TextFormat.icons.error + "Error. La ruta especificada es directorio: " + TextFormat.colors.red + p + TextFormat.colors.reset);
                validezDeLaRuta = true;
            }
        }
        return validezDeLaRuta;
    }
    public static boolean rutaEspecificadaEsDirectorio(boolean verbosity, Path ... paths) {
        boolean validezDeLaRuta = false;
        // VALIDACION DE LOS DATOS INTRODUCIDOS POR CONSOLA! SI LAS RUTAS INTRODUCIDAS SON SOLO DIRECTORIOS NO SIGO CON EL PROGRAMA Y SE LO INFORMO AL USUARIO!
        for (Path p : paths) {
            if (p != null && Files.isDirectory(p)) {
                if (verbosity) { System.out.println(TextFormat.icons.error + "Error. La ruta especificada es directorio: " + TextFormat.colors.red + p + TextFormat.colors.reset); }
                validezDeLaRuta = true;
            }
        }
        return validezDeLaRuta;
    }
    public static boolean losArchivosNoExisten(Path ... paths) {
        boolean validezDelArchivo = false;
        for (Path p: paths){
            if (p != null && !p.toFile().exists()){
                System.out.println(TextFormat.icons.error + "Error. El archivo especificado no existe: " + TextFormat.colors.red + p + TextFormat.colors.reset);
                validezDelArchivo = true;
            }
        }
        return validezDelArchivo;
    }
    public static boolean losArchivosNoExisten(boolean verb, Path ... paths) {
        boolean validezDelArchivo = false;
        for (Path p: paths){
            if (p != null && !p.toFile().exists()){
                if (verb){ System.out.println(TextFormat.icons.error + "Error. El archivo especificado no existe: " + TextFormat.colors.red + p + TextFormat.colors.reset); }
                validezDelArchivo = true;
            }
        }
        return validezDelArchivo;
    }
    public static void validacionDelPronosticoLeido(String[] line) throws Exception {
        if (line.length != 7) {
            throw new Exception("La cantidad de campos introducidos es incorrecta!");
        }
        // ME ASEGURO QUE EXISTA EL NOMBRE DEL PARTICIPANTE
        if (line[0].equals("")) {
            throw new Exception("El campo correspondiente al nombre del participante esta vacio!");
        }
        if (!line[0].matches("[a-zA-Z ]+")) { //  EXPRESION REGULAR PARA DESCRIBIR UNA CADENA DE TEXTO QUE SOLO CONTIENE LETRAS MAYUSCULAS, MINUSCULAS O ESPACIOS PARA EL NOMBRE DEL PARTICIPANTE
            throw new Exception("El campo correspondiente al nombre del participante contiene informacion invalida! Solo se admiten caracteres alfabeticos.");
        }
        if (line[1].equals("")){
            throw new Exception("El campo correspondiente al numero de DNI/Pasaporte esta vacio!");
        }
        if (!line[1].matches("\\d+")) { // EXPRESION REGULAR PARA DESCRIBIR UNA CADENA DE TEXTO QUE TIENE SOLO CARACTERES ALFANUMERICOS PARA EL NUMERO DE DNI/PASAPORTE DEL PARTICIPANTE
            throw new Exception("El campo correspondiente al numero de DNI/Pasaporte del participante contiene informacion invalida! Solo se admiten numeros.");
        }
        // ME ASEGURO QUE SE HAYA INTRODUCIDO EL NOMBRE DE LOS EQUIPOS
        if (line[2].equals("") || line[6].equals("")) {
            throw new Exception("El campo correspondiente a uno de los equipos esta vacio!");
        }
        if (!line[2].matches("[a-zA-Z ]+") && !line[6].matches("[a-zA-Z ]+")) {
            throw new Exception("El campo correspondiente a uno de los equipos contiene informacion invalida! Solo se admiten caracteres alfabeticos.");
        }
        // ME ASEGURO QUE EXISTA EL VALOR QUE DICTA EL RESULTADO DEL PARTIDO
        if (line[3].equals("") && line[4].equals("") && line[5].equals("")) {
            throw new Exception("Los campos que pronostican el resultado del partido están vacios!");
        }
        // ENUMERADOR VALIDO (SOLO DEBE SER UNA "X" MAYUSCULA O MINUSCULA!
        if (!enumeradorValido(line[3], line[4], line[5])) {
            throw new Exception("El enumerador usado en los campos donde se pronostica el resultado del partido, es invalido!");
        }
    }

    // METODO AUXILIAR PARA DETERMINAR SI EL ENUMERADOR USADO EN EL ARCHIVO DE PRONOSTICOS ES VALIDO, SOLO DEBE SER UNA X MAYUSCULA O MINUSCULA!
    private static boolean enumeradorValido(String s0, String s1, String s2) {
        boolean enum0 = (s0.equals("x") || s0.equals("X")) && (s1.equals("")) && (s2.equals(""));
        boolean enum1 = (s0.equals("")) && (s1.equals("x") || s1.equals("X")) && (s2.equals(""));
        boolean enum2 = (s0.equals("")) && (s1.equals("")) && (s2.equals("x") || s2.equals("X"));
        return enum0 || enum1 || enum2;
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
    public static boolean existePronostico(List<Pronostico> subListaPronosticos, String idUnivocaDelParticipante, String equipoLocal, String equipoVisitante) {
        return subListaPronosticos.stream().anyMatch(p -> p.getObjParticipante().getIdentificacionUnivoca() == Integer.parseInt(idUnivocaDelParticipante)
                && p.getPartido().getEquipoLocal().getNombre().equals(equipoLocal)
                && p.getPartido().getEquipoVisitante().getNombre().equals(equipoVisitante));
    }
    public static Pronostico buscarRetornarPronosticoExistente(List<Pronostico> subListaPronosticos, String idUnivocaDelParticipante, String equipoLocal, String equipoVisitante){
        return subListaPronosticos.stream().filter(p -> p.getObjParticipante().getIdentificacionUnivoca() == Integer.parseInt(idUnivocaDelParticipante)
                && p.getPartido().getEquipoLocal().getNombre().equals(equipoLocal)
                && p.getPartido().getEquipoVisitante().getNombre().equals(equipoVisitante)).findFirst().get();
    }
    public static boolean existePartido(List<Partido> subListaPartidos, String nombreEquipoLocal, String nombreEquipoVisitante){
        return subListaPartidos.stream().anyMatch(p -> p.getEquipoLocal().getNombre().equals(nombreEquipoLocal) && p.getEquipoVisitante().getNombre().equals(nombreEquipoVisitante));
    }
    public static Partido buscarRetornarPartidoExistente(List<Partido> subListaPartidos, String equipoLocal, String equipoVisitante) {
        return subListaPartidos.stream().filter(p -> p.getEquipoLocal().getNombre().equals(equipoLocal) && p.getEquipoVisitante().getNombre().equals(equipoVisitante)).findFirst().orElse(null);
    }
    public static boolean existeParticipante(List<Participante> subListaParticipantes, Integer dni){
        return subListaParticipantes.stream().anyMatch(p -> Objects.equals(p.getIdentificacionUnivoca(), dni));
    }
    public static Participante buscarRetornarParticipanteYaExistente(List<Participante> subListaParticipantes, Integer dniParticipante) {
        return subListaParticipantes.stream().filter(p -> Objects.equals(p.getIdentificacionUnivoca(), dniParticipante)).findFirst().orElse(null);
    }
    public static boolean existeEquipo(List<Equipo> subListaEquipos,String nombreEquipo) {
        return subListaEquipos.stream().anyMatch(equipo -> equipo.getNombre().equals(nombreEquipo));
    }
    public static Equipo buscarRetornarEquipoExistente(List<Equipo> subListaEquipos,String nombreEquipo) {
        return subListaEquipos.stream().filter(e -> e.getNombre().equals(nombreEquipo)).findFirst().orElse(null);
    }
}
