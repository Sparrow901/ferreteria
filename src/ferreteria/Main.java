package ferreteria;

import ferreteria.vista.LoginFrame;

import javax.swing.*;

/**
 * ════════════════════════════════════════════════════════════
 *  SISTEMA DE GESTIÓN DE FERRETERÍA
 *  Proyecto Integrador — Tercer Semestre
 *  Jean Alexis Chara | Ronald Hurtado | Deibit Arley Urbano
 *  Curso 312
 * ════════════════════════════════════════════════════════════
 *
 * REQUISITOS:
 *   - JDK 11 o superior
 *   - MySQL 8.x corriendo en localhost:3306
 *   - mysql-connector-j (descarga: https://dev.mysql.com/downloads/connector/j/)
 *
 * PASOS PARA EJECUTAR:
 *   1. Ejecutar schema.sql en tu MySQL (MySQL Workbench o terminal)
 *   2. Ajustar usuario/contraseña en ConexionDB.java si es necesario
 *   3. Compilar: javac -cp ".;mysql-connector-j-8.x.x.jar" -d out src/ferreteria/**\/*.java
 *      (Linux/Mac usa : en vez de ; en el classpath)
 *   4. Ejecutar: java -cp ".;mysql-connector-j-8.x.x.jar;out" ferreteria.Main
 */
public class Main {
    public static void main(String[] args) {
        // Usar Look & Feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Continúa con Look & Feel por defecto
        }

        // Ejecutar en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}
