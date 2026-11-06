package ferreteria.dao;

import ferreteria.conexion.ConexionDB;
import ferreteria.modelo.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO — Operaciones CRUD sobre la tabla producto.
 */
public class ProductoDAO {

    private Connection conn() { return ConexionDB.getConexion(); }

    // ── CREATE ─────────────────────────────────────────────────
    public boolean crear(Producto p) {
        String sql = "INSERT INTO producto (numero_serie, nombre, precio, stock, stock_minimo) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNumeroSerie());
            ps.setString(2, p.getNombre());
            ps.setDouble(3, p.getPrecio());
            ps.setInt(4, p.getStock());
            ps.setInt(5, p.getStockMinimo());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) p.setId(keys.getInt(1));
            }
            return rows > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ── READ ALL ───────────────────────────────────────────────
    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM producto ORDER BY nombre";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // ── READ — stock bajo ──────────────────────────────────────
    public List<Producto> listarStockBajo() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM producto WHERE stock <= stock_minimo ORDER BY stock";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // ── READ by ID ─────────────────────────────────────────────
    public Producto buscarPorId(int id) {
        String sql = "SELECT * FROM producto WHERE id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ── UPDATE ─────────────────────────────────────────────────
    public boolean actualizar(Producto p) {
        String sql = "UPDATE producto SET numero_serie=?, nombre=?, precio=?, stock=?, stock_minimo=? WHERE id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, p.getNumeroSerie());
            ps.setString(2, p.getNombre());
            ps.setDouble(3, p.getPrecio());
            ps.setInt(4, p.getStock());
            ps.setInt(5, p.getStockMinimo());
            ps.setInt(6, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ── DELETE ─────────────────────────────────────────────────
    public boolean eliminar(int id) {
        String sql = "DELETE FROM producto WHERE id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ── MAPEO ──────────────────────────────────────────────────
    private Producto mapear(ResultSet rs) throws SQLException {
        return new Producto(
            rs.getInt("id"),
            rs.getString("numero_serie"),
            rs.getString("nombre"),
            rs.getDouble("precio"),
            rs.getInt("stock"),
            rs.getInt("stock_minimo")
        );
    }
}
