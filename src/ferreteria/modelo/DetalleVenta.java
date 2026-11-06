package ferreteria.modelo;

/**
 * Clase DetalleVenta — cada ítem dentro de una venta.
 */
public class DetalleVenta {

    private int     id;
    private int     ventaId;
    private Producto producto;
    private int     cantidad;
    private double  precioUnitario;
    private double  descuento;
    private double  subtotal;

    public DetalleVenta() {}

    public DetalleVenta(Producto producto, int cantidad, double descuento) {
        this.producto       = producto;
        this.cantidad       = cantidad;
        this.precioUnitario = producto.getPrecio();
        this.descuento      = descuento;
        calcularSubtotal();
    }

    /** Calcula subtotal aplicando descuento porcentual. */
    public void calcularSubtotal() {
        double bruto = precioUnitario * cantidad;
        subtotal = bruto - (bruto * descuento / 100.0);
    }

    // ── Getters / Setters ──────────────────────────────────────
    public int      getId()             { return id; }
    public int      getVentaId()        { return ventaId; }
    public Producto getProducto()       { return producto; }
    public int      getCantidad()       { return cantidad; }
    public double   getPrecioUnitario() { return precioUnitario; }
    public double   getDescuento()      { return descuento; }
    public double   getSubtotal()       { return subtotal; }

    public void setId(int id)               { this.id             = id; }
    public void setVentaId(int v)           { this.ventaId        = v; }
    public void setProducto(Producto p)     { this.producto       = p; }
    public void setCantidad(int c)          { this.cantidad       = c; calcularSubtotal(); }
    public void setPrecioUnitario(double p) { this.precioUnitario = p; calcularSubtotal(); }
    public void setDescuento(double d)      { this.descuento      = d; calcularSubtotal(); }
    public void setSubtotal(double s)       { this.subtotal       = s; }
}
