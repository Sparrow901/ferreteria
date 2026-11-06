package ferreteria.modelo;

/**
 * SUBCLASE — Vendedor
 * Hereda de Usuario. Nivel de acceso MEDIO.
 * Puede registrar ventas, consultar productos y registrar clientes.
 */
public class Vendedor extends Usuario {

    private double descuentoMaximo; // % máximo que puede aplicar

    public Vendedor() {
        super();
        setRol(Rol.VENDEDOR);
        this.descuentoMaximo = 10.0;
    }

    public Vendedor(int id, String nombre, String contacto,
                    String username, String password, boolean activo) {
        super(id, nombre, contacto, username, password, Rol.VENDEDOR, activo);
        this.descuentoMaximo = 10.0;
    }

    @Override
    public String getTipo() { return "VENDEDOR"; }

    /** Valida si el descuento propuesto está dentro del límite. */
    public boolean descuentoPermitido(double porcentaje) {
        return porcentaje >= 0 && porcentaje <= descuentoMaximo;
    }

    public double getDescuentoMaximo()          { return descuentoMaximo; }
    public void   setDescuentoMaximo(double d)  { this.descuentoMaximo = d; }

    @Override
    public String toString() {
        return "🛒 [VENDEDOR] " + getNombre() + " (" + getUsername() + ")";
    }
}
