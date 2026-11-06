package ferreteria.vista;

import ferreteria.dao.UsuarioDAO;
import ferreteria.modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Ventana de inicio de sesión.
 * Autentica al usuario y redirige según su rol (Admin / Vendedor).
 */
public class LoginFrame extends JFrame {

    private JTextField     txtUsuario;
    private JPasswordField txtPassword;
    private JButton        btnIngresar;
    private UsuarioDAO     usuarioDAO;

    public LoginFrame() {
        usuarioDAO = new UsuarioDAO();
        initUI();
    }

    private void initUI() {
        setTitle("Ferretería — Inicio de Sesión");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 320);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(30, 30, 45));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel lblTitulo = new JLabel("🔧 FERRETERÍA", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(255, 165, 0));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);

        JLabel lblSub = new JLabel("Sistema de Gestión", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(Color.LIGHT_GRAY);
        gbc.gridy = 1;
        panel.add(lblSub, gbc);

        // Campo usuario
        gbc.gridwidth = 1; gbc.gridy = 2; gbc.gridx = 0;
        JLabel lblU = new JLabel("Usuario:");
        lblU.setForeground(Color.WHITE);
        panel.add(lblU, gbc);

        txtUsuario = new JTextField(15);
        estilizarCampo(txtUsuario);
        gbc.gridx = 1;
        panel.add(txtUsuario, gbc);

        // Campo contraseña
        gbc.gridy = 3; gbc.gridx = 0;
        JLabel lblP = new JLabel("Contraseña:");
        lblP.setForeground(Color.WHITE);
        panel.add(lblP, gbc);

        txtPassword = new JPasswordField(15);
        estilizarCampo(txtPassword);
        gbc.gridx = 1;
        panel.add(txtPassword, gbc);

        // Botón
        btnIngresar = new JButton("INGRESAR");
        btnIngresar.setBackground(new Color(255, 140, 0));
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnIngresar.setFocusPainted(false);
        btnIngresar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        panel.add(btnIngresar, gbc);

        // Hint
        JLabel hint = new JLabel("admin / admin123  |  vendedor / vendedor123", SwingConstants.CENTER);
        hint.setFont(new Font("Monospaced", Font.PLAIN, 10));
        hint.setForeground(new Color(100, 100, 130));
        gbc.gridy = 5;
        panel.add(hint, gbc);

        add(panel);

        // Acciones
        btnIngresar.addActionListener(e -> autenticar());
        txtPassword.addActionListener(e -> autenticar());
        txtUsuario.addActionListener(e -> txtPassword.requestFocus());
    }

    private void estilizarCampo(JTextField campo) {
        campo.setBackground(new Color(50, 50, 70));
        campo.setForeground(Color.WHITE);
        campo.setCaretColor(Color.WHITE);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 110)),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }

    private void autenticar() {
        String user = txtUsuario.getText().trim();
        String pass = new String(txtPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese usuario y contraseña.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario usuario = usuarioDAO.autenticar(user, pass);

        if (usuario != null) {
            dispose();
            new DashboardFrame(usuario).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Credenciales incorrectas.", "Error", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
        }
    }
}
