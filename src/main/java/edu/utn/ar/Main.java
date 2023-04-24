package edu.utn.ar;
// IMPORTACION DE DEPENDENCIAS!
import org.apache.commons.cli.*;
import com.opencsv.CSVReader;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import edu.utn.ar.utils.*;
import org.apache.commons.collections.ArrayStack;
// IMPORTACION DE LIBRERIAS BUILT-IN!
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    private static String parametroArchivoPronosticos;
    private static String parametroArchivoResultados;
    private static String parametroArchivoConfig;
    private static Path rutaDeArchivoConfig;
    private static Path rutaDeArchivoPronosticos;
    private static Path rutaDeArchivoResultados;
    private static File pronosticosArchivoCSV;
    private static File resultadosArchivoCSV;
    private static File configArchivoCSV;
    private static List<Equipo> equipos = new ArrayList<>();
    private static List<Partido> partidos = new ArrayList<>();
    // VOY TRACKEANDO LAS RONDAS DE PARTIDOS QUE SE VAN JUGANDO PARA POSTERIORMENTE USAR ESTE DATO PARA ARMAR EL OBJETO RONDAS
    private static List<Integer> rondasBuffer = new ArrayList<>();
    // LA LISTA DE RONDAS QUE SE VAN JUGANDO PERO CONTENIENDO SUS RESPECTIVOS PARTIDOS!
    private static List<Ronda> rondas = new ArrayList<>();
    // private static List<Fases> fases = new ArrayList<>();
    private static List<Pronostico> pronosticos = new ArrayList<>();
    private static List<Participante> participantes = new ArrayList<>();
    private static LectorDeArchivosCSV lector;
    public static void main(String[] args) throws Exception {
        Arrays.stream(args).forEach(System.out::println);
        // DEFINICION DE LOS PARAMETROS QUE EL PROGRAMA PUEDE O NO RECIBIR!
        Options options = new Options();
        options = construccionDeParametros(options); // METODO AUXILIAR PARA CONSTRUIR EL OBJETO TIPO "Options" Y AÑADIR A ESTE LOS PARAMETROS QUE EL PROGRAMA TIENE DISPONIBLE
        CommandLineParser parser = new DefaultParser();
        CommandLine parametrosPasados;
        try {
            parametrosPasados = parser.parse(options, args);
            validacionDeRutasPasadasPorParametro(parametrosPasados);
            lector = new LectorDeArchivosCSV(resultadosArchivoCSV, pronosticosArchivoCSV, configArchivoCSV);
            lector.ejecutar();
            if (parametrosPasados.getOptionValue("d").equals("local")) {  // SI EL USUARIO DISPONE DE UN ARCHIVO CSV SE USA ESTE MISMO.
                System.out.println(TextFormat.icons.info + "Cargando listas localmente ...");
                cargarListasLocalmente(lector);
                if (equipos != null && partidos != null && rondas != null) System.out.println(TextFormat.icons.success + "Se ha cargado con exito la lista de equipos, partidos y rondas.");
                pronosticos = lector.getPronosticos(); // SI SE DESEA USAR LOS PRONOSTICOS DEL ARCHIVO ESPECIFICADO
                participantes = lector.getParticipantes();
                if (pronosticos != null & participantes != null) System.out.println(TextFormat.icons.success + "Se ha cargado con exito la lista de pronosticos.");
            }
            if (parametrosPasados.getOptionValue("d").equals("remota")){  // SI EL USUARIO DISPONE DE UNA BASE DE DATOS SE INTENTA ACCEDER, ESTO VA A IGNORAR EL ARCHIVO DE PRONOSTICOS SI SE ESPECIFICA!
                if (!parametrosPasados.getOptionValue("p").equals("")){ // SI EL USUARIO ESPECIFICA UN ARCHIVO CSV PERO EL PARAMETRO -D = REMOTA ENTONCES SE ADVIERTE EL PASO POR ALTO DEL ARCHIVO CSV!
                    System.out.println(TextFormat.icons.warning + "Advertencia, el parametro \"" + TextFormat.colors.yellow + "-p <rutaDeArchivoPronosticosCSV>" + TextFormat.colors.reset + "\" ha sido ignorado porque se dispone una base de datos.");
                    System.out.println(TextFormat.icons.warning + "Se va a hacer uso del archivo en caso de que el programa no pueda conectarse a la base de datos.");
                }
                cargarListasLocalmente(lector);
                if (equipos != null && partidos != null && rondas != null) System.out.println(TextFormat.icons.success + "Se ha cargado con exito la lista de equipos, partidos y rondas.");
                try {
                    System.out.println(TextFormat.icons.info + "Buscando contenido de la base de datos...");
                    instanciarPronosticos(); // USO EL METODO QUE ACCEDE A LA BASE DE DATOS ENTONCES NO NECESITA EL ARCHIVO CSV COMO PARAMETRO!
                    System.out.println(TextFormat.icons.success + "Se ha cargado con exito la lista de pronosticos.");
                } catch (ClassNotFoundException cnfe){ // SI LANZA EXCEPCION ENTONCES INTENTO CARGAR LA LISTA DE PRONOSTICOS CON EL ARCHIVO CSV DADO PARA LOS PRONOSTICOS!
                    TextFormat.informarError(cnfe);
                    System.out.println(TextFormat.icons.error + "No se va a usar la base de datos.");
                    if (parametroArchivoPronosticos != null){
                        System.out.println(TextFormat.icons.info + "Se detecto un archivo dado para los pronosticos.");
                        pronosticos = lector.getPronosticos();
                        if (pronosticos != null) System.out.println(TextFormat.icons.success + "Se ha cargado con exito la lista de pronosticos.");
                    }
                }
                System.out.println(TextFormat.icons.info + "Tamaño de la lista de participantes: " + TextFormat.colors.blue + participantes.size() + TextFormat.colors.reset);
                System.out.println(TextFormat.icons.info + "Tamaño de la lista de pronosticos: " + TextFormat.colors.blue + pronosticos.size() + TextFormat.colors.reset);
            }
            participantes = cargarPuntosSegunLosPronosticosAcertados();
            Collections.sort(participantes, (p1, p2) -> Float.compare(p2.getPuntosAcumulados(), p1.getPuntosAcumulados()));
            TextFormat.imprimirTablaDePuntuaciones(participantes);
            if (pronosticos == null) { throw new Exception("Algo salio mal durante la carga de la correspondiente a los pronosticos."); }
        // FIN DEL METODO MAIN
        } catch (Exception e) {
            TextFormat.informarError(e);
            TextFormat.helpBanner();
            return;
        }
    }
    //  METODO USADO PARA CONSTRUIR EL OBJETO OPTIONS Y CREAR LOS PARAMETROS QUE EL PROGRAMA TIENE DISPONIBLE!
    private static Options construccionDeParametros(Options options) {
        // CONJUNTO DE PARAMETROS OBLIGATORIOS!
        Option archivoDeEntradaResultados = new Option("r", "resultados", true, ""); // PARAMETRO PARA ESPECIFICAR LA RUTA DEL ARCHIVO RESULTADOS!
        archivoDeEntradaResultados.setRequired(true);
        options.addOption(archivoDeEntradaResultados);
        Option tipoDeConexion = new Option("d", "database", true, ""); // PARAMETRO PARA SABER SI HAY QUE CONECTARSE A UNA DB O NO PARA OBTENER LOS PRONOSTICOS!
        tipoDeConexion.setRequired(true);
        options.addOption(tipoDeConexion);
        Option archivoConfiguracion = new Option("c", "config", true, ""); // PARAMETRO PARA SABER DONDE ESTA EL ARCHIVO DE CONFIGURACION!
        archivoConfiguracion.setRequired(true);
        options.addOption(archivoConfiguracion);
        // PARAMETROS OPCIONALES!
        Option archivoDeEntradaPronosticos = new Option("p", "pronosticos", true, ""); // PARAMETRO PARA ESPECIFICAR LA RUTA DEL ARCHIVO PRONOSTICOS!
        archivoDeEntradaPronosticos.setRequired(false);
        options.addOption(archivoDeEntradaPronosticos);
        Option bannerDeAyuda = new Option("h", "help", false, ""); // PARAMETRO POR SI EL USUARIO NECESITA UN MENSAJE DE AYUDA.
        bannerDeAyuda.setRequired(false);
        options.addOption(bannerDeAyuda);
        // RETORNO EL OBJETO MODIFICADO!
        return options;
    }
    // CARGADO DE LAS LISTAS equipos, partidos y rondas LOCALMENTE!

    private static void validacionDeRutasPasadasPorParametro(CommandLine parametrosPasados) throws Exception {
        parametroArchivoResultados = (parametrosPasados.hasOption("r")) ? parametrosPasados.getOptionValue("r") : null;
        parametroArchivoConfig = (parametrosPasados.hasOption("c")) ? parametrosPasados.getOptionValue("c") : null;
        parametroArchivoPronosticos = (parametrosPasados.hasOption("p")) ? parametrosPasados.getOptionValue("p") : null;
        rutaDeArchivoResultados = (parametroArchivoResultados != null) ? Paths.get(parametroArchivoResultados).toAbsolutePath() : null;
        rutaDeArchivoConfig = (parametroArchivoConfig != null) ? Paths.get(parametroArchivoConfig).toAbsolutePath() : null;
        rutaDeArchivoPronosticos = (parametroArchivoPronosticos != null) ? Paths.get(parametroArchivoPronosticos).toAbsolutePath() : null;
        if (ValidacionDeDatos.rutaEspecificadaEsDirectorio(true, rutaDeArchivoResultados, rutaDeArchivoConfig, rutaDeArchivoPronosticos)){
            throw new Exception("Los parametros mencionados son directorios y no archivos.");
        }
        if (ValidacionDeDatos.losArchivosNoExisten(true, rutaDeArchivoResultados, rutaDeArchivoConfig, rutaDeArchivoPronosticos)) {
            throw new Exception("Los archivos mencionados no existen.");
        }
        resultadosArchivoCSV = new File(rutaDeArchivoResultados.toUri());
        configArchivoCSV = new File(rutaDeArchivoConfig.toUri());
        pronosticosArchivoCSV = new File(rutaDeArchivoPronosticos.toUri());
    }
    private static void cargarListasLocalmente(LectorDeArchivosCSV lector) throws Exception {
        try {
            // AHORA PROCEDO CON ANALIZAR EL ARCHIVO DE RESULTADOS.CSV SOLO PARA CARGAR LA LISTA DE EQUIPOS
            equipos = lector.getEquipos();
            if (equipos == null) {
                throw new Exception("Algo salio mal durante la carga de la correspondiente a los equipos.");
            }
            // AHORA PROCEDO CON ANALIZAR EL ARCHIVO DE RESULTADOS PARA CARGAR LA LISTA DE RONDAS CON LOS PARTIDOS JUGADOS REUTILIZANDO LOS OBJETOS DE TIPO EQUIPO INSTANCIADOS ANTERIORMENTE
            partidos = lector.getPartidos();
            if (partidos == null) {
                throw new Exception("Algo salio mal durante la carga de la correspondiente a los partidos.");
            }
            // AHORA QUE TENGO LOS PARTIDOS DISPONIBLES ARMO EL OBJETO RONDAS!
            rondas = lector.getRondas();
        } catch (Exception e){
            TextFormat.informarError(e);
        }
    }
    // METODO USADO PARA CARGAR LA LISTA DE PRONOSTICOS Y PARTICIPANTES USANDO UNA BASE DE DATOS!
    private static void instanciarPronosticos() throws ClassNotFoundException, SQLException {
        // AHORA TENGO QUE CREAR UNA INSTANCIA DE MYSQLCONNECTOR!
        MySqlConnector jdbcInstancia = new MySqlConnector(equipos, partidos, participantes, pronosticos);
        try {
            jdbcInstancia.setHost(lector.getHost());
            jdbcInstancia.setPort(lector.getPort());
            jdbcInstancia.setDb_name(lector.getDbName());
            jdbcInstancia.setDb_url();
            jdbcInstancia.setUser(lector.getUser());
            jdbcInstancia.setPass(lector.getPass()); // YA TERMINÉ DE CONFIGURAR EL SQLCONNECTOR! AHORA LO EJECUTO:
            jdbcInstancia.ejecutar();
            participantes = jdbcInstancia.getParticipantes();
            pronosticos = jdbcInstancia.getPronosticos();
        } catch (Exception e){
            if (e instanceof ClassNotFoundException) {
                TextFormat.informarError(e);
                throw new ClassNotFoundException(TextFormat.icons.error + TextFormat.colors.red + "Fatal. No se encontro un driver." + TextFormat.colors.reset);
            }
            TextFormat.informarError(e);
        }
    }
    // CARGO LOS PUNTOS CORRESPONDIENTES A CADA PARTICIPANTE ITERANDO SOBRE LA LISTA DE PRONOSTICOS Y EVALUANDO SI EXISTE EL PARTIDO AL CUAL SE REFIEREN!
    private static List<Participante> cargarPuntosSegunLosPronosticosAcertados() {
        int iterador = 0;
        System.out.println(TextFormat.colors.white + TextFormat.effects.bold + String.format("%1$-114s", "\n\t\t\t\t\t► ► ► PRONOSTICOS DE LOS PARTICIPANTES! ◄ ◄ ◄") + TextFormat.colors.reset);
        // ABRO UN BUCLE PARA ITERAR A LO LARGO DE LA LISTA DE PRONOSTICOS
        do {
            System.out.println(TextFormat.colors.cyan + String.format("%1$-128s", " ").replace(' ', '─') + TextFormat.colors.reset);
            // AHORA EVALUO SI HAY UN PARTIDO AL QUE SE REFIERE EL PARTICIPANTE CON ESTE PRONOSTICO Y SI EFECTIVAMENTE ES ACERTADO
            Partido pBuffer = ValidacionDeDatos.buscarRetornarPartidoExistente(partidos, pronosticos.get(iterador).getEquipoLocal().getNombre(), pronosticos.get(iterador).getEquipoVisitante().getNombre());
            // GUARDO EL PRONOSTICO EN UN BUFFER PARA USARLO MAS COMODAMENTE
            Pronostico pronosticoBuffer = pronosticos.get(iterador);
            boolean condicionDeCarga = (pronosticoBuffer.getPronosticoEquipoLocal() == pBuffer.getResultadoEquipoLocal()) && (pronosticoBuffer.getPronosticoEquipoVisitante() == pBuffer.getResultadoEquipoVisitante());
            if (condicionDeCarga){
                String nombreParticipanteBuffer = pronosticos.get(iterador).getObjParticipante().getNombre();
                participantes.stream().filter(p -> p.getNombre().equals(nombreParticipanteBuffer)).findFirst().get().adicionarPuntos(1.0f);
            }
            // IMPRIMO EL PRONOSTICO EN CUESTION:
            TextFormat.imprimirPronosticoDelParticipante(pronosticos, iterador, condicionDeCarga);
            iterador++;
        } while (iterador < pronosticos.size());
        // CIERRO EL BUCLE!
        System.out.println(TextFormat.colors.cyan + String.format("%1$-128s", " ").replace(' ', '─') + TextFormat.colors.reset);
        return participantes;
    }
}