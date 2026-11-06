package ferreteria.vista;

import ferreteria.dao.UsuarioDAO;
import ferreteria.modelo.Administrador;
import ferreteria.modelo.Usuario;
import ferreteria.modelo.Vendedor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel de gestión de usuarios (solo Administrador).
 */
public class UsuarioPanel extends JPanel {

    private final UsuarioDAO       dao    = new UsuarioDAO();
    private       DefaultTableModel modelo;
    private       JTable            tabla;
    private JTextField     txtNombre, txtUsername, txtPassword;
    private JComboBox<String> cmbRol;
    private JButton btnGuardar, btnActualizar, btnDesactivar, btnLimpiar;

    public UsuarioPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(20, 20, 35));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initUI();
        cargarTabla();
    }

    private void initUI() {
        String[] cols = {"ID", "Nombre", "Username", "Rol", "Activo"};
        modelo = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        estilizarTabla(tabla);
        tabla.getSelectionModel().addListSelectionListener(e -> seleccionar());
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Formulario
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(30, 30, 50));
        form.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(155, 89, 182)),
            "Gestión de Usuarios", 0, 0, null, new Color(155, 89, 182)));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 8, 5, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        txtNombre   = campo(form, g, "Nombre:",    0);
        txtUsername = campo(form, g, "Username:",  1);
        txtPassword = campo(form, g, "Password:",  2);

        g.gridy = 3; g.gridx = 0;
        JLabel lRol = new JLabel("Rol:"); lRol.setForeground(Color.LIGHT_GRAY); form.add(lRol, g);
        g.gridx = 1;
        cmbRol = new JComboBox<>(new String[]{"ADMIN", "VENDEDOR"});
        cmbRol.setBackground(new Color(50,50,70)); cmbRol.setForeground(Color.WHITE);
        form.add(cmbRol, g);

        JPanel btns = new JPanel(new FlowLayout());
        btns.setBackground(new Color(30, 30, 50));
        btnGuardar    = btn("💾 Crear",      new Color(39, 174, 96));
        btnActualizar = btn("✏️ Actualizar", new Color(52, 152, 219));
        btnDesactivar = btn("🚫 Desactivar", new Color(231, 76, 60));
        btnLimpiar    = btn("🔄 Limpiar",    new Color(127, 140, 141));

        btns.add(btnGuardar); btns.add(btnActualizar); btns.add(btnDesactivar); btns.add(btnLimpiar);
        g.gridy = 4; g.gridx = 0; g.gridwidth = 2;
        form.add(btns, g);
        add(form, BorderLayout.EAST);

        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnDesactivar.addActionListener(e -> desactivar());
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
        String rol = (String) cmbRol.getSelectedItem();
        Usuario u = "ADMIN".equals(rol)
            ? new Administrador(0, txtNombre.getText().trim(), null, txtUsername.getText().trim(), txtPassword.getText().trim(), true)
            : new Vendedor(0, txtNombre.getText().trim(), null, txtUsername.getText().trim(), txtPassword.getText().trim(), true);
        if (!u.validar()) { err("Complete todos los campos."); return; }
        if (dao.crear(u)) { cargarTabla(); limpiar(); JOptionPane.showMessageDialog(this, "Usuario creado ✔"); }
    }

    private void actualizar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { err("Seleccione un usuario."); return; }
        int id  = (int) modelo.getValueAt(fila, 0);
        String rol = (String) cmbRol.getSelectedItem();
        Usuario u = "ADMIN".equals(rol)
            ? new Administrador(id, txtNombre.getText().trim(), null, txtUsername.getText().trim(), txtPassword.getText().trim(), true)
            : new Vendedor(id, txtNombre.getText().trim(), null, txtUsername.getText().trim(), txtPassword.getText().trim(), true);
        if (dao.actualizar(u)) { cargarTabla(); limpiar(); JOptionPane.showMessageDialog(this, "Actualizado ✔"); }
    }

    private void desactivar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { err("Seleccione un usuario."); return; }
        int id = (int) modelo.getValueAt(fila, 0);
        if (JOptionPane.showConfirmDialog(this, "¿Desactivar este usuario?", "Confirmar", JOptionPane.YES_NO_OPTION) == 0)
            if (dao.eliminar(id)) { cargarTabla(); limpiar(); }
    }

    private void seleccionar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        txtNombre.setText((String) modelo.getValueAt(fila, 1));
        txtUsername.setText((String) modelo.getValueAt(fila, 2));
        cmbRol.setSelectedItem(modelo.getValueAt(fila, 3));
    }

    private void cargarTabla() {
        modelo.setRowCount(0);
        for (Usuario u : dao.listar())
            modelo.addRow(new Object[]{u.getId(), u.getNombre(), u.getUsername(), u.getRol(), u.isActivo() ? "✅" : "❌"});
    }

    private void limpiar() {
        txtNombre.setText(""); txtUsername.setText(""); txtPassword.setText("");
        tabla.clearSelection();
    }

    private void err(String m) { JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }

    private void estilizarTabla(JTable t) {
        t.setBackground(new Color(30,30,50)); t.setForeground(Color.WHITE);
        t.setGridColor(new Color(60,60,80));
        t.getTableHeader().setBackground(new Color(155,89,182));
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
