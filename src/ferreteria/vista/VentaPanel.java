package ferreteria.vista;

import ferreteria.dao.ClienteDAO;
import ferreteria.dao.ProductoDAO;
import ferreteria.dao.VentaDAO;
import ferreteria.modelo.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel de registro de ventas.
 * Permite seleccionar cliente, agregar productos y procesar la venta.
 */
public class VentaPanel extends JPanel {

    private final Usuario       usuarioActual;
    private final ProductoDAO   productoDAO = new ProductoDAO();
    private final ClienteDAO    clienteDAO  = new ClienteDAO();
    private final VentaDAO      ventaDAO    = new VentaDAO();
    private final Venta         ventaActual = new Venta();

    private JComboBox<Cliente>  cmbCliente;
    private JComboBox<Producto> cmbProducto;
    private JSpinner            spnCantidad;
    private JTextField          txtDescuento;
    private DefaultTableModel   modeloDetalle;
    private JLabel              lblTotal;

    public VentaPanel(Usuario usuario) {
        this.usuarioActual = usuario;
        ventaActual.setVendedor(usuario);
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(20, 20, 35));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initUI();
    }

    private void initUI() {
        // ── Panel superior: cliente + producto ─────────────────
        JPanel panelTop = new JPanel(new GridBagLayout());
        panelTop.setBackground(new Color(30, 30, 50));
        panelTop.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 165, 0)),
            "Nueva Venta", 0, 0, null, new Color(255, 165, 0)));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 8, 5, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        // Cliente
        g.gridy = 0; g.gridx = 0;
        JLabel lCliente = new JLabel("Cliente:"); lCliente.setForeground(Color.LIGHT_GRAY);
        panelTop.add(lCliente, g);
        g.gridx = 1;
        cmbCliente = new JComboBox<>();
        clienteDAO.listar().forEach(c -> cmbCliente.addItem(c));
        estilizarCombo(cmbCliente);
        panelTop.add(cmbCliente, g);

        // Producto
        g.gridy = 1; g.gridx = 0;
        JLabel lProd = new JLabel("Producto:"); lProd.setForeground(Color.LIGHT_GRAY);
        panelTop.add(lProd, g);
        g.gridx = 1;
        cmbProducto = new JComboBox<>();
        productoDAO.listar().forEach(p -> cmbProducto.addItem(p));
        estilizarCombo(cmbProducto);
        panelTop.add(cmbProducto, g);

        // Cantidad y descuento
        g.gridy = 2; g.gridx = 0;
        JLabel lCant = new JLabel("Cantidad:"); lCant.setForeground(Color.LIGHT_GRAY);
        panelTop.add(lCant, g);
        g.gridx = 1;
        spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        panelTop.add(spnCantidad, g);

        g.gridy = 3; g.gridx = 0;
        JLabel lDesc = new JLabel("Descuento %:"); lDesc.setForeground(Color.LIGHT_GRAY);
        panelTop.add(lDesc, g);
        g.gridx = 1;
        txtDescuento = new JTextField("0");
        estilizarCampo(txtDescuento);
        panelTop.add(txtDescuento, g);

        g.gridy = 4; g.gridx = 0; g.gridwidth = 2;
        JButton btnAgregar = new JButton("➕ Agregar Producto");
        btnAgregar.setBackground(new Color(52, 152, 219));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAgregar.setFocusPainted(false);
        panelTop.add(btnAgregar, g);
        btnAgregar.addActionListener(e -> agregarProducto());

        add(panelTop, BorderLayout.NORTH);

        // ── Tabla de detalle ───────────────────────────────────
        String[] cols = {"Producto", "Cantidad", "Precio Unit.", "Descuento %", "Subtotal"};
        modeloDetalle = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tablaDetalle = new JTable(modeloDetalle);
        tablaDetalle.setBackground(new Color(30, 30, 50));
        tablaDetalle.setForeground(Color.WHITE);
        tablaDetalle.setGridColor(new Color(60, 60, 80));
        tablaDetalle.getTableHeader().setBackground(new Color(255, 140, 0));
        tablaDetalle.getTableHeader().setForeground(Color.WHITE);
        tablaDetalle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaDetalle.setRowHeight(24);
        add(new JScrollPane(tablaDetalle), BorderLayout.CENTER);

        // ── Panel inferior: total + botón ──────────────────────
        JPanel panelBot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBot.setBackground(new Color(20, 20, 35));

        lblTotal = new JLabel("TOTAL: $0");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotal.setForeground(new Color(255, 165, 0));
        panelBot.add(lblTotal);

        JButton btnClear = new JButton("🗑️ Limpiar");
        btnClear.addActionListener(e -> limpiarVenta());
        btnClear.setBackground(new Color(127, 140, 141));
        btnClear.setForeground(Color.WHITE);
        btnClear.setFocusPainted(false);
        panelBot.add(btnClear);

        JButton btnRegistrar = new JButton("✅ Registrar Venta");
        btnRegistrar.setBackground(new Color(39, 174, 96));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.addActionListener(e -> registrarVenta());
        panelBot.add(btnRegistrar);

        add(panelBot, BorderLayout.SOUTH);
    }

    private void agregarProducto() {
        Producto producto = (Producto) cmbProducto.getSelectedItem();
        if (producto == null) return;

        int cantidad = (int) spnCantidad.getValue();
        double descuento;
        try { descuento = Double.parseDouble(txtDescuento.getText()); }
        catch (NumberFormatException e) { JOptionPane.showMessageDialog(this, "Descuento inválido."); return; }

        // Validar disponibilidad
        Producto actualizado = productoDAO.buscarPorId(producto.getId());
        if (!actualizado.hayDisponibilidad(cantidad)) {
            JOptionPane.showMessageDialog(this,
                "Stock insuficiente. Disponible: " + actualizado.getStock(),
                "Sin Stock", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar descuento del vendedor
        if (usuarioActual instanceof Vendedor) {
            Vendedor v = (Vendedor) usuarioActual;
            if (!v.descuentoPermitido(descuento)) {
                JOptionPane.showMessageDialog(this,
                    "Descuento máximo permitido: " + v.getDescuentoMaximo() + "%",
                    "Descuento Excedido", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        DetalleVenta detalle = new DetalleVenta(actualizado, cantidad, descuento);
        ventaActual.agregarDetalle(detalle);

        modeloDetalle.addRow(new Object[]{
            actualizado.getNombre(), cantidad,
            String.format("$%.0f", actualizado.getPrecio()),
            descuento + "%",
            String.format("$%.0f", detalle.getSubtotal())
        });

        lblTotal.setText("TOTAL: $" + String.format("%.0f", ventaActual.getTotal()));
    }

    private void registrarVenta() {
        if (ventaActual.getDetalles().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Agregue al menos un producto.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Cliente cliente = (Cliente) cmbCliente.getSelectedItem();
        ventaActual.setCliente(cliente);

        boolean ok = ventaDAO.registrarVenta(ventaActual);
        if (ok) {
            // Verificar stock bajo
            StringBuilder alertas = new StringBuilder();
            for (DetalleVenta dv : ventaActual.getDetalles()) {
                Producto p = productoDAO.buscarPorId(dv.getProducto().getId());
                if (p != null && p.stockBajo()) {
                    alertas.append("⚠️ ").append(p.getNombre())
                           .append(" — Stock restante: ").append(p.getStock()).append("\n");
                }
            }
            String msg = "✅ Venta registrada. Total: $" + String.format("%.0f", ventaActual.getTotal());
            if (alertas.length() > 0) msg += "\n\nALERTA DE STOCK:\n" + alertas;
            JOptionPane.showMessageDialog(this, msg, "Venta Exitosa", JOptionPane.INFORMATION_MESSAGE);
            limpiarVenta();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar la venta.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarVenta() {
        ventaActual.getDetalles().clear();
        ventaActual.setTotal(0);
        modeloDetalle.setRowCount(0);
        lblTotal.setText("TOTAL: $0");
        spnCantidad.setValue(1);
        txtDescuento.setText("0");
    }

    private void estilizarCombo(JComboBox<?> c) {
        c.setBackground(new Color(50,50,70)); c.setForeground(Color.WHITE);
        c.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    private void estilizarCampo(JTextField f) {
        f.setBackground(new Color(50,50,70)); f.setForeground(Color.WHITE); f.setCaretColor(Color.WHITE);
    }
}
