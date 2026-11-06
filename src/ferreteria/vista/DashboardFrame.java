package ferreteria.vista;

import ferreteria.dao.ProductoDAO;
import ferreteria.dao.VentaDAO;
import ferreteria.modelo.Administrador;
import ferreteria.modelo.Producto;
import ferreteria.modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Ventana principal del sistema.
 * Muestra Dashboard + pestañas según el rol del usuario.
 */
public class DashboardFrame extends JFrame {

    private final Usuario       usuarioActual;
    private final ProductoDAO   productoDAO = new ProductoDAO();
    private final VentaDAO      ventaDAO    = new VentaDAO();

    public DashboardFrame(Usuario usuario) {
        this.usuarioActual = usuario;
        initUI();
    }

    private void initUI() {
        setTitle("Ferretería — " + usuarioActual.getTipo() + ": " + usuarioActual.getNombre());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(950, 650);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(20, 20, 35));

        // Barra superior
        JPanel barraTop = new JPanel(new BorderLayout());
        barraTop.setBackground(new Color(30, 30, 50));
        barraTop.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel lblTitulo = new JLabel("🔧 Sistema de Ferretería");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(255, 165, 0));
        barraTop.add(lblTitulo, BorderLayout.WEST);

        JLabel lblUser = new JLabel(usuarioActual.toString());
        lblUser.setForeground(Color.LIGHT_GRAY);
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        barraTop.add(lblUser, BorderLayout.EAST);
        add(barraTop, BorderLayout.NORTH);

        // Panel de métricas rápidas
        JPanel panelMetricas = construirMetricas();

        // Pestañas principales
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(new Color(30, 30, 50));
        tabs.setForeground(Color.WHITE);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabs.addTab("📊 Dashboard",   panelMetricas);
        tabs.addTab("📦 Productos",   new ProductoPanel(usuarioActual));
        tabs.addTab("👥 Clientes",    new ClientePanel(usuarioActual));
        tabs.addTab("🛒 Nueva Venta", new VentaPanel(usuarioActual));
        tabs.addTab("📋 Historial",   construirHistorial());

        if (usuarioActual instanceof Administrador) {
            tabs.addTab("🔑 Usuarios", new UsuarioPanel());
        }

        add(tabs, BorderLayout.CENTER);

        // Barra inferior
        JPanel barraBot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        barraBot.setBackground(new Color(20, 20, 35));
        JButton btnSalir = new JButton("Cerrar sesión");
        btnSalir.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        barraBot.add(btnSalir);
        add(barraBot, BorderLayout.SOUTH);
    }

    private JPanel construirMetricas() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(20, 20, 35));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Tarjetas de métricas
        JPanel tarjetas = new JPanel(new GridLayout(1, 4, 15, 0));
        tarjetas.setBackground(new Color(20, 20, 35));

        double totalHoy     = ventaDAO.totalVentasHoy();
        List<Producto> todos = productoDAO.listar();
        List<Producto> bajo  = productoDAO.listarStockBajo();

        tarjetas.add(crearTarjeta("💰 Ventas Hoy",
            "$ " + String.format("%,.0f", totalHoy), new Color(39, 174, 96)));
        tarjetas.add(crearTarjeta("📦 Productos",
            String.valueOf(todos.size()), new Color(52, 152, 219)));
        tarjetas.add(crearTarjeta("⚠️ Stock Bajo",
            String.valueOf(bajo.size()), bajo.isEmpty() ? new Color(46, 204, 113) : new Color(231, 76, 60)));
        tarjetas.add(crearTarjeta("👤 Sesión",
            usuarioActual.getTipo(), new Color(155, 89, 182)));

        panel.add(tarjetas, BorderLayout.NORTH);

        // Alerta de productos con stock bajo
        if (!bajo.isEmpty()) {
            JTextArea alertaArea = new JTextArea();
            alertaArea.setEditable(false);
            alertaArea.setBackground(new Color(50, 25, 25));
            alertaArea.setForeground(new Color(255, 100, 100));
            alertaArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            alertaArea.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 50, 50)),
                "⚠️  Productos con Stock Bajo",
                0, 0, null, new Color(255, 100, 100)
            ));
            StringBuilder sb = new StringBuilder();
            for (Producto p : bajo) {
                sb.append(String.format("  %-30s  Stock actual: %d  (mínimo: %d)%n",
                    p.getNombre(), p.getStock(), p.getStockMinimo()));
            }
            alertaArea.setText(sb.toString());
            panel.add(new JScrollPane(alertaArea), BorderLayout.CENTER);
        }

        return panel;
    }

    private JPanel crearTarjeta(String titulo, String valor, Color color) {
        JPanel tarjeta = new JPanel(new GridLayout(2, 1));
        tarjeta.setBackground(new Color(30, 30, 50));
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, color),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(Color.LIGHT_GRAY);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel lblValor = new JLabel(valor);
        lblValor.setForeground(color);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 20));

        tarjeta.add(lblTitulo);
        tarjeta.add(lblValor);
        return tarjeta;
    }

    private JPanel construirHistorial() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(20, 20, 35));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnas = {"ID", "Fecha", "Total", "Cliente", "Vendedor"};
        java.util.List<Object[]> datos = ventaDAO.listarResumen();
        Object[][] filas = datos.toArray(new Object[0][]);

        JTable tabla = new JTable(filas, columnas) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla.setBackground(new Color(30, 30, 50));
        tabla.setForeground(Color.WHITE);
        tabla.setGridColor(new Color(60, 60, 80));
        tabla.getTableHeader().setBackground(new Color(255, 140, 0));
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setRowHeight(24);

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }
}
