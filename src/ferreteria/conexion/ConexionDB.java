package ferreteria.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase Singleton para gestionar la conexión a MySQL.
 */
public class ConexionDB {

    // ── Configuración ──────────────────────────────────────────
    private static final String HOST     = "localhost";
    private static final String PORT     = "3306";
    private static final String DATABASE = "ferreteria";
    private static final String USER     = "root";
    private static final String PASSWORD = "";          // Cambiar según entorno

    private static final String URL =
        "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
        + "?useSSL=false&serverTimezone=America/Bogota&allowPublicKeyRetrieval=true";

    private static Connection instancia = null;

    private ConexionDB() {}

    /** Devuelve la conexión (la crea si no existe o está cerrada). */
    public static Connection getConexion() {
        try {
            if (instancia == null || instancia.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                instancia = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Driver MySQL no encontrado. Agrega mysql-connector-j al classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
        return instancia;
    }

    /** Cierra la conexión activa. */
    public static void cerrar() {
        try {
            if (instancia != null && !instancia.isClosed()) {
                instancia.close();
                instancia = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
