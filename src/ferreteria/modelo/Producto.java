package ferreteria.modelo;

/**
 * Clase Producto — representa un artículo del inventario.
 */
public class Producto {

    private int    id;
    private String numeroSerie;
    private String nombre;
    private double precio;
    private int    stock;
    private int    stockMinimo;

    public Producto() {}

    public Producto(int id, String numeroSerie, String nombre,
                    double precio, int stock, int stockMinimo) {
        this.id          = id;
        this.numeroSerie = numeroSerie;
        this.nombre      = nombre;
        this.precio      = precio;
        this.stock       = stock;
        this.stockMinimo = stockMinimo;
    }

    /** Verifica si el stock está por debajo del mínimo. */
    public boolean stockBajo() {
        return stock <= stockMinimo;
    }

    /** Verifica si hay suficiente cantidad para una venta. */
    public boolean hayDisponibilidad(int cantidad) {
        return stock >= cantidad;
    }

    public boolean validar() {
        return numeroSerie != null && !numeroSerie.isBlank()
            && nombre != null && !nombre.isBlank()
            && precio > 0 && stock >= 0;
    }

    // ── Getters / Setters ──────────────────────────────────────
    public int    getId()           { return id; }
    public String getNumeroSerie()  { return numeroSerie; }
    public String getNombre()       { return nombre; }
    public double getPrecio()       { return precio; }
    public int    getStock()        { return stock; }
    public int    getStockMinimo()  { return stockMinimo; }

    public void setId(int id)                  { this.id          = id; }
    public void setNumeroSerie(String ns)      { this.numeroSerie  = ns; }
    public void setNombre(String n)            { this.nombre       = n; }
    public void setPrecio(double p)            { this.precio       = p; }
    public void setStock(int s)                { this.stock        = s; }
    public void setStockMinimo(int sm)         { this.stockMinimo  = sm; }

    @Override
    public String toString() {
        return "[" + numeroSerie + "] " + nombre + " - $" + String.format("%.0f", precio)
             + " (Stock: " + stock + (stockBajo() ? " ⚠️" : "") + ")";
    }
}
