package ferreteria.modelo;

/**
 * SUBCLASE — Cliente
 * Hereda de Persona. Representa a los compradores de la ferretería.
 */
public class Cliente extends Persona {

    private String direccion;

    public Cliente() { super(); }

    public Cliente(int id, String nombre, String contacto, String direccion) {
        super(id, nombre, contacto);
        this.direccion = direccion;
    }

    @Override
    public String getTipo() { return "CLIENTE"; }

    @Override
    public boolean validar() {
        return nombre != null && !nombre.isBlank();
    }

    public String getDireccion()          { return direccion; }
    public void   setDireccion(String d)  { this.direccion = d; }

    @Override
    public String toString() {
        return nombre + (contacto != null && !contacto.isBlank() ? " - " + contacto : "");
    }
}
