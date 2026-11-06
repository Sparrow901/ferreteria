package ferreteria.vista;

import ferreteria.dao.ClienteDAO;
import ferreteria.modelo.Administrador;
import ferreteria.modelo.Cliente;
import ferreteria.modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel de gestión de clientes (CRUD).
 */
public class ClientePanel extends JPanel {

    private final ClienteDAO       dao     = new ClienteDAO();
    private final boolean          esAdmin;
    private       DefaultTableModel modelo;
    private       JTable            tabla;
    private JTextField txtNombre, txtContacto, txtDireccion;
    private JButton    btnGuardar, btnActualizar, btnEliminar, btnLimpiar;

    public ClientePanel(Usuario usuario) {
        this.esAdmin = (usuario instanceof Administrador);
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(20, 20, 35));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initUI();
        cargarTabla();
    }

    private void initUI() {
        String[] cols = {"ID", "Nombre", "Contacto", "Dirección"};
        modelo = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        estilizarTabla(tabla);
        tabla.getSelectionModel().addListSelectionListener(e -> seleccionar());
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Formulario (Admin y Vendedor pueden crear clientes)
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(30, 30, 50));
        form.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219)),
            "Datos del Cliente", 0, 0, null, new Color(52, 152, 219)));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 8, 5, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        txtNombre    = campo(form, g, "Nombre:",    0);
        txtContacto  = campo(form, g, "Contacto:",  1);
        txtDireccion = campo(form, g, "Dirección:", 2);

        JPanel btns = new JPanel(new FlowLayout());
        btns.setBackground(new Color(30, 30, 50));
        btnGuardar    = btn("💾 Guardar",    new Color(39, 174, 96));
        btnActualizar = btn("✏️ Actualizar", new Color(52, 152, 219));
        btnEliminar   = btn("🗑️ Eliminar",   new Color(231, 76, 60));
        btnLimpiar    = btn("🔄 Limpiar",    new Color(127, 140, 141));

        btnEliminar.setEnabled(esAdmin);
        btnActualizar.setEnabled(esAdmin);
        btns.add(btnGuardar); btns.add(btnActualizar); btns.add(btnEliminar); btns.add(btnLimpiar);

        g.gridy = 3; g.gridx = 0; g.gridwidth = 2;
        form.add(btns, g);
        add(form, BorderLayout.EAST);

        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
    }

    private JTextField campo(JPanel p, GridBagConstraints g, String lbl, int fila) {
        g.gridy = fila; g.gridx = 0; g.gridwidth = 1;
        JLabel l = new JLabel(lbl); l.setForeground(Color.LIGHT_GRAY); p.add(l, g);
        g.gridx = 1;
        JTextField txt = new JTextField(14);
        txt.setBackground(new Color(50,50,70)); txt.setForeground(Color.WHITE); txt.setCaretColor(Color.WHITE);
        p.add(txt, g);
        return txt;
    }

    private void guardar() {
        Cliente c = new Cliente(0, txtNombre.getText().trim(), txtContacto.getText().trim(), txtDireccion.getText().trim());
        if (!c.validar()) { err("El nombre es obligatorio."); return; }
        if (dao.crear(c)) { cargarTabla(); limpiar(); JOptionPane.showMessageDialog(this, "Cliente registrado ✔"); }
    }

    private void actualizar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { err("Seleccione un cliente."); return; }
        int id = (int) modelo.getValueAt(fila, 0);
        Cliente c = new Cliente(id, txtNombre.getText().trim(), txtContacto.getText().trim(), txtDireccion.getText().trim());
        if (dao.actualizar(c)) { cargarTabla(); limpiar(); JOptionPane.showMessageDialog(this, "Actualizado ✔"); }
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { err("Seleccione un cliente."); return; }
        int id = (int) modelo.getValueAt(fila, 0);
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar?", "Confirmar", JOptionPane.YES_NO_OPTION) == 0)
            if (dao.eliminar(id)) { cargarTabla(); limpiar(); }
    }

    private void seleccionar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        txtNombre.setText((String) modelo.getValueAt(fila, 1));
        txtContacto.setText((String) modelo.getValueAt(fila, 2));
        txtDireccion.setText((String) modelo.getValueAt(fila, 3));
    }

    private void cargarTabla() {
        modelo.setRowCount(0);
        for (Cliente c : dao.listar())
            modelo.addRow(new Object[]{c.getId(), c.getNombre(), c.getContacto(), c.getDireccion()});
    }

    private void limpiar() {
        txtNombre.setText(""); txtContacto.setText(""); txtDireccion.setText("");
        tabla.clearSelection();
    }

    private void err(String m) { JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }

    private void estilizarTabla(JTable t) {
        t.setBackground(new Color(30,30,50)); t.setForeground(Color.WHITE);
        t.setGridColor(new Color(60,60,80));
        t.getTableHeader().setBackground(new Color(52,152,219));
        t.getTableHeader().setForeground(Color.WHITE);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12)); t.setRowHeight(24);
    }

    private JButton btn(String text, Color color) {
        JButton b = new JButton(text);
        b.setBackground(color); b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12)); b.setFocusPainted(false);
        return b;
    }
}
