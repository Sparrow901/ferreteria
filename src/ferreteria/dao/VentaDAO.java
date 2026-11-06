package ferreteria.dao;

import ferreteria.conexion.ConexionDB;
import ferreteria.modelo.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO — Registra ventas y sus detalles.
 */
public class VentaDAO {

    private Connection conn() { return ConexionDB.getConexion(); }

    /**
     * Registra la venta y sus detalles en una sola transacción.
     * El trigger de MySQL actualiza el stock automáticamente.
     */
    public boolean registrarVenta(Venta venta) {
        String sqlVenta   = "INSERT INTO venta (fecha, total, cliente_id, usuario_id) VALUES (NOW(),?,?,?)";
        String sqlDetalle = "INSERT INTO detalle_venta (venta_id, producto_id, cantidad, precio_unitario, descuento, subtotal) VALUES (?,?,?,?,?,?)";

        try {
            conn().setAutoCommit(false);

            // 1. Insertar cabecera de venta
            int ventaId;
            try (PreparedStatement ps = conn().prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
                ps.setDouble(1, venta.getTotal());
                ps.setObject(2, venta.getCliente() != null ? venta.getCliente().getId() : null);
                ps.setInt(3, venta.getVendedor().getId());
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                keys.next();
                ventaId = keys.getInt(1);
                venta.setId(ventaId);
            }

            // 2. Insertar detalles (el trigger actualiza stock)
            try (PreparedStatement ps = conn().prepareStatement(sqlDetalle)) {
                for (DetalleVenta dv : venta.getDetalles()) {
                    ps.setInt(1, ventaId);
                    ps.setInt(2, dv.getProducto().getId());
                    ps.setInt(3, dv.getCantidad());
                    ps.setDouble(4, dv.getPrecioUnitario());
                    ps.setDouble(5, dv.getDescuento());
                    ps.setDouble(6, dv.getSubtotal());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn().commit();
            return true;

        } catch (SQLException e) {
            try { conn().rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { conn().setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    /** Lista todas las ventas con información básica. */
    public List<Object[]> listarResumen() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT v.id, v.fecha, v.total, c.nombre AS cliente, u.nombre AS vendedor " +
                     "FROM venta v " +
                     "LEFT JOIN cliente c ON v.cliente_id = c.id " +
                     "JOIN usuario u ON v.usuario_id = u.id " +
                     "ORDER BY v.fecha DESC";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("id"),
                    rs.getTimestamp("fecha"),
                    rs.getDouble("total"),
                    rs.getString("cliente"),
                    rs.getString("vendedor")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    /** Cuenta el total de ventas del día. */
    public double totalVentasHoy() {
        String sql = "SELECT IFNULL(SUM(total),0) FROM venta WHERE DATE(fecha) = CURDATE()";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}
