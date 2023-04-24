package edu.utn.ar.utils;

import java.sql.*;
import java.util.List;

public class MySqlConnector {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static String host;
    private static String port;
    private static String db_name;
    private static String user;
    private static String pass;
    private static String db_url;
    private static List<Equipo> equipos;
    private static List<Partido> partidos;
    private static List<Pronostico> pronosticos;
    private static List<Participante> participantes;

    public MySqlConnector(List<Equipo> listaParametroDeEquipos, List<Partido> listaParametroDePartidos, List<Participante> listaParametroDeParticipantes, List<Pronostico> listaParametroDePronosticos){
        equipos = listaParametroDeEquipos;
        partidos = listaParametroDePartidos;
        participantes = listaParametroDeParticipantes;
        pronosticos = listaParametroDePronosticos;
    }
    public static void ejecutar() throws SQLException, ClassNotFoundException {
        // RECOMENDACION: DESDE JAVA, SOLAMENTE SELECCIONAR, INSERTAR,
        // MODIFICAR O ELIMINAR DATOS, PERO EL RESTO DE LA ESTRUCTURA DE LA BASE DE DATOS SE DEBE HACER DESDE WORKBENCH!
        System.out.println(TextFormat.colors.white + TextFormat.effects.bold + String.format("%1$-114s", "\n\t\t\t\t\t► ► ► CONEXION A MYSQL DB! ◄ ◄ ◄") + TextFormat.colors.reset);
        System.out.println(TextFormat.colors.cyan + String.format("%1$-128s", " ").replace(' ', '─') + TextFormat.colors.reset);
        System.out.println(TextFormat.icons.info + "MySQL Connector iniciando...");
        Class.forName(JDBC_DRIVER);
        Connection con = DriverManager.getConnection(db_url, user, pass);
        System.out.println(TextFormat.icons.info + "MySQL base de datos conectada...");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select * from pronostico");
        obtenerParticipantes(rs);
        rs = stmt.executeQuery("select * from pronostico");
        obtenerPronosticos(rs);
        // USO LA DB
        System.out.println( TextFormat.colors.blue + "\t └─ " + TextFormat.colors.reset
                + "MySQL base de datos desconectandose...");
        con.close();
    }

    private static void obtenerParticipantes(ResultSet rs){
        System.out.println(TextFormat.icons.info + "Descargando los datos de los participantes ...");
        try {
            while (rs.next()) {
                if (!ValidacionDeDatos.existeParticipante(participantes, rs.getInt("idParticipante"))){
                    participantes.add(
                            new Participante(rs.getInt("idParticipante"), rs.getString("nombreParticipante"))
                    );
                    System.out.println(TextFormat.colors.blue + "\t ├─ " + TextFormat.colors.reset +
                            rs.getInt("id") + " - " +
                            rs.getString("nombreParticipante") + " - " +
                            rs.getInt("idParticipante"));
                }
            }
        } catch (Exception e){
            System.out.println(TextFormat.icons.error + "Ocurrio un error durante la recepcion de los datos de la base de datos: "+ TextFormat.colors.red + e.getMessage());
        }
    }

    private static void obtenerPronosticos(ResultSet rs){
        System.out.println(TextFormat.icons.info + "Descargando los datos de pronosticos...");
        try {
            while (rs.next()) {
                if (!ValidacionDeDatos.existePronostico(pronosticos, String.valueOf(rs.getInt("idParticipante")),
                        rs.getString("equipoLocal"), rs.getString("equipoVisitante"))){
                    Participante participanteBuffer = ValidacionDeDatos.buscarRetornarParticipanteYaExistente(participantes, rs.getInt("idParticipante"));
                    Equipo local = ValidacionDeDatos.buscarRetornarEquipoExistente(equipos, rs.getString("equipoLocal"));
                    Equipo visitante = ValidacionDeDatos.buscarRetornarEquipoExistente(equipos, rs.getString("equipoVisitante"));
                    Partido partido = ValidacionDeDatos.buscarRetornarPartidoExistente(partidos, local.getNombre(), visitante.getNombre());
                    ResultadoEnum resultadoEquipoLocalBuffer;
                    ResultadoEnum resultadoEquipoVisitanteBuffer;
                    resultadoEquipoLocalBuffer = ResultadoEnum.EMPATE;
                    resultadoEquipoVisitanteBuffer = ResultadoEnum.EMPATE;
                    if (rs.getBoolean("ganaEquipoLocal")) {
                        resultadoEquipoLocalBuffer = ResultadoEnum.VICTORIA;
                        resultadoEquipoVisitanteBuffer = ResultadoEnum.DERROTA;
                    }
                    if (rs.getBoolean("ganaEquipoVisitante")) {
                        resultadoEquipoLocalBuffer = ResultadoEnum.DERROTA;
                        resultadoEquipoVisitanteBuffer = ResultadoEnum.VICTORIA;
                    }
                    System.out.println(TextFormat.colors.blue + "\t ├─ " + TextFormat.colors.reset +
                            rs.getInt("id") + " - " +
                            rs.getString("nombreParticipante") + " - " +
                            rs.getInt("idParticipante") + " - " +
                            rs.getString("equipoLocal") + " - " +
                            rs.getBoolean("ganaEquipoLocal") + " - " +
                            rs.getBoolean("empate") + " - " +
                            rs.getBoolean("ganaEquipoVisitante") + " - " +
                            rs.getString("equipoVisitante"));
                    pronosticos.add(
                            new Pronostico(
                                    participanteBuffer.getIdentificacionUnivoca(),
                                    participanteBuffer,
                                    partido,
                                    local,
                                    visitante,
                                    resultadoEquipoLocalBuffer,
                                    resultadoEquipoVisitanteBuffer
                            )
                    );
                }
            }
        } catch (Exception e){
            System.out.println(TextFormat.icons.error + "Ocurrio un error durante la recepcion de los datos de la base de datos: "+ TextFormat.colors.red + e.getMessage());
        }
    }
    public static List<Pronostico> getPronosticos() {
        return pronosticos;
    }
    public static List<Participante> getParticipantes() {
        return participantes;
    }
    public static String getHost() { return host; }
    public static String getPort() { return port; }
    public static String getDb_name() { return db_name; }
    public static void setUser(String user) { MySqlConnector.user = user; }
    public static void setPass(String pass) { MySqlConnector.pass = pass; }
    public static void setHost(String host) { MySqlConnector.host = host; }
    public static void setPort(String port) { MySqlConnector.port = port; }
    public static void setDb_name(String db_name) { MySqlConnector.db_name = db_name; }
    public static void setDb_url() { MySqlConnector.db_url = ("jdbc:mysql://" + getHost() + ":" + getPort() + "/" + getDb_name()); }
}
