package ferreteria.modelo;

/**
 * SUBCLASE — Usuario
 * Hereda de Persona. Representa a los empleados del sistema.
 * Se especializa en: Administrador, Vendedor.
 */
public class Usuario extends Persona {

    public enum Rol { ADMIN, VENDEDOR }

    private String  username;
    private String  password;
    private Rol     rol;
    private boolean activo;

    public Usuario() { super(); }

    public Usuario(int id, String nombre, String contacto,
                   String username, String password, Rol rol, boolean activo) {
        super(id, nombre, contacto);
        this.username = username;
        this.password = password;
        this.rol      = rol;
        this.activo   = activo;
    }

    // ── Métodos heredados ──────────────────────────────────────
    @Override
    public String getTipo() {
        return rol != null ? rol.name() : "USUARIO";
    }

    @Override
    public boolean validar() {
        return nombre   != null && !nombre.isBlank()
            && username != null && !username.isBlank()
            && password != null && !password.isBlank()
            && rol      != null;
    }

    /** Verifica si las credenciales coinciden (comparación simple). */
    public boolean autenticar(String user, String pass) {
        return username.equals(user) && password.equals(pass);
    }

    // ── Getters / Setters ──────────────────────────────────────
    public String  getUsername() { return username; }
    public String  getPassword() { return password; }
    public Rol     getRol()      { return rol; }
    public boolean isActivo()    { return activo; }

    public void setUsername(String u) { this.username = u; }
    public void setPassword(String p) { this.password = p; }
    public void setRol(Rol r)         { this.rol      = r; }
    public void setActivo(boolean a)  { this.activo   = a; }
}
