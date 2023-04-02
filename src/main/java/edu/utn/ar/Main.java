package edu.utn.ar;
import com.opencsv.CSVReader;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.lang3.ObjectUtils;
import org.w3c.dom.Text;
import javax.xml.transform.Result;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 1 && (args[0].charAt(0) == 'h' || args[0].equals("help"))) { helpBanner(); return; }
        if (args.length != 2) {
            System.out.println(TextFormat.icons.error + "Parametro(s) invalido(s).");
            System.out.println(TextFormat.icons.help + "Introduzca <help> o <h> como unico y primer parametro adicional para ver el mensaje de ayuda disponible.");
            return;
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
            if(!rutaValidaDePronosticosCSV){ System.out.println(TextFormat.icons.error + "Error. La ruta especificada es directorio: " + TextFormat.colors.red + rutaDeArchivoPronosticos + TextFormat.colors.reset); }
            if(!rutaValidaDeResultadosCSV){ System.out.println(TextFormat.icons.error + "Error. La ruta especificada es directorio: " + TextFormat.colors.red + rutaDeArchivoResultados + TextFormat.colors.reset); }
            return;
        }
        // AHORA TENGO QUE COMPROBAR QUE LOS ARCHIVOS EXISTAN, A PESAR DE HABER COMPROBADO QUE LAS RUTAS SON VALIDAS!
        File pronosticosArchivoCSV = new File(rutaDeArchivoPronosticos.toUri());
        File resultadosArchivoCSV = new File(rutaDeArchivoResultados.toUri());
        if(!pronosticosArchivoCSV.exists()){ archivoValidoDePronosticos = false; }
        if(!resultadosArchivoCSV.exists()){ archivoValidoDeResultados = false; }
        // INFORMO DEL ERROR EN CUESTION!
        if (!archivoValidoDePronosticos || !archivoValidoDeResultados){
            if(!archivoValidoDePronosticos){ System.out.println(TextFormat.icons.error + "El archivo especificado no existe: " + TextFormat.colors.red + rutaDeArchivoPronosticos + TextFormat.colors.reset); }
            if(!archivoValidoDeResultados){ System.out.println(TextFormat.icons.error + "El archivo especificado no existe: " + TextFormat.colors.red + rutaDeArchivoResultados + TextFormat.colors.reset); }
            return;
        }
        // IMPRESION EN PANTALLA DE LOS DATOS VALIDOS!
        System.out.println(TextFormat.icons.success + "Archivo ingresado correctamente: " + TextFormat.colors.green + rutaDeArchivoPronosticos.getFileName() + TextFormat.colors.reset);
        System.out.println(TextFormat.icons.success + "Archivo ingresado correctamente: " + TextFormat.colors.green + rutaDeArchivoResultados.getFileName() + TextFormat.colors.reset);
        // AHORA PROCEDO CON ANALIZAR EL ARCHIVO DE RESULTADOS.CSV SOLO PARA CARGAR LA LISTA DE EQUIPOS
        List<Equipo> equipos = instanciarEquipos(resultadosArchivoCSV);
        if (equipos == null){
            System.out.println(TextFormat.icons.error +  "Ejecucion del programa interrumpida por el error mostrado en pantalla."); return;
        }
        // AHORA PROCEDO CON ANALIZAR EL ARCHIVO DE RESULTADOS PARA CARGAR LA LISTA DE PARTIDOS JUGADOS REUTILIZANDO LOS OBJETOS DE TIPO EQUIPO INSTANCIADOS ANTERIORMENTE
        List<Partido> partidos = leerArchivoResultadosCSV(resultadosArchivoCSV, equipos);
        if (partidos == null){
            System.out.println(TextFormat.icons.error +  "Ejecucion del programa interrumpida por el error mostrado en pantalla."); return;
        }
        // AHORA PROCEDO CON ANALIZAR UNO DE LOS DOS ARCHIVOS!
        List<Pronostico> pronosticos = leerArchivoPronosticosCSV(pronosticosArchivoCSV, partidos, equipos);
        if (pronosticos == null){
            System.out.println(TextFormat.icons.error + "Ejecucion del programa interrumpida por el error mostrado en pantalla."); return;
        }
        // AHORA PROCEDO A COMPARAR LAS DOS LISTAS PARA DETERMINAR EL GANADOR POR PUNTOS!
        List<Participante> participantes = compararListas(partidos, pronosticos);
        // FIN DEL METODO MAIN
    }

    private static List<Equipo> instanciarEquipos(File resultadosArchivoCSV) throws Exception, CsvValidationException {
        List<Equipo> equipos = new ArrayList<>();
        int lineNumber = 0; int identificador = 0;
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(resultadosArchivoCSV)).withSkipLines(1).withCSVParser(new CSVParserBuilder().withSeparator(';').build()).build()) {
            String[] line;
            while((line = reader.readNext()) != null){
                // ME ASEGURO QUE NO ESTEN VACIOS LOS CAMPOS CORRESPONDIENTES A LOS NOMBRES DE LOS EQUIPOS
                if (line[0].equals("") || line[3].equals("")){ throw new Exception("Campo correspondiente al nombre de un equipo, esta vacio."); }
                // ME ASEGURO QUE NO HAYA SIDO CREADO ANTES EL EQUIPO EN CUESTION, SI NO FUE CREADO, CREO UNA INSTANCIA EN UN BUFFER
                String[] finalLine = line;
                if(equipos != null && !equipos.stream().anyMatch(equipo -> equipo.getNombre().equals(finalLine[0]))){
                    Equipo equipoLocalBuffer = new Equipo(Integer.toString(identificador), line[0]);
                    equipos.add(lineNumber, equipoLocalBuffer);
                    identificador++;
                }
                if(equipos != null && !equipos.stream().anyMatch(equipo -> equipo.getNombre().equals(finalLine[3]))){
                    Equipo equipoVisitanteBuffer = new Equipo(Integer.toString(identificador), line[3]);
                    equipos.add(lineNumber, equipoVisitanteBuffer);
                    identificador++;
                }
                // SI NO ESTAN VACIOS LOS CAMPOS CORRESPONDIENTES AL EQUIPO Y EL EQUIPO EN CUESTION NO FUE CREADO ANTES, ENTONCES LOS CARGO A LA LISTA
                lineNumber++;
            }
        } catch (Exception e) {
            System.out.println(TextFormat.icons.error + "Lectura erronea del archivo: " + TextFormat.colors.red + resultadosArchivoCSV.toPath().getFileName() + TextFormat.colors.reset);
            System.out.println(TextFormat.icons.error + "En el renglon: " + TextFormat.colors.red + (lineNumber + 1) + TextFormat.colors.reset);
            System.out.println(TextFormat.icons.error + "Excepcion: " + e.getClass());
            System.out.println(TextFormat.icons.error + e.getMessage());
            equipos = null;
        } finally {
            // SENCILLAMENTE IMPRIMO LOS ELEMENTOS EN PANTALLA PARA VER QUE TODO ESTA EN ORDEN!
            int iE = 0;
            do {
                System.out.println(TextFormat.icons.info + "Equipo instanciado: " + TextFormat.colors.green + equipos.get(iE).getNombre() + TextFormat.colors.reset
                        + " Identificacion univoca: " + TextFormat.colors.green + equipos.get(iE).getIdentificacionUnivoca() + TextFormat.colors.reset);
                iE++;
            } while(iE < equipos.size());
            return equipos;
        }
    }

    // METODO USADO PARA CARGAR LA LISTA DE PARTIDOS!
    private static List<Partido> leerArchivoResultadosCSV(File ArchivoCSV, List<Equipo> listaEquipos) throws Exception, CsvValidationException {
        List<Partido> partidos = new ArrayList<>(); String[] line; int lineNumber = 0;
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(ArchivoCSV)).withSkipLines(1).withCSVParser(new CSVParserBuilder().withSeparator(';').build()).build()) {
            while ((line = reader.readNext()) != null) {
                // ME ASEGURO DE QUE HAYAN LLEGADO LA CANTIDAD DE CAMPOS NECESARIOS!
                if (line.length != 4){ throw new IOException("La cantidad de campos introducidos es incorrecta."); }
                // ME ASEGURO QUE LOS CAMPOS CORRESPONDIENTES A LOS NOMBRES DE LOS EQUIPOS NO ESTEN VACIOS!
                if (line[0].equals("") || line[3].equals("")){ throw new IOException("El campo correspondiente al nombre de uno de los equipos esta vacio."); }
                // ME ASEGURO QUE LOS CAMPOS CORRESPONDIENTES A LOS GOLES NO ESTEN VACIOS!
                if (line[1].equals("") || line[2].equals("")){ throw new NumberFormatException("El campo correspondiente a los goles de un equipo no contiene un valor para leer."); }
                // ME ASEGURO QUE LOS CAMPOS CORRESPONDIENTES A LOS GOLES NO TENGAN VALORES NEGATIVOS!
                if((Integer.parseInt(line[1]) < 0) || Integer.parseInt(line[2]) < 0){ throw new NumberFormatException("El campo correspondiente a los goles de un equipo contiene un valor negativo."); }
                // ME TENGO QUE ASEGURAR QUE NO TENGA VALORES FLOTANTES
                // LO TENGO QUE HACER!!!!
                // TENGO QUE ASEGURARME DE NO INSTANCIAR UN EQUIPO REPETIDAS VECES!
                String[] finalLine = line;
                Equipo equipoLocalBuffer = listaEquipos.stream().filter(e -> e.getNombre().equals(finalLine[0])).findFirst().get(); // TENGO QUE MANEJAR UNA EXCEPCION SI NO SE ENCUENTRA EL EQUIPO!!!
                Equipo equipoVisitanteBuffer = listaEquipos.stream().filter(e -> e.getNombre().equals(finalLine[3])).findFirst().get(); // TENGO QUE MANEJAR UNA EXCEPCION SI NO SE ENCUENTRA EL EQUIPO!!!
                // CARGAR LA LISTA
                partidos.add(lineNumber, new Partido(
                        Integer.toString(lineNumber), // IDENTIFICACION UNIVOCA DEL PARTIDO!
                        equipoLocalBuffer,            // EQUIPO LOCAL!
                        Integer.parseInt(line[1]),    // GOLES DEL EQUIPO LOCAL!
                        Integer.parseInt(line[2]),    // GOLES DEL EQUIPO VISITANTE!
                        equipoVisitanteBuffer         // EQUIPO VISITANTE!
                ));
                // CAMBIO DE INDICE!
                lineNumber++;
            }
        } catch (Exception e) {
            System.out.println(TextFormat.icons.error + "Lectura erronea del archivo: " + TextFormat.colors.red + ArchivoCSV.toPath().getFileName() + TextFormat.colors.reset);
            System.out.println(TextFormat.icons.error + "En el renglon: " + TextFormat.colors.red + (lineNumber + 1) + TextFormat.colors.reset);
            System.out.println(TextFormat.icons.error + "Excepcion: " + e.getClass());
            System.out.println(TextFormat.icons.error + e.getMessage());
            partidos = null;
        } finally {
            // SENCILLAMENTE IMPRIMO LOS ELEMENTOS EN PANTALLA PARA VER QUE TODO ESTA EN ORDEN!
            int iP = 0;
            do {
                System.out.println(TextFormat.icons.info + "Identificacion univoca del partido: " + TextFormat.colors.green + partidos.get(iP).getIdentificacionUnivoca() + TextFormat.colors.reset
                        + " Equipo local: " + TextFormat.colors.green + partidos.get(iP).getEquipoLocal().getNombre() + TextFormat.colors.reset
                        + "(" + partidos.get(iP).getEquipoLocal().getIdentificacionUnivoca() + ")" + " Resultado del partido para el equipo local: " + TextFormat.colors.green + partidos.get(iP).getResultadoEquipoLocal() + TextFormat.colors.reset
                        + " Equipo visitante: " + TextFormat.colors.green + partidos.get(iP).getEquipoVisitante().getNombre() + TextFormat.colors.reset
                        + "(" + partidos.get(iP).getEquipoVisitante().getIdentificacionUnivoca() + ")" + " Resultado del partido para el equipo visitante: " + TextFormat.colors.green + partidos.get(iP).getResultadoEquipoVisitante() + TextFormat.colors.reset);
                iP++;
            } while(iP < partidos.size());
            return partidos;
        }
    }
    // METODO USADO PARA LEER EL ARCHIVO PRONOSTICOS.CSV
    private static List<Pronostico> leerArchivoPronosticosCSV(File ArchivoCSV, List<Partido> partidos, List<Equipo> equipos) throws Exception, CsvValidationException{
        List<Pronostico> pronosticos = new ArrayList<>(); String[] line; int lineNumber = 0;
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(ArchivoCSV)).withSkipLines(1).withCSVParser(new CSVParserBuilder().withSeparator(';').build()).build()) {
            while ((line = reader.readNext()) != null) {
                // ME ASEGURO QUE HAYA LA CANTIDAD CORRECTA DE CAMPOS
                if (line.length != 6){ throw new Exception("La cantidad de campos introducidos es incorrecta."); }
                // ME ASEGURO QUE EXISTA EL NOMBRE DEL PARTICIPANTE
                if (line[0].equals("")){ throw new Exception("El campo correspondiente al nombre del participante esta vacio."); }
                // ME ASEGURO QUE SE HAYA INTRODUCIDO EL NOMBRE DE LOS EQUIPOS
                if (line[1].equals("") || line[5].equals("")){ throw new Exception("El campo correspondiente a uno de los equipos esta vacio."); }
                // ME ASEGURO QUE EXISTA EL VALOR QUE DICTA EL RESULTADO DEL PARTIDO
                if (line[2].equals("") && line[3].equals("") && line[4].equals("")){ throw new Exception("Los campos que pronostican el resultado del partido están vacios."); }
                // ME ASEGURO QUE HAYA UN SOLO CAMPO PRONOSTICANDO EL RESULTADO DEL PARTIDO
                if (!soloHayUnEnumerador(line[2], line[3], line[4])){ throw new Exception("Los campos donde se coloca el resultado pronosticado del partido tienen información invalida."); }
                // ENUMERADOR VALIDO (SOLO DEBE SER UNA "X" MAYUSCULA O MINUSCULA!
                if (!enumeradorValido(line[2], line[3], line[4])) { throw new Exception("El enumerador usado en los campos donde se pronostica el resultado del partido, es invalido."); }
                String nombreParticipanteBuffer = line[0];
                String[] finalLine = line;
                Equipo equipoLocalBuffer = equipos.stream().filter(e -> e.getNombre().equals(finalLine[1])).findFirst().orElse(null);
                Equipo equipoVisitanteBuffer = equipos.stream().filter(e -> e.getNombre().equals(finalLine[5])).findFirst().orElse(null);
                // SI EL EQUIPO ESPECIFICADO EN EL PRONOSTICO NO EXISTE ENTONCES LANZO UNA EXCEPCION!
                if (equipoLocalBuffer == null) throw new Exception("El campo correspondiente al equipo local contiene informacion invalida.");
                if (equipoVisitanteBuffer == null) throw new Exception("El campo correspondiente al equipo visitante contiene informacion invalida.");
                ResultadoEnum resultadoEquipoLocalBuffer = null;
                ResultadoEnum resultadoEquipoVisitanteBuffer = null;
                if (line[2].equals("x") || line[2].equals("X")) {
                    resultadoEquipoLocalBuffer = ResultadoEnum.GANADOR;
                    resultadoEquipoVisitanteBuffer = ResultadoEnum.PERDEDOR;
                }
                if (line[3].equals("x") || line[3].equals("X")) {
                    resultadoEquipoLocalBuffer = ResultadoEnum.EMPATE;
                    resultadoEquipoVisitanteBuffer = ResultadoEnum.EMPATE;
                }
                if (line[4].equals("x") || line[4].equals("X")) {
                    resultadoEquipoLocalBuffer = ResultadoEnum.PERDEDOR;
                    resultadoEquipoVisitanteBuffer = ResultadoEnum.GANADOR;
                }
                Partido partidoBuffer = partidos.stream().filter(p -> p.getEquipoLocal().getNombre().equals(finalLine[1]) && p.getEquipoVisitante().getNombre().equals(finalLine[5])).findFirst().get();
                pronosticos.add(lineNumber, new Pronostico(nombreParticipanteBuffer, partidoBuffer, equipoLocalBuffer, equipoVisitanteBuffer, resultadoEquipoLocalBuffer, resultadoEquipoVisitanteBuffer));
                lineNumber++;
            }
        } catch (Exception e) {
            System.out.println(TextFormat.icons.error + "Lectura erronea del archivo: " + ArchivoCSV.toPath().getFileName());
            System.out.println(TextFormat.icons.error + "En el renglon: " + TextFormat.colors.red + (lineNumber + 1) + TextFormat.colors.reset);
            System.out.println(TextFormat.icons.error + "Excepcion: " + e.getClass());
            System.out.println(TextFormat.icons.error + e.getMessage());
            pronosticos = null;
        } finally {
            int iP = 0;
            do {
                System.out.println(TextFormat.icons.info + "Participante del pronostico: " + TextFormat.colors.green + pronosticos.get(iP).getNombreParticipante() + TextFormat.colors.reset
                        + " Partido: " + TextFormat.colors.green + pronosticos.get(iP).getPartido().getEquipoLocal().getNombre() + TextFormat.colors.reset
                        + " vs. " + TextFormat.colors.green + pronosticos.get(iP).getPartido().getEquipoVisitante().getNombre() + TextFormat.colors.reset
                        + " Resultado pronosticado para equipo local: " + TextFormat.colors.green + pronosticos.get(iP).getPronosticoEquipoLocal() + TextFormat.colors.reset
                        + " Resultado pronosticado para equipo visitante: " + TextFormat.colors.green + pronosticos.get(iP).getPronosticoEquipoVisitante() + TextFormat.colors.reset);
                iP++;
            } while (iP < pronosticos.size());
            return pronosticos;
        }
    }
    // METODO AUXILIAR PARA DETERMINAR SI EL ENUMERADOR USADO EN EL ARCHIVO DE PRONOSTICOS ES VALIDO, SOLO DEBE SER UNA X MAYUSCULA O MINUSCULA!
    private static boolean enumeradorValido(String s0, String s1, String s2) {
        boolean enum0 = (s0.equals("x") || s0.equals("X")) && (s1.equals("")) && (s2.equals(""));
        boolean enum1 = (s0.equals("")) && (s1.equals("x") || s1.equals("X")) && (s2.equals(""));
        boolean enum2 = (s0.equals("")) && (s1.equals("")) && (s2.equals("x") || s2.equals("X"));
        if (enum0 || enum1 || enum2){ return true; }
        return false;
    }
    // METODO AUXILIAR SOLO PARA DETERMINAR SI LOS CAMPOS CORRESPONDIENTES A LOS RESULTADOS PRONOSTICADOS TIENEN UN SOLO INDICADOR ESCRITO POR LINEA!
    private static boolean soloHayUnEnumerador(String s0, String s1, String s2) {
        boolean enum1 = (!s0.equals("") && s1.equals("") && s2.equals(""));
        boolean enum2 = (s0.equals("") && !s1.equals("") && s2.equals(""));
        boolean enum3 = (s0.equals("") && s1.equals("") && !s2.equals(""));
        if (enum1 || enum2 || enum3){ return true; }
        return false;
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
            if (participantes.stream().filter(p -> p.getNombre().equals(nombreBufferAux)).findFirst().orElse(null) == null){
                participantes.add(indiceParticipantes, new Participante(Integer.toString(identificador), nombreBufferAux));
                // ACTUALIZO LOS ITERADORES PARA LOS PARTICIPANTES Y EL ITERADOR QUE USO PARA IDENTIFICACION UNIVOCA!
                indiceParticipantes++;
                identificador++;
            }
            // ACTUALIZO EL ITERADOR DE PRONOSTICOS!
            indicePronosticos++;
        } while(indicePronosticos < listaPronosticos.size());
        // AHORA LE CARGO LOS PUNTOS A LOS PARTICIPANTES!
        participantes = cargarPuntosSegunLosPronosticosAcertados(listaPronosticos, listaPartidos, participantes);
        Collections.sort(participantes, (p1, p2) -> Float.compare(p2.getPuntosAcumulados(), p1.getPuntosAcumulados()));
        // IMPRIMO INFORMACION EN PANTALLA PARA PODER VISUALIZARLA!
        int iP = 0;
        System.out.println("\n" + TextFormat.colors.cyan + "\t\t\t\t► ► TABLA DE PUNTUACIONES ◄ ◄" + TextFormat.colors.reset + "\n");
        do {
            System.out.println("\t\tPuesto (" + TextFormat.colors.cyan + (iP + 1) + TextFormat.colors.reset + "): " + TextFormat.colors.green + participantes.get(iP).getNombre() + TextFormat.colors.reset
                    + " Identificacion univoca: " + TextFormat.colors.green + participantes.get(iP).getIdentificacionUnivoca() + TextFormat.colors.reset
                    + " Puntos: (" + TextFormat.colors.purple + participantes.get(iP).getPuntosAcumulados() + TextFormat.colors.reset + ")");
            iP++;
        }while (iP < participantes.size());
        return participantes;
    }

    private static List<Participante> cargarPuntosSegunLosPronosticosAcertados(List<Pronostico> listaPronosticos, List<Partido> listaPartidos, List<Participante> listaParticipantes) {
        int indiceParticipantes = 0;
        List<Participante> participantes = listaParticipantes;
        List<Pronostico> subListaPronosticos = new ArrayList<>();
        // PARA CADA PARTICIPANTE QUIERO SABER QUÉ PRONÓSTICOS ACERTARON
        do {
            String nombreBuffer = listaParticipantes.get(indiceParticipantes).getNombre(); // ME GUARDO EL NOMBRE DEL PARTICIPANTE DE ESTA ITERACION EN UN BUFFER
            subListaPronosticos = listaPronosticos.stream().filter(l -> l.getNombreParticipante().equals(nombreBuffer)).collect(Collectors.toList()); // CREO UNA SUBLISTA DE PRONOSTICOS SOLO CORRESPONDIENTES A ESE PARTICIPANTE
            int indicePartidos = 0;
            for (int i = 0; i < subListaPronosticos.size(); i++) { // PARA CADA PRONOSTICO DE LA SUBLISTA QUIERO SABER CUALES SON ACERTADOS
                String nombreLocalBuffer = subListaPronosticos.get(i).getEquipoLocal().getNombre();
                String nombreVisitanteBuffer = subListaPronosticos.get(i).getEquipoVisitante().getNombre();
                System.out.println(TextFormat.icons.info + "Pronosticos correspondientes al participante: " + TextFormat.colors.green + nombreBuffer + TextFormat.colors.reset
                                    + " Identificacion univoca del partido referido: " + TextFormat.colors.blue + subListaPronosticos.get(i).getPartido().getIdentificacionUnivoca() + TextFormat.colors.reset
                                    + TextFormat.colors.yellow + "\n\t . . . " + TextFormat.colors.reset + "Equipo local del pronostico: " + TextFormat.colors.green + subListaPronosticos.get(i).getEquipoLocal().getNombre() + TextFormat.colors.reset + ": "
                                    + TextFormat.colors.cyan + subListaPronosticos.get(i).getPronosticoEquipoLocal() + TextFormat.colors.reset
                                    + TextFormat.colors.yellow + "\n\t . . . " + TextFormat.colors.reset + "Equipo visitante del pronostico: " + TextFormat.colors.green + subListaPronosticos.get(i).getEquipoVisitante().getNombre() + TextFormat.colors.reset + ": "
                                    + TextFormat.colors.cyan + subListaPronosticos.get(i).getPronosticoEquipoVisitante() + TextFormat.colors.reset);
                Partido pBuffer = listaPartidos.stream().filter(p -> p.getEquipoLocal().getNombre().equals(nombreLocalBuffer) && p.getEquipoVisitante().getNombre().equals(nombreVisitanteBuffer)).findFirst().get();
                System.out.println(TextFormat.colors.yellow + "\t . . . " + TextFormat.colors.reset
                        + "Partido referido por el participante: <" + TextFormat.colors.blue + pBuffer.getIdentificacionUnivoca() +  TextFormat.colors.reset + "> "
                        + TextFormat.colors.green + pBuffer.getEquipoLocal().getNombre() + TextFormat.colors.reset + " (" + TextFormat.colors.red + pBuffer.getResultadoEquipoLocal() + TextFormat.colors.reset+ ")"
                        + " vs. " + TextFormat.colors.green + pBuffer.getEquipoVisitante().getNombre() + TextFormat.colors.reset + " (" + TextFormat.colors.red + pBuffer.getResultadoEquipoVisitante() + TextFormat.colors.reset+ ")");
                if (pBuffer.getResultadoEquipoLocal() == subListaPronosticos.get(i).getPronosticoEquipoLocal() && pBuffer.getResultadoEquipoVisitante() == subListaPronosticos.get(i).getPronosticoEquipoVisitante()){
                    participantes.get(indiceParticipantes).adicionarPuntos(1.0f);
                    System.out.println(TextFormat.colors.yellow + "\t . . . " + TextFormat.colors.purple + "PUNTOS AÑADIDOS: " + Float.toString(1.0f) + TextFormat.colors.reset);
                }
            }
            indiceParticipantes++;
        } while(indiceParticipantes < listaParticipantes.size());
        return participantes;
    }

    // BANNER DE AYUDA SI SE INTRODUCE EL COMANDO <help> Ó <h>
    private static void helpBanner() {
        System.out.println(TextFormat.icons.help + "Mensaje de ayuda:");
        System.out.println(TextFormat.icons.help + "El programa esta pensado para leer dos archivos llamados \"pronosticos.csv\" y \"resultados.csv\".");
        System.out.println(TextFormat.icons.help + "Dichos archivos tienen que ser especificados como parametros");
        System.out.println(TextFormat.icons.help + "al momento de ejecutar el programa desde la consola de comandos.\"");
        System.out.println(TextFormat.icons.help + "La sintaxis es la siguiente, primero la ruta del archivo de los pronosticos dados por los participantes, luego");
        System.out.println(TextFormat.icons.help + "la ruta del archivo donde estan escritos los resultados de los partidos:");
        System.out.println(TextFormat.icons.help + "java .\\Main.java <rutaDelArchivoPronosticosCSV> <rutaDelArchivoResultadosCSV>");
        System.out.println(TextFormat.icons.help + "Una vez especificadas las rutas a los archivos necesarios el programa puede interpretar su contenido");
        System.out.println(TextFormat.icons.help + "y opcionalmente volcando en un archivo \"puntuaciones.csv\" los participantes y sus respectivas puntuaciones, asi");
        System.out.println(TextFormat.icons.help + "como tambien el ganador con los mejores pronosticos para los partidos dados.");
    }
}