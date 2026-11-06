package ferreteria.modelo;

/**
 * SUBCLASE — Administrador
 * Hereda de Usuario. Nivel de acceso ALTO.
 * Puede gestionar productos, clientes, usuarios y consultar ventas.
 */
public class Administrador extends Usuario {

    public Administrador() {
        super();
        setRol(Rol.ADMIN);
    }

    public Administrador(int id, String nombre, String contacto,
                         String username, String password, boolean activo) {
        super(id, nombre, contacto, username, password, Rol.ADMIN, activo);
    }

    @Override
    public String getTipo() { return "ADMINISTRADOR"; }

    /** Los administradores tienen acceso total. */
    public boolean puedeEliminarUsuario()  { return true; }
    public boolean puedeVerReportes()      { return true; }
    public boolean puedeGestionarStock()   { return true; }

    @Override
    public String toString() {
        return "👑 [ADMIN] " + getNombre() + " (" + getUsername() + ")";
    }
}
