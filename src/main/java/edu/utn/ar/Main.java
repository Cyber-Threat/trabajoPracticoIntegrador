package edu.utn.ar;
import edu.utn.ar.Equipo;
import com.opencsv.CSVReader;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Main {
    // CLASE IRRELEVANTE, PURAMENTE ESTETICA!
    private static class colores {
        private static final String ANSI_RESET = "\u001B[0m";
        private static final String ANSI_RED = "\u001B[31m";
        private static final String ANSI_GREEN = "\u001B[32m";
        private static final String ANSI_YELLOW = "\u001B[33m";
        private static final String ANSI_BLUE = "\u001B[34m";
        private static final String ANSI_PURPLE = "\u001B[35m";
        private static final String ANSI_CYAN = "\u001B[36m";
        private static final String ANSI_WHITE = "\u001B[37m";
    }
    // CLASE IRRELEVANTE, PURAMENTE ESTETICA!
    private static class icono {
        private static final String error = "\t[" + colores.ANSI_RED + "e" + colores.ANSI_RESET + "] ";
        private static final String info = "\t[" + colores.ANSI_BLUE + "i" + colores.ANSI_RESET + "] ";
        private static final String success = "\t[" + colores.ANSI_GREEN + "s" + colores.ANSI_RESET + "] ";
        private static final String help = "\t[" + colores.ANSI_PURPLE + "h" + colores.ANSI_RESET + "] ";
        private static final String warning = "\t[" + colores.ANSI_YELLOW + "w" + colores.ANSI_RESET + "] ";
    }
    // CLASE IRRELEVANTE, PURAMENTE ESTETICA!
    private static class textoEnConsola{
        static String rutaEspecificadaEsDirectorio = icono.error + "La ruta especificada es un directorio, no un archivo: ";
        static String archivoNoExistente = icono.error + "Archivo inexistente: ";
        static String archivoCorrectamenteIngresado = icono.success + "Archivo correctamente ingresado: ";
        static String help = icono.help + "Introduzca como primer y unico parametro <help> o <h> para mostrar texto informativo respecto al uso del programa.";
        static String lecturaDeDatos = icono.success + "Lectura de los datos!";
        public static String ArchivoRecibido = icono.success + "Archivo recibido: ";
    }
    // MI LECTOR PARA ARCHIVOS CSV
    public static void main(String[] args) throws IOException, CsvValidationException {
        if (args.length == 1) {
            if (args[0].charAt(0) == 'h' || args[0].equals("help")) {
                helpBanner();
                return;
            }
        }
        if (args.length == 2){
            boolean rutaValidaDeResultadosCSV = true;
            boolean rutaValidaDePronosticosCSV = true;
            boolean archivoValidoDeResultados = true;
            boolean archivoValidoDePronosticos = true;
            // GUARDADO DE LOS DATOS INTRODUCIDOS POR CONSOLA!
            Path rutaDeArchivoPronosticos = Paths.get(args[0]).toAbsolutePath();
            Path rutaDeArchivoResultados = Paths.get(args[1]).toAbsolutePath();
            // VALIDACION DE LOS DATOS INTRODUCIDOS POR CONSOLA!
            if (Files.isDirectory(rutaDeArchivoPronosticos)) { rutaValidaDePronosticosCSV = false; }
            if (Files.isDirectory(rutaDeArchivoResultados)) { rutaValidaDeResultadosCSV = false; }
            // INFORMO DEL ERROR EN CUESTION!
            if (!rutaValidaDePronosticosCSV || !rutaValidaDeResultadosCSV){
                if(!rutaValidaDePronosticosCSV){ System.out.println(textoEnConsola.rutaEspecificadaEsDirectorio + rutaDeArchivoPronosticos); }
                if(!rutaValidaDeResultadosCSV){ System.out.println(textoEnConsola.rutaEspecificadaEsDirectorio + rutaDeArchivoResultados); }
                return;
            }
            // AHORA TENGO QUE COMPROBAR QUE LOS ARCHIVOS EXISTAN, A PESAR DE HABER COMPROBADO QUE LAS RUTAS SON VALIDAS!
            File pronosticosArchivoCSV = new File(rutaDeArchivoPronosticos.toUri());
            File resultadosArchivoCSV = new File(rutaDeArchivoResultados.toUri());
            if(!pronosticosArchivoCSV.exists()){ archivoValidoDePronosticos = false; }
            if(!resultadosArchivoCSV.exists()){ archivoValidoDeResultados = false; }
            // INFORMO DEL ERROR EN CUESTION!
            if (!archivoValidoDePronosticos || !archivoValidoDeResultados){
                if(!archivoValidoDePronosticos){ System.out.println(textoEnConsola.archivoNoExistente + rutaDeArchivoPronosticos); }
                if(!archivoValidoDeResultados){ System.out.println(textoEnConsola.archivoNoExistente + rutaDeArchivoResultados); }
                return;
            }
            // IMPRESION EN PANTALLA DE LOS DATOS VALIDOS!
            System.out.println(textoEnConsola.archivoCorrectamenteIngresado + colores.ANSI_GREEN + rutaDeArchivoPronosticos.getFileName() + colores.ANSI_RESET);
            System.out.println(textoEnConsola.archivoCorrectamenteIngresado + colores.ANSI_GREEN + rutaDeArchivoResultados.getFileName() + colores.ANSI_RESET);
            // AHORA PROCEDO CON ANALIZAR UNO DE LOS DOS ARCHIVOS!
            leerArchivoCSV(resultadosArchivoCSV);
        } else {
            System.out.println(icono.error + "Parametro(s) invalido(s).");
            System.out.println(icono.help + "Introduzca <help> o <h> como unico y primer parametro adicional para ver el mensaje de ayuda disponible.");
        }
        // FIN DEL METODO MAIN
    }
    // METODO USADO PARA LEER LOS ARCHIVOS!
    private static void leerArchivoCSV(File ArchivoCSV) throws IOException, CsvValidationException {
        System.out.println(textoEnConsola.lecturaDeDatos);
        System.out.println(textoEnConsola.ArchivoRecibido + colores.ANSI_GREEN + ArchivoCSV.toPath().getFileName() + colores.ANSI_RESET);
        CSVReader reader = new CSVReaderBuilder(new FileReader(ArchivoCSV)).withCSVParser(new CSVParserBuilder().withSeparator(';').build()).build();
        List <Equipo> Equipos;
        String[] line;
        int lineNumber = 0;
        while((line = reader.readNext()) != null){
            System.out.print("\t[" + colores.ANSI_BLUE + "Linea " + lineNumber + colores.ANSI_RESET + "] Equipo local: " + line[0] + ", goles: " + line[1]);
            System.out.print(" [" + colores.ANSI_BLUE + lineNumber + colores.ANSI_RESET + "] Equipo visitante: " + line[2] + ", goles: " + line[3] + "\n");
            lineNumber++;
        }
    }
    // BANNER DE AYUDA SI SE INTRODUCE EL COMANDO <help> Ó <h>
    private static void helpBanner() {
        System.out.println(icono.help + "Mensaje de ayuda:");
        System.out.println(icono.help + "\tEl programa esta pensado para leer dos archivos llamados \"pronosticos.csv\" y \"resultados.csv\".");
        System.out.println(icono.help + "\tDichos archivos tienen que ser especificados como parametros");
        System.out.println(icono.help + "\tal momento de ejecutar el programa desde la consola de comandos.\"");
        System.out.println(icono.help + "\tLa sintaxis es la siguiente, primero la ruta del archivo de los pronosticos dados por los participantes, luego");
        System.out.println(icono.help + "\tla ruta del archivo donde estan escritos los resultados de los partidos:");
        System.out.println(icono.help + colores.ANSI_GREEN + "\tjava .\\Main.java <rutaDelArchivoPronosticosCSV> <rutaDelArchivoResultadosCSV>" + colores.ANSI_RESET);
        System.out.println(icono.help + "\tUna vez especificadas las rutas a los archivos necesarios el programa puede interpretar su contenido");
        System.out.println(icono.help + "\ty opcionalmente volcando en un archivo \"puntuaciones.csv\" los participantes y sus respectivas puntuaciones, asi");
        System.out.println(icono.help + "\tcomo tambien el ganador con los mejores pronosticos para los partidos dados.");
    }
}