package ferreteria.vista;

import ferreteria.dao.ProductoDAO;
import ferreteria.modelo.Administrador;
import ferreteria.modelo.Producto;
import ferreteria.modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel de gestión de productos (CRUD).
 * Admin: CRUD completo. Vendedor: solo lectura.
 */
public class ProductoPanel extends JPanel {

    private final ProductoDAO     dao     = new ProductoDAO();
    private final boolean         esAdmin;
    private       DefaultTableModel modelo;
    private       JTable           tabla;

    // Campos del formulario
    private JTextField txtSerie, txtNombre, txtPrecio, txtStock, txtStockMin;
    private JButton    btnGuardar, btnActualizar, btnEliminar, btnLimpiar;

    public ProductoPanel(Usuario usuario) {
        this.esAdmin = (usuario instanceof Administrador);
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(20, 20, 35));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initUI();
        cargarTabla();
    }

    private void initUI() {
        // ── Tabla ──────────────────────────────────────────────
        String[] cols = {"ID", "Serie", "Nombre", "Precio", "Stock", "Stock Mín."};
        modelo = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        estilizarTabla(tabla);
        tabla.getSelectionModel().addListSelectionListener(e -> seleccionarFila());
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        if (!esAdmin) return;  // Solo admin ve el formulario

        // ── Formulario ─────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(30, 30, 50));
        form.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 140, 0)),
            "Gestión de Productos", 0, 0, null, new Color(255, 140, 0)));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 8, 5, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        txtSerie    = agregarCampo(form, g, "N° Serie:",   0);
        txtNombre   = agregarCampo(form, g, "Nombre:",     1);
        txtPrecio   = agregarCampo(form, g, "Precio:",     2);
        txtStock    = agregarCampo(form, g, "Stock:",      3);
        txtStockMin = agregarCampo(form, g, "Stock Mín.:", 4);

        // Botones
        JPanel botones = new JPanel(new FlowLayout());
        botones.setBackground(new Color(30, 30, 50));

        btnGuardar    = boton("💾 Guardar",    new Color(39, 174, 96));
        btnActualizar = boton("✏️ Actualizar", new Color(52, 152, 219));
        btnEliminar   = boton("🗑️ Eliminar",   new Color(231, 76, 60));
        btnLimpiar    = boton("🔄 Limpiar",    new Color(127, 140, 141));

        botones.add(btnGuardar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);

        g.gridy = 5; g.gridx = 0; g.gridwidth = 2;
        form.add(botones, g);

        add(form, BorderLayout.EAST);

        // Acciones
        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
    }

    private JTextField agregarCampo(JPanel p, GridBagConstraints g, String label, int fila) {
        g.gridy = fila; g.gridx = 0; g.gridwidth = 1;
        JLabel lbl = new JLabel(label);
        lbl.setForeground(Color.LIGHT_GRAY);
        p.add(lbl, g);
        g.gridx = 1;
        JTextField txt = new JTextField(14);
        estilizarCampo(txt);
        p.add(txt, g);
        return txt;
    }

    private void guardar() {
        try {
            Producto p = new Producto(0,
                txtSerie.getText().trim(), txtNombre.getText().trim(),
                Double.parseDouble(txtPrecio.getText()),
                Integer.parseInt(txtStock.getText()),
                Integer.parseInt(txtStockMin.getText()));
            if (!p.validar()) { mostrarError("Datos inválidos."); return; }
            if (dao.crear(p)) { cargarTabla(); limpiar(); JOptionPane.showMessageDialog(this, "Producto guardado ✔"); }
        } catch (NumberFormatException e) { mostrarError("Precio y stock deben ser numéricos."); }
    }

    private void actualizar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { mostrarError("Seleccione un producto."); return; }
        int id = (int) modelo.getValueAt(fila, 0);
        try {
            Producto p = new Producto(id,
                txtSerie.getText().trim(), txtNombre.getText().trim(),
                Double.parseDouble(txtPrecio.getText()),
                Integer.parseInt(txtStock.getText()),
                Integer.parseInt(txtStockMin.getText()));
            if (dao.actualizar(p)) { cargarTabla(); limpiar(); JOptionPane.showMessageDialog(this, "Actualizado ✔"); }
        } catch (NumberFormatException e) { mostrarError("Precio y stock deben ser numéricos."); }
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { mostrarError("Seleccione un producto."); return; }
        int id = (int) modelo.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar este producto?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION && dao.eliminar(id)) { cargarTabla(); limpiar(); }
    }

    private void seleccionarFila() {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || !esAdmin) return;
        txtSerie.setText((String) modelo.getValueAt(fila, 1));
        txtNombre.setText((String) modelo.getValueAt(fila, 2));
        txtPrecio.setText(String.valueOf(modelo.getValueAt(fila, 3)));
        txtStock.setText(String.valueOf(modelo.getValueAt(fila, 4)));
        txtStockMin.setText(String.valueOf(modelo.getValueAt(fila, 5)));
    }

    private void cargarTabla() {
        modelo.setRowCount(0);
        List<Producto> lista = dao.listar();
        for (Producto p : lista) {
            modelo.addRow(new Object[]{
                p.getId(), p.getNumeroSerie(), p.getNombre(),
                p.getPrecio(), p.getStock(), p.getStockMinimo()
            });
        }
    }

    private void limpiar() {
        if (txtSerie != null) { txtSerie.setText(""); txtNombre.setText(""); txtPrecio.setText(""); txtStock.setText(""); txtStockMin.setText(""); }
        tabla.clearSelection();
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void estilizarTabla(JTable t) {
        t.setBackground(new Color(30, 30, 50));
        t.setForeground(Color.WHITE);
        t.setGridColor(new Color(60, 60, 80));
        t.getTableHeader().setBackground(new Color(255, 140, 0));
        t.getTableHeader().setForeground(Color.WHITE);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.setRowHeight(24);
    }

    private void estilizarCampo(JTextField f) {
        f.setBackground(new Color(50, 50, 70));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 110)),
            BorderFactory.createEmptyBorder(3, 6, 3, 6)));
    }

    private JButton boton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        return btn;
    }
}
