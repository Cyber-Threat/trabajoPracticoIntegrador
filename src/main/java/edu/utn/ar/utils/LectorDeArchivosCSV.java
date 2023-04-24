package edu.utn.ar.utils;
import java.io.File;
import java.io.FileReader;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
public class LectorDeArchivosCSV {
    private static List<Equipo> equipos = new ArrayList<>();
    private static List<Partido> partidos = new ArrayList<>();
    private static final List<Integer> rondasBuffer = new ArrayList<>();
    private static List<Ronda> rondas = new ArrayList<>();
    private static final List<Participante> participantes = new ArrayList<>();
    private static List<Pronostico> pronosticos = new ArrayList<>();
    private static List<File> files = new ArrayList<>();
    private static String host;
    private static String port;
    private static String dbName;
    private static String user;
    private static String pass;
    private static int puntuacionPorPartido;
    private static int puntuacionPorRonda;
    private static int puntuacionPorFase;
    public LectorDeArchivosCSV(File ... f){
        files = Arrays.asList(f);
    }
    public static void ejecutar() throws Exception {
        for (File f: files) {
            switch (f.getName()){
                case "resultados.csv" -> {
                    instanciarEquipos(f);
                    instanciarPartidos(f);
                }
                case "pronosticos.csv" -> instanciarPronosticos(f);
                case "config.csv" -> cargarConfig(f);
            }
        }
    }

    private static void cargarConfig(File f) {
        String[] line;
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(f)).withCSVParser(new CSVParserBuilder().withSeparator(':').build()).build()){
            while ((line = reader.readNext()) != null) {
                switch (line[0]){
                    case "host" -> setHost(line[1]);
                    case "port" -> setPort(line[1]);
                    case "dbName" -> setDbName(line[1]);
                    case "user" -> setUser(line[1]);
                    case "pass" -> setPass(line[1]);
                    case "puntuacionPorPartido" -> setPuntuacionPorPartido(line[1]);
                    case "puntuacionPorRonda" -> setPuntuacionPorRonda(line[1]);
                    case "puntuacionPorFase" -> setPuntuacionPorFase(line[1]);
                    default -> throw new Exception("El archivo de configuracion tiene informacion invalida.");
                }
                System.out.println(TextFormat.icons.info + String.format("%1$-32s", line[0] + ":") + TextFormat.colors.cyan  + line[1] + TextFormat.colors.reset);
            }
        } catch (Exception e) {
            TextFormat.informarError(e);
        }
    }
    private static void instanciarEquipos(File ArchivoCSV){
        int lineNumber = 0;
        int identificador = 0;
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(ArchivoCSV)).withSkipLines(1).withCSVParser(new CSVParserBuilder().withSeparator(';').build()).build()){
            String[] line;
            while ((line = reader.readNext()) != null) {
                // ME ASEGURO QUE NO ESTEN VACIOS LOS CAMPOS CORRESPONDIENTES A LOS NOMBRES DE LOS EQUIPOS
                if (line[1].equals("") || line[4].equals("")) {
                    throw new Exception("Campo correspondiente al nombre de un equipo, esta vacio.");
                }
                // ME ASEGURO QUE NO HAYA SIDO CREADO ANTES EL EQUIPO EN CUESTION, SI NO FUE CREADO, CREO UNA INSTANCIA EN UN BUFFER
                if (equipos != null && !ValidacionDeDatos.existeEquipo(equipos, line[1])) {
                    Equipo equipoLocalBuffer = new Equipo(identificador, line[1]);
                    equipos.add(lineNumber, equipoLocalBuffer);
                    identificador++;
                }
                if (equipos != null && !ValidacionDeDatos.existeEquipo(equipos, line[4])) {
                    Equipo equipoVisitanteBuffer = new Equipo(identificador, line[4]);
                    equipos.add(lineNumber, equipoVisitanteBuffer);
                    identificador++;
                }
                // SI NO ESTAN VACIOS LOS CAMPOS CORRESPONDIENTES AL EQUIPO Y EL EQUIPO EN CUESTION NO FUE CREADO ANTES, ENTONCES LOS CARGO A LA LISTA
                lineNumber++;
            }
        } catch (Exception e) {
            TextFormat.informarError(ArchivoCSV, lineNumber, e);
            equipos = null;
        } finally {
            // SENCILLAMENTE IMPRIMO LOS ELEMENTOS EN PANTALLA PARA VER QUE ESTA BIEN LO QUE HICE!
            assert equipos != null;
            TextFormat.imprimirEquiposInstanciados(equipos);
        }
    }
    private static void instanciarPartidos(File ArchivoCSV) throws Exception {
        String[] line;
        int lineNumber = 0; // ITERADOR AUXILIAR PARA INFORMAR EN QUE LINEA DEL ARCHIVO ESTA EL PROBLEMA EN CASO QUE SUCEDA ALGO MAL
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(ArchivoCSV)).withSkipLines(1).withCSVParser(new CSVParserBuilder().withSeparator(';').build()).build()) {
            while ((line = reader.readNext()) != null) {
                ValidacionDeDatos.validacionDePartidoLeido(line);
                // TENGO QUE ASEGURARME DE NO INSTANCIAR UN EQUIPO REPETIDAS VECES!
                String[] finalLine = line;
                Equipo equipoLocalBuffer = ValidacionDeDatos.buscarRetornarEquipoExistente(equipos, finalLine[1]); // TENGO QUE MANEJAR UNA EXCEPCION SI NO SE ENCUENTRA EL EQUIPO!!!
                if (equipoLocalBuffer == null ) { throw new Exception("El campo correspondiente al nombre del equipo contiene informacion invalida."); }
                Equipo equipoVisitanteBuffer = ValidacionDeDatos.buscarRetornarEquipoExistente(equipos, finalLine[4]); // TENGO QUE MANEJAR UNA EXCEPCION SI NO SE ENCUENTRA EL EQUIPO!!!
                if (equipoVisitanteBuffer == null ) { throw new Exception("El campo correspondiente al nombre del equipo contiene informacion invalida."); }
                // SI SE ENCONTRO UNA RONDA QUE NO FUE CREADA ENTONCES CREO UNA INSTANCIA DEL OBJETO RONDA Y POSTERIORMENTE LE CARGO SUS RESPECTIVOS PARTIDOS
                if (rondasBuffer.stream().noneMatch(i -> i.equals(Integer.parseInt(finalLine[0])))) { rondasBuffer.add(Integer.parseInt(finalLine[0])); }
                // CARGAR LA LISTA
                partidos.add(new Partido(
                        Integer.parseInt(line[0]),    // RONDA EN LA CUAL FUE JUGADO EL PARTIDO
                        lineNumber, // IDENTIFICACION UNIVOCA DEL PARTIDO!
                        equipoLocalBuffer,            // EQUIPO LOCAL!
                        Integer.parseInt(line[2]),    // GOLES DEL EQUIPO LOCAL!
                        Integer.parseInt(line[3]),    // GOLES DEL EQUIPO VISITANTE!
                        equipoVisitanteBuffer         // EQUIPO VISITANTE!
                ));
                // CAMBIO DE INDICE!
                lineNumber++;
            }
        } catch (Exception e) {
            TextFormat.informarError(ArchivoCSV, lineNumber, e);
            partidos = null;
        }
        rondas = armarObjetoRondas();
    }
    private static List<Ronda> armarObjetoRondas() {
        // ALGORITMO PARA CARGAR LOS PARTIDOS AL OBJETO RONDAS EN EL ORDEN EN EL CUAL SE INSTANCIARON! ESTOY ASUMIENDO QUE DESDE LA BASE DE DATOS ME LLEGAN ORDENADOS!
        System.out.println(TextFormat.colors.white + TextFormat.effects.bold + String.format("%1$-114s", "\n\t\t\t\t\t► ► ► PARTIDOS INSTANCIADOS! ◄ ◄ ◄") + TextFormat.colors.reset);
        for (int i = 0; i < rondasBuffer.size(); i++){
            final int aux = i; // EL INT AUX TIENE QUE SER FINAL PARA PODER SER PASADO A LA EXPRESION LAMBDA CON LA CUAL FILTRO LOS PARTIDOS QUE COINCIDEN CON EL ITERADOR "i"
            rondas.add(new Ronda(partidos.stream().filter(p -> p.getRondaCorrespondiente() == rondasBuffer.get(aux)).collect(Collectors.toList())));
            TextFormat.imprimirPartidosInstanciados(rondas.get(i).getPartidos());
        }
        return rondas;
    }
    private static void instanciarPronosticos(File ArchivoCSV) {
        String[] line;
        int lineNumber = 0;
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(ArchivoCSV)).withSkipLines(1).withCSVParser(new CSVParserBuilder().withSeparator(';').build()).build()) {
            while ((line = reader.readNext()) != null) {
                // VALIDACION DE LOS DATOS LEIDOS DE LA LINEA
                ValidacionDeDatos.validacionDelPronosticoLeido(line);
                // PROCEDO CON LA LOGICA DEL METODO
                Integer dniParticipanteBuffer = Integer.parseInt(line[1]);
                Equipo equipoLocalBuffer = ValidacionDeDatos.buscarRetornarEquipoExistente(equipos, line[2]);
                Equipo equipoVisitanteBuffer = ValidacionDeDatos.buscarRetornarEquipoExistente(equipos, line[6]);
                // SI EL EQUIPO ESPECIFICADO EN EL PRONOSTICO NO EXISTE ENTONCES LANZO UNA EXCEPCION!
                if (equipoLocalBuffer == null)
                    throw new Exception("El campo correspondiente al equipo local contiene informacion invalida: " + TextFormat.colors.red + line[2] + TextFormat.colors.reset);
                if (equipoVisitanteBuffer == null)
                    throw new Exception("El campo correspondiente al equipo visitante contiene informacion invalida: " + TextFormat.colors.red + line[6] + TextFormat.colors.reset);
                ResultadoEnum resultadoEquipoLocalBuffer = null;
                ResultadoEnum resultadoEquipoVisitanteBuffer = null;
                if (line[3].equals("x") || line[3].equals("X")) {
                    resultadoEquipoLocalBuffer = ResultadoEnum.VICTORIA;
                    resultadoEquipoVisitanteBuffer = ResultadoEnum.DERROTA;
                }
                if (line[4].equals("x") || line[4].equals("X")) {
                    resultadoEquipoLocalBuffer = ResultadoEnum.EMPATE;
                    resultadoEquipoVisitanteBuffer = ResultadoEnum.EMPATE;
                }
                if (line[5].equals("x") || line[5].equals("X")) {
                    resultadoEquipoLocalBuffer = ResultadoEnum.DERROTA;
                    resultadoEquipoVisitanteBuffer = ResultadoEnum.VICTORIA;
                }
                Partido partidoBuffer = ValidacionDeDatos.buscarRetornarPartidoExistente(partidos, line[2], line[6]);
                if (partidoBuffer == null){
                    throw new Exception("No hay tal partido donde participan "
                            + TextFormat.colors.red + equipoLocalBuffer.getNombre() + TextFormat.colors.reset + " vs. "
                            + TextFormat.colors.red + equipoVisitanteBuffer.getNombre() + TextFormat.colors.reset + ".");
                }
                Participante participanteBufferAux = ValidacionDeDatos.buscarRetornarParticipanteYaExistente(participantes, dniParticipanteBuffer);
                if (!ValidacionDeDatos.existeParticipante(participantes, dniParticipanteBuffer)){ // SI NO EXISTE EL PARTICIPANTE ENTONCES CREO UNA NUEVA INSTANCIA DEL OBJETO PARTICIPANTE
                    participanteBufferAux = new Participante(dniParticipanteBuffer, line[0]);
                    participantes.add(participanteBufferAux);
                }
                pronosticos.add(new Pronostico(
                        lineNumber,
                        participanteBufferAux,
                        partidoBuffer,
                        equipoLocalBuffer,
                        equipoVisitanteBuffer,
                        resultadoEquipoLocalBuffer,
                        resultadoEquipoVisitanteBuffer));
                lineNumber++;
            }
        } catch (Exception e) {
            TextFormat.informarError(ArchivoCSV, lineNumber, e);
            pronosticos = null;
        }
    }
    public static List<Equipo> getEquipos() {
        return equipos;
    }
    public static List<Partido> getPartidos() {
        return partidos;
    }
    public static List<Ronda> getRondas() {
        return rondas;
    }
    public static List<Participante> getParticipantes() {
        return participantes;
    }
    public static List<Pronostico> getPronosticos() {
        return pronosticos;
    }
    public static String getHost() { return host; }
    public static String getPort() { return port; }
    public static String getDbName() { return dbName; }
    public static String getUser() { return user; }
    public static String getPass() { return pass; }
    public static int getPuntuacionPorPartido() { return puntuacionPorPartido; }
    public static int getPuntuacionPorRonda() { return puntuacionPorRonda; }
    public static int getPuntuacionPorFase() { return puntuacionPorFase; }
    public static void setHost(String s) { host = s; }
    public static void setPort(String s) { port = s; }
    public static void setDbName(String s) { dbName = s; }
    public static void setUser(String s) { user = s; }
    public static void setPass(String s) { pass = s; }
    public static void setPuntuacionPorPartido(String s) { puntuacionPorPartido = Integer.parseInt(s); }
    public static void setPuntuacionPorRonda(String s) { puntuacionPorRonda = Integer.parseInt(s); }
    public static void setPuntuacionPorFase(String s) { puntuacionPorFase = Integer.parseInt(s); }
}
