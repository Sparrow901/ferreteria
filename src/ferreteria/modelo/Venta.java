package ferreteria.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase Venta — representa una transacción de venta.
 */
public class Venta {

    private int               id;
    private LocalDateTime     fecha;
    private double            total;
    private Cliente           cliente;
    private Usuario           vendedor;
    private List<DetalleVenta> detalles;

    public Venta() {
        this.fecha    = LocalDateTime.now();
        this.detalles = new ArrayList<>();
    }

    public Venta(int id, LocalDateTime fecha, double total, Cliente cliente, Usuario vendedor) {
        this.id = id; this.fecha = fecha; this.total = total;
        this.cliente = cliente; this.vendedor = vendedor;
        this.detalles = new ArrayList<>();
    }

    public double calcularTotal() {
        total = detalles.stream().mapToDouble(DetalleVenta::getSubtotal).sum();
        return total;
    }

    public void agregarDetalle(DetalleVenta detalle) {
        detalles.add(detalle);
        calcularTotal();
    }

    public String getFechaFormateada() {
        if (fecha == null) return "";
        return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public int getId() { return id; }
    public LocalDateTime getFecha() { return fecha; }
    public double getTotal() { return total; }
    public Cliente getCliente() { return cliente; }
    public Usuario getVendedor() { return vendedor; }
    public List<DetalleVenta> getDetalles() { return detalles; }
    public void setId(int id) { this.id = id; }
    public void setFecha(LocalDateTime f) { this.fecha = f; }
    public void setTotal(double t) { this.total = t; }
    public void setCliente(Cliente c) { this.cliente = c; }
    public void setVendedor(Usuario v) { this.vendedor = v; }
    public void setDetalles(List<DetalleVenta> d) { this.detalles = d; }
}
