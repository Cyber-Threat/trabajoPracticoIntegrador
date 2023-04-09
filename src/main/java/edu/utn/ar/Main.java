package edu.utn.ar;
// IMPORTACION DE DEPENDENCIAS!
import com.opencsv.CSVReader;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import edu.utn.ar.utils.*;
// IMPORTACION DE LIBRERIAS BUILT-IN!
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    private static List<Equipo> equipos = new ArrayList<>();
    private static List<Partido> partidos = new ArrayList<>();
    // VOY TRACKEANDO LAS RONDAS DE PARTIDOS QUE SE VAN JUGANDO PARA POSTERIORMENTE USAR ESTE DATO PARA ARMAR EL OBJETO RONDAS
    private static List<Integer> rondasBuffer = new ArrayList<>();
    // LA LISTA DE RONDAS QUE SE VAN JUGANDO PERO CONTENIENDO SUS RESPECTIVOS PARTIDOS!
    private static List<Ronda> rondas = new ArrayList<>();
    private static List<Pronostico> pronosticos = new ArrayList<>();
    private static List<Participante> participantes = new ArrayList<>();
    public static void main(String[] args) throws Exception {
        if (args.length == 1 && (args[0].charAt(0) == 'h' || args[0].equals("help"))) {
            TextFormat.helpBanner();
            return;
        }
        if (args.length != 2) {
            System.out.println(TextFormat.icons.error + "Parametro(s) invalido(s).");
            System.out.println(TextFormat.icons.help + "Introduzca <help> o <h> como unico y primer parametro adicional para ver el mensaje de ayuda disponible.");
            return;
        }
        // GUARDADO DE LOS DATOS INTRODUCIDOS POR CONSOLA!
        Path rutaDeArchivoPronosticos = Paths.get(args[0]).toAbsolutePath();
        Path rutaDeArchivoResultados = Paths.get(args[1]).toAbsolutePath();
        // VALIDACION DE LOS DATOS INTRODUCIDOS POR CONSOLA! SI LAS RUTAS INTRODUCIDAS SON SOLO DIRECTORIOS NO SIGO CON EL PROGRAMA Y SE LO INFORMO AL USUARIO!
        if (validacionDeDatos.rutaEspecificadaEsDirectorio(rutaDeArchivoPronosticos, rutaDeArchivoResultados)) { return; }
        // AHORA TENGO QUE COMPROBAR QUE LOS ARCHIVOS EXISTAN, A PESAR DE HABER COMPROBADO QUE LAS RUTAS SON VALIDAS!
        File pronosticosArchivoCSV = new File(rutaDeArchivoPronosticos.toUri());
        File resultadosArchivoCSV = new File(rutaDeArchivoResultados.toUri());
        if (validacionDeDatos.losArchivosNoExisten(rutaDeArchivoPronosticos, rutaDeArchivoResultados, pronosticosArchivoCSV, resultadosArchivoCSV)) { return; }
        // IMPRESION EN PANTALLA DE LOS DATOS VALIDOS!
        System.out.println(TextFormat.icons.success + "Archivo ingresado correctamente: " + TextFormat.colors.green + rutaDeArchivoPronosticos.getFileName() + TextFormat.colors.reset);
        System.out.println(TextFormat.icons.success + "Archivo ingresado correctamente: " + TextFormat.colors.green + rutaDeArchivoResultados.getFileName() + TextFormat.colors.reset);
        // AHORA PROCEDO CON ANALIZAR EL ARCHIVO DE RESULTADOS.CSV SOLO PARA CARGAR LA LISTA DE EQUIPOS
        equipos = instanciarEquipos(resultadosArchivoCSV);
        if (equipos == null) {
            System.out.println(TextFormat.icons.error + "Ejecucion del programa interrumpida por el error mostrado en pantalla.");
            return;
        }
        // AHORA PROCEDO CON ANALIZAR EL ARCHIVO DE RESULTADOS PARA CARGAR LA LISTA DE RONDAS CON LOS PARTIDOS JUGADOS REUTILIZANDO LOS OBJETOS DE TIPO EQUIPO INSTANCIADOS ANTERIORMENTE
        partidos = leerArchivoResultadosCSV(resultadosArchivoCSV, equipos);
        if (partidos == null) {
            System.out.println(TextFormat.icons.error + "Ejecucion del programa interrumpida por el error mostrado en pantalla.");
            return;
        }
        // AHORA QUE TENGO LOS PARTIDOS DISPONIBLES ARMO EL OBJETO RONDAS!
        rondas = armarObjetoRondas(partidos, rondasBuffer);
        // AHORA PROCEDO CON ANALIZAR UNO DE LOS DOS ARCHIVOS!
        pronosticos = leerArchivoPronosticosCSV(pronosticosArchivoCSV, partidos, equipos);
        if (pronosticos == null) {
            System.out.println(TextFormat.icons.error + "Ejecucion del programa interrumpida por el error mostrado en pantalla.");
            return;
        }
        // AHORA PROCEDO A COMPARAR LAS DOS LISTAS PARA DETERMINAR EL GANADOR POR PUNTOS!
        participantes = compararListas(partidos, pronosticos);
        // FIN DEL METODO MAIN
    }

    // METODO USADO PARA CARGAR EQUIPOS!
    private static List<Equipo> instanciarEquipos(File resultadosArchivoCSV) throws Exception, CsvValidationException {
        // List<Equipo> equipos = new ArrayList<>();
        int lineNumber = 0;
        int identificador = 0;
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(resultadosArchivoCSV)).withSkipLines(1).withCSVParser(new CSVParserBuilder().withSeparator(';').build()).build()) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                // ME ASEGURO QUE NO ESTEN VACIOS LOS CAMPOS CORRESPONDIENTES A LOS NOMBRES DE LOS EQUIPOS
                if (line[1].equals("") || line[4].equals("")) {
                    throw new Exception("Campo correspondiente al nombre de un equipo, esta vacio.");
                }
                // ME ASEGURO QUE NO HAYA SIDO CREADO ANTES EL EQUIPO EN CUESTION, SI NO FUE CREADO, CREO UNA INSTANCIA EN UN BUFFER
                String[] finalLine = line;
                if (equipos != null && !equipos.stream().anyMatch(equipo -> equipo.getNombre().equals(finalLine[1]))) {
                    Equipo equipoLocalBuffer = new Equipo(Integer.toString(identificador), line[1]);
                    equipos.add(lineNumber, equipoLocalBuffer);
                    identificador++;
                }
                if (equipos != null && !equipos.stream().anyMatch(equipo -> equipo.getNombre().equals(finalLine[4]))) {
                    Equipo equipoVisitanteBuffer = new Equipo(Integer.toString(identificador), line[4]);
                    equipos.add(lineNumber, equipoVisitanteBuffer);
                    identificador++;
                }
                // SI NO ESTAN VACIOS LOS CAMPOS CORRESPONDIENTES AL EQUIPO Y EL EQUIPO EN CUESTION NO FUE CREADO ANTES, ENTONCES LOS CARGO A LA LISTA
                lineNumber++;
            }
        } catch (Exception e) {
            TextFormat.informarError(resultadosArchivoCSV, lineNumber, e);
            equipos = null;
        } finally {
            // SENCILLAMENTE IMPRIMO LOS ELEMENTOS EN PANTALLA PARA VER QUE ESTA BIEN LO QUE HICE!
            TextFormat.imprimirEquiposInstanciados(equipos);
            return equipos;
        }
    }

    // METODO USADO PARA CARGAR LA LISTA DE PARTIDOS!
    private static List<Partido> leerArchivoResultadosCSV(File ArchivoCSV, List<Equipo> listaEquipos) throws Exception, CsvValidationException {
        List<Partido> partidos = new ArrayList<>();
        String[] line;
        int lineNumber = 0; // ITERADOR AUXILIAR PARA INFORMAR EN QUE LINEA DEL ARCHIVO ESTA EL PROBLEMA EN CASO QUE SUCEDA ALGO MAL
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(ArchivoCSV)).withSkipLines(1).withCSVParser(new CSVParserBuilder().withSeparator(';').build()).build()) {
            while ((line = reader.readNext()) != null) {
                validacionDeDatos.validacionDePartidoLeido(line);
                // TENGO QUE ASEGURARME DE NO INSTANCIAR UN EQUIPO REPETIDAS VECES!
                String[] finalLine = line;
                Equipo equipoLocalBuffer = listaEquipos.stream().filter(e -> e.getNombre().equals(finalLine[1])).findFirst().orElse(null); // TENGO QUE MANEJAR UNA EXCEPCION SI NO SE ENCUENTRA EL EQUIPO!!!
                if (equipoLocalBuffer == null ) { throw new Exception("El campo correspondiente al nombre del equipo contiene informacion invalida."); }
                Equipo equipoVisitanteBuffer = listaEquipos.stream().filter(e -> e.getNombre().equals(finalLine[4])).findFirst().orElse(null); // TENGO QUE MANEJAR UNA EXCEPCION SI NO SE ENCUENTRA EL EQUIPO!!!
                if (equipoVisitanteBuffer == null ) { throw new Exception("El campo correspondiente al nombre del equipo contiene informacion invalida."); }
                // EL NUMERO DE RONDA ES USADO PARA CONSTRUIR LA LISTA DE RONDAS
                if (rondasBuffer.stream().anyMatch(i -> i == Integer.parseInt(finalLine[0])) == false) { rondasBuffer.add(Integer.parseInt(finalLine[0])); }
                // CARGAR LA LISTA
                partidos.add(lineNumber, new Partido(
                        Integer.parseInt(line[0]),    // RONDA EN LA CUAL FUE JUGADO EL PARTIDO
                        Integer.toString(lineNumber), // IDENTIFICACION UNIVOCA DEL PARTIDO!
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
        } finally {
            return partidos;
        }
    }

    private static List<Ronda> armarObjetoRondas(List<Partido> partidos, List<Integer> rondasBuffer) {
        // ALGORITMO PARA CARGAR LOS PARTIDOS AL OBJETO RONDAS EN EL ORDEN EN EL CUAL SE INSTANCIARON! ESTOY ASUMIENDO QUE DESDE LA BASE DE DATOS ME LLEGAN ORDENADOS!
        System.out.println(TextFormat.colors.white + TextFormat.effects.bold + String.format("%1$-114s", "\n\t\t\t\t\t► ► ► PARTIDOS INSTANCIADOS! ◄ ◄ ◄") + TextFormat.colors.reset);
        for (int i = 0; i < rondasBuffer.size(); i++){
            final int aux = i; // EL INT AUX TIENE QUE SER FINAL PARA PODER SER PASADO A LA EXPRESION LAMBDA CON LA CUAL FILTRO LOS PARTIDOS QUE COINCIDEN CON EL ITERADOR "i"
            rondas.add(new Ronda(partidos.stream().filter(p -> p.getRondaCorrespondiente() == rondasBuffer.get(aux)).collect(Collectors.toList())));
            TextFormat.imprimirPartidosInstanciados(rondas.get(i).getPartidos());
        }
        return rondas;
    }

    // METODO USADO PARA LEER EL ARCHIVO PRONOSTICOS.CSV
    private static List<Pronostico> leerArchivoPronosticosCSV(File ArchivoCSV, List<Partido> partidos, List<Equipo> equipos) {
        List<Pronostico> pronosticos = new ArrayList<>();
        String[] line;
        int lineNumber = 0;
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(ArchivoCSV)).withSkipLines(1).withCSVParser(new CSVParserBuilder().withSeparator(';').build()).build()) {
            while ((line = reader.readNext()) != null) {
                String[] finalLine = line;
                // VALIDACION DE LOS DATOS LEIDOS DE LA LINEA
                validacionDeDatos.validacionDelPronosticoLeido(pronosticos, line, finalLine);
                // PROCEDO CON LA LOGICA DEL METODO
                String nombreParticipanteBuffer = line[0];
                Equipo equipoLocalBuffer = equipos.stream().filter(e -> e.getNombre().equals(finalLine[1])).findFirst().orElse(null);
                Equipo equipoVisitanteBuffer = equipos.stream().filter(e -> e.getNombre().equals(finalLine[5])).findFirst().orElse(null);
                // SI EL EQUIPO ESPECIFICADO EN EL PRONOSTICO NO EXISTE ENTONCES LANZO UNA EXCEPCION!
                if (equipoLocalBuffer == null)
                    throw new Exception("El campo correspondiente al equipo local contiene informacion invalida: " + TextFormat.colors.red + finalLine[1]);
                if (equipoVisitanteBuffer == null)
                    throw new Exception("El campo correspondiente al equipo visitante contiene informacion invalida: " + TextFormat.colors.red + finalLine[5]);
                ResultadoEnum resultadoEquipoLocalBuffer = null;
                ResultadoEnum resultadoEquipoVisitanteBuffer = null;
                if (line[2].equals("x") || line[2].equals("X")) {
                    resultadoEquipoLocalBuffer = ResultadoEnum.VICTORIA;
                    resultadoEquipoVisitanteBuffer = ResultadoEnum.DERROTA;
                }
                if (line[3].equals("x") || line[3].equals("X")) {
                    resultadoEquipoLocalBuffer = ResultadoEnum.EMPATE;
                    resultadoEquipoVisitanteBuffer = ResultadoEnum.EMPATE;
                }
                if (line[4].equals("x") || line[4].equals("X")) {
                    resultadoEquipoLocalBuffer = ResultadoEnum.DERROTA;
                    resultadoEquipoVisitanteBuffer = ResultadoEnum.VICTORIA;
                }
                Partido partidoBuffer = partidos.stream().filter(p -> p.getEquipoLocal().getNombre().equals(finalLine[1]) && p.getEquipoVisitante().getNombre().equals(finalLine[5])).findFirst().get();
                pronosticos.add(lineNumber, new Pronostico(nombreParticipanteBuffer, partidoBuffer, equipoLocalBuffer, equipoVisitanteBuffer, resultadoEquipoLocalBuffer, resultadoEquipoVisitanteBuffer));
                lineNumber++;
            }
        } catch (Exception e) {
            TextFormat.informarError(ArchivoCSV, lineNumber, e);
            pronosticos = null;
        } finally {
            return pronosticos;
        }
    }

    // METODO USADOS PARA COMPARAR LAS LISTAS OBTENIDAS A PARTIR DE LOS ARCHIVOS
    private static List<Participante> compararListas(List<Partido> listaPartidos, List<Pronostico> listaPronosticos) throws Exception {
        List<Participante> participantes = new ArrayList<>();
        int indicePronosticos = 0;
        int indiceParticipantes = 0;
        int identificador = 0; // EL INT QUE PIENSO USAR COMO IDENTIFICADOR UNIVOCO
        // ITERO A LO LARGO DE LA LISTA DE PRONOSTICOS PARA OBTENER LOS PARTICIPANTES

        do {
            String nombreBufferAux = listaPronosticos.get(indicePronosticos).getNombreParticipante();
            // SI EL PARTICIPANTE NO EXISTE ENTONCES LO CREO Y DESPUÉS LO CARGO EN LA LISTA
            if (participantes.stream().filter(p -> p.getNombre().equals(nombreBufferAux)).findFirst().orElse(null) == null) {
                participantes.add(indiceParticipantes, new Participante(Integer.toString(identificador), nombreBufferAux));
                // ACTUALIZO LOS ITERADORES PARA LOS PARTICIPANTES Y EL ITERADOR QUE USO PARA IDENTIFICACION UNIVOCA!
                indiceParticipantes++;
                identificador++;
            }
            // ACTUALIZO EL ITERADOR DE PRONOSTICOS!
            indicePronosticos++;
        } while (indicePronosticos < listaPronosticos.size());
        // AHORA LE CARGO LOS PUNTOS A LOS PARTICIPANTES!
        participantes = cargarPuntosSegunLosPronosticosAcertados(listaPronosticos, listaPartidos, participantes);
        Collections.sort(participantes, (p1, p2) -> Float.compare(p2.getPuntosAcumulados(), p1.getPuntosAcumulados()));
        TextFormat.imprimirTablaDePuntuaciones(participantes);
        return participantes;
    }

    // CARGO LOS PUNTOS CORRESPONDIENTES A CADA PARTICIPANTE ITERANDO SOBRE LA LISTA DE PRONOSTICOS Y EVALUANDO SI EXISTE EL PARTIDO AL CUAL SE REFIEREN!
    private static List<Participante> cargarPuntosSegunLosPronosticosAcertados(List<Pronostico> listaPronosticos, List<Partido> listaPartidos, List<Participante> listaParticipantes) {
        int indiceParticipantes = 0;
        List<Participante> participantes = listaParticipantes;
        List<Pronostico> subListaPronosticos = new ArrayList<>();
        // PARA CADA PARTICIPANTE QUIERO SABER QUÉ PRONÓSTICOS ACERTARON
        System.out.println(TextFormat.colors.white + TextFormat.effects.bold + String.format("%1$-114s", "\n\t\t\t\t\t► ► ► PRONOSTICOS DE LOS PARTICIPANTES! ◄ ◄ ◄") + TextFormat.colors.reset);
        do {
            String nombreBuffer = listaParticipantes.get(indiceParticipantes).getNombre(); // ME GUARDO EL NOMBRE DEL PARTICIPANTE DE ESTA ITERACION EN UN BUFFER
            subListaPronosticos = listaPronosticos.stream().filter(l -> l.getNombreParticipante().equals(nombreBuffer)).collect(Collectors.toList()); // CREO UNA SUBLISTA DE PRONOSTICOS SOLO CORRESPONDIENTES A ESE PARTICIPANTE
            int indicePartidos = 0;
            for (int i = 0; i < subListaPronosticos.size(); i++) { // PARA CADA PRONOSTICO DE LA SUBLISTA QUIERO SABER CUALES SON ACERTADOS
                String nombreLocalBuffer = subListaPronosticos.get(i).getEquipoLocal().getNombre();
                String nombreVisitanteBuffer = subListaPronosticos.get(i).getEquipoVisitante().getNombre();
                // BUSCO EL PARTIDO AL QUE SE REFIERE EL PRONOSTICO PARA LA POSTERIOR EVALUACION!
                Partido pBuffer = listaPartidos.stream().filter(p -> p.getEquipoLocal().getNombre().equals(nombreLocalBuffer) && p.getEquipoVisitante().getNombre().equals(nombreVisitanteBuffer)).findFirst().get();
                // EFECTIVAMENTE EVALUO SI EL PRONOSTICO ES ACERTADO, DE SER EL CASO SUMO PUNTOS AL PARTICIPANTE!!
                if (pBuffer.getResultadoEquipoLocal() == subListaPronosticos.get(i).getPronosticoEquipoLocal() && pBuffer.getResultadoEquipoVisitante() == subListaPronosticos.get(i).getPronosticoEquipoVisitante()) {
                    participantes.get(indiceParticipantes).adicionarPuntos(1.0f);
                }
                System.out.println(TextFormat.colors.cyan + String.format("%1$-128s", " ").replace(' ', '─') + TextFormat.colors.reset);
                TextFormat.imprimirPronosticosDelParticipante(indiceParticipantes, participantes, subListaPronosticos, nombreBuffer, i, pBuffer);
            }
            indiceParticipantes++;
        } while (indiceParticipantes < listaParticipantes.size());
        System.out.println(TextFormat.colors.cyan + String.format("%1$-128s", " ").replace(' ', '─') + TextFormat.colors.reset);
        return participantes;
    }
}