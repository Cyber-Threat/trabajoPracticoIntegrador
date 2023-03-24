package edu.utn.ar;
import com.opencsv.CSVReader;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException, CsvValidationException {
        if (args.length == 1 && (args[0].charAt(0) == 'h' || args[0].equals("help"))) { helpBanner(); return; }
        if (args.length != 2) {
            System.out.println("\t[e] Parametro(s) invalido(s).");
            System.out.println("\t[h] Introduzca <help> o <h> como unico y primer parametro adicional para ver el mensaje de ayuda disponible.");
        }
        // TABLA DE VALORES BOOLEANOS AUXILIARES PARA CONTROLAR EL FLUJO DEL PROGRAMA EN FUNCION DE LA VALIDEZ DE LOS ARGUMENTOS PASADOS COMO PARAMETROS AL METODO MAIN
        boolean rutaValidaDeResultadosCSV = true;
        boolean rutaValidaDePronosticosCSV = true;
        boolean archivoValidoDeResultados = true;
        boolean archivoValidoDePronosticos = true;
        // GUARDADO DE LOS DATOS INTRODUCIDOS POR CONSOLA!
        Path rutaDeArchivoPronosticos = Paths.get(args[0]).toAbsolutePath();
        Path rutaDeArchivoResultados = Paths.get(args[1]).toAbsolutePath();
        // VALIDACION DE LOS DATOS INTRODUCIDOS POR CONSOLA! SI LAS RUTAS INTRODUCIDAS SON SOLO DIRECTORIOS NO SIGO CON EL PROGRAMA Y SE LO INFORMO AL USUARIO!
        if (Files.isDirectory(rutaDeArchivoPronosticos)) { rutaValidaDePronosticosCSV = false; }
        if (Files.isDirectory(rutaDeArchivoResultados)) { rutaValidaDeResultadosCSV = false; }
        // INFORMO DEL ERROR EN CUESTION!
        if (!rutaValidaDePronosticosCSV || !rutaValidaDeResultadosCSV){
            if(!rutaValidaDePronosticosCSV){ System.out.println("\t[e] Error. La ruta especificada es directorio: " + rutaDeArchivoPronosticos); }
            if(!rutaValidaDeResultadosCSV){ System.out.println("\t[e] Error. La ruta especificada es directorio: " + rutaDeArchivoResultados); }
            return;
        }
        // AHORA TENGO QUE COMPROBAR QUE LOS ARCHIVOS EXISTAN, A PESAR DE HABER COMPROBADO QUE LAS RUTAS SON VALIDAS!
        File pronosticosArchivoCSV = new File(rutaDeArchivoPronosticos.toUri());
        File resultadosArchivoCSV = new File(rutaDeArchivoResultados.toUri());
        if(!pronosticosArchivoCSV.exists()){ archivoValidoDePronosticos = false; }
        if(!resultadosArchivoCSV.exists()){ archivoValidoDeResultados = false; }
        // INFORMO DEL ERROR EN CUESTION!
        if (!archivoValidoDePronosticos || !archivoValidoDeResultados){
            if(!archivoValidoDePronosticos){ System.out.println("\t[e] El archivo especificado no existe: " + rutaDeArchivoPronosticos); }
            if(!archivoValidoDeResultados){ System.out.println("\t[e] El archivo especificado no existe: " + rutaDeArchivoResultados); }
            return;
        }
        // IMPRESION EN PANTALLA DE LOS DATOS VALIDOS!
        System.out.println("\t[s] Archivo ingresado correctamente: " + rutaDeArchivoPronosticos.getFileName());
        System.out.println("\t[s] Archivo ingresado correctamente: " + rutaDeArchivoResultados.getFileName());
        // AHORA PROCEDO CON ANALIZAR UNO DE LOS DOS ARCHIVOS!
        ArrayList<Partido> partidos = leerArchivoPartidosCSV(resultadosArchivoCSV);
        ArrayList<Pronostico> pronosticos  = leerArchivoPronosticosCSV(pronosticosArchivoCSV, partidos);
        // AHORA PROCEDO A COMPARAR LAS DOS LISTAS PARA DETERMINAR EL GANADOR POR PUNTOS!
        ArrayList<Participante> participantes = compararArrayLists(partidos, pronosticos);
        // FIN DEL METODO MAIN
    }
    // METODO USADO PARA LEER EL ARCHIVO PARTIDOS.CSV!
    private static ArrayList<Partido> leerArchivoPartidosCSV(File ArchivoCSV) throws IOException, CsvValidationException {
        System.out.println("\t[s] Lectura de archivo.");
        System.out.println("\t[i] Archivo recibido: " + ArchivoCSV.toPath().getFileName());
        ArrayList<Partido> partidos = new ArrayList<>();
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(ArchivoCSV)).withCSVParser(new CSVParserBuilder().withSeparator(';').build()).build()) {
            String[] line;
            int lineNumber = 0;
            while ((line = reader.readNext()) != null) {
                // CARGAR LA LISTA
                partidos.add(lineNumber, new Partido(
                        new Equipo(line[0]),        // EQUIPO LOCAL
                        new Equipo(line[3])         // EQUIPO VISITANTE
                ));
                partidos.get(lineNumber).setGolesDelLocal(Integer.parseInt(line[1]));
                partidos.get(lineNumber).setGolesDelVisitante(Integer.parseInt(line[2]));
                // IMPRIMIR LOS ELEMENTOS
                System.out.println("\t[i] Elemento: " + lineNumber
                                      + " Equipo local: " + partidos.get(lineNumber).getEquipoLocal().getNombre()
                                      + " goles del equipo local: " + partidos.get(lineNumber).getGolesDelLocal()
                                      + " goles del equipo visistante: " + partidos.get(lineNumber).getGolesDelVisitante()
                                      + " Equipo visitante: " + partidos.get(lineNumber).getEquipoVisitante().getNombre());
                // CAMBIO DE INDICE!
                lineNumber++;
            }
            System.out.println("\t[i] Partidos jugados: " + partidos.size());
            return partidos;
        }
    }
    // METODO USADO PARA LEER EL ARCHIVO PRONOSTICOS.CSV
    private static ArrayList<Pronostico> leerArchivoPronosticosCSV(File ArchivoCSV, ArrayList<Partido> partidos) throws IOException, CsvValidationException{
        System.out.println("\t[s] Lectura de archivo.");
        System.out.println("\t[i] Archivo recibido: " + ArchivoCSV.toPath().getFileName());
        ArrayList<Pronostico> pronosticos = new ArrayList<>();
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(ArchivoCSV)).withCSVParser(new CSVParserBuilder().withSeparator(';').build()).build()) {
            String[] line;
            int lineNumber = 0;
            while ((line = reader.readNext()) != null) {
                String nombreParticipanteBuffer = line[0];
                Equipo equipoLocalBuffer = new Equipo(line[1]);
                Equipo equipoVisitanteBuffer = new Equipo(line[5]);
                ResultadoEnum resultadoBuffer = null;
                if (line[2].equals("x")) resultadoBuffer = ResultadoEnum.GANADOR;
                if (line[3].equals("x")) resultadoBuffer = ResultadoEnum.EMPATE;
                if (line[4].equals("x")) resultadoBuffer = ResultadoEnum.PERDEDOR;
                Partido partidoBuffer = new Partido(equipoLocalBuffer, equipoVisitanteBuffer);
                pronosticos.add(lineNumber, new Pronostico(nombreParticipanteBuffer, partidoBuffer, equipoLocalBuffer, resultadoBuffer)); // AÑADO LOS ELEMENTOS NECESARIOS AL PRONOSTICO!
                System.out.printf("\t[i] Pronostico: " + lineNumber
                        + " Participante: " + line[0]
                        + " Equipo local: " + line[1]
                        + " Pronostico para el equipo local: " + resultadoBuffer
                        + " Equipo visitante: " + line[5] + "\n");
                lineNumber++;
            }
        }
        return pronosticos;
    }
    // METODO USADOS PARA COMPARAR LAS LISTAS OBTENIDAS A PARTIR DE LOS ARCHIVOS
    private static ArrayList<Participante> compararArrayLists(ArrayList<Partido> listaPartidos, ArrayList<Pronostico> listaPronosticos){
        ArrayList<Participante> participantes = null;
        int indicePronosticos = 0;
        int indicePartidos = 0;
        do{ // VOY A ITERAR POR LA LISTA DE PRONOSTICOS > INSTANCIO UN OBJETO PARTICIPANTE SI ES QUE NO ESTÁ INSTANCIADO EN LA LISTA "PARTICPANTES"
            System.out.println("\t[i] Participante: " + listaPronosticos.get(indicePronosticos).getNombreParticipante()
                    + "; Equipo local de su pronostico: " + listaPronosticos.get(indicePronosticos).getEquipo().getNombre()
                    + "; Resultado esperado: " + listaPronosticos.get(indicePronosticos).getResultado()
                    + "\n\tPronosticos exitosos: ");

            do{ // AHORA INTENTO EVALUAR SI SUS PRONOSTICOS FUERON ACERTADOS ITERANDO POR LA LISTA DE PARTIDOS! SI FUE ACERTADO SUMO PUNTOS A TAL PARTICIPANTE, DE LO CONTRARIO SIGO BUSCANDO

                indicePartidos++;
            }while(indicePartidos < listaPartidos.size());
            indicePronosticos++;
        }while(indicePronosticos < listaPronosticos.size());
        return participantes;
    }
    // BANNER DE AYUDA SI SE INTRODUCE EL COMANDO <help> Ó <h>
    private static void helpBanner() {
        System.out.println("\t[h] Mensaje de ayuda:");
        System.out.println("\t\tEl programa esta pensado para leer dos archivos llamados \"pronosticos.csv\" y \"resultados.csv\".");
        System.out.println("\t\tDichos archivos tienen que ser especificados como parametros");
        System.out.println("\t\tal momento de ejecutar el programa desde la consola de comandos.\"");
        System.out.println("\t\tLa sintaxis es la siguiente, primero la ruta del archivo de los pronosticos dados por los participantes, luego");
        System.out.println("\t\tla ruta del archivo donde estan escritos los resultados de los partidos:");
        System.out.println("\t\tjava .\\Main.java <rutaDelArchivoPronosticosCSV> <rutaDelArchivoResultadosCSV>");
        System.out.println("\t\tUna vez especificadas las rutas a los archivos necesarios el programa puede interpretar su contenido");
        System.out.println("\t\ty opcionalmente volcando en un archivo \"puntuaciones.csv\" los participantes y sus respectivas puntuaciones, asi");
        System.out.println("\t\tcomo tambien el ganador con los mejores pronosticos para los partidos dados.");
    }
}