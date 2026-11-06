package ferreteria.dao;

import ferreteria.conexion.ConexionDB;
import ferreteria.modelo.Administrador;
import ferreteria.modelo.Usuario;
import ferreteria.modelo.Vendedor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO — Operaciones CRUD sobre la tabla usuario.
 */
public class UsuarioDAO {

    private Connection conn() { return ConexionDB.getConexion(); }

    // ── CREATE ─────────────────────────────────────────────────
    public boolean crear(Usuario u) {
        String sql = "INSERT INTO usuario (nombre, username, password, rol, activo) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getUsername());
            ps.setString(3, u.getPassword());
            ps.setString(4, u.getRol().name());
            ps.setBoolean(5, u.isActivo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ── READ ALL ───────────────────────────────────────────────
    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario ORDER BY nombre";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // ── READ ONE (autenticación) ───────────────────────────────
    public Usuario autenticar(String username, String password) {
        String sql = "SELECT * FROM usuario WHERE username=? AND password=? AND activo=TRUE";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ── UPDATE ─────────────────────────────────────────────────
    public boolean actualizar(Usuario u) {
        String sql = "UPDATE usuario SET nombre=?, username=?, password=?, rol=?, activo=? WHERE id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getUsername());
            ps.setString(3, u.getPassword());
            ps.setString(4, u.getRol().name());
            ps.setBoolean(5, u.isActivo());
            ps.setInt(6, u.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ── DELETE ─────────────────────────────────────────────────
    public boolean eliminar(int id) {
        String sql = "UPDATE usuario SET activo=FALSE WHERE id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ── MAPEO ──────────────────────────────────────────────────
    private Usuario mapear(ResultSet rs) throws SQLException {
        int     id       = rs.getInt("id");
        String  nombre   = rs.getString("nombre");
        String  username = rs.getString("username");
        String  password = rs.getString("password");
        String  rolStr   = rs.getString("rol");
        boolean activo   = rs.getBoolean("activo");

        if ("ADMIN".equals(rolStr)) {
            return new Administrador(id, nombre, null, username, password, activo);
        } else {
            return new Vendedor(id, nombre, null, username, password, activo);
        }
    }
}
