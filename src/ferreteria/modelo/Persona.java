package ferreteria.modelo;

/**
 * SUPERCLASE ABSTRACTA — Persona
 * Base de la jerarquía de clases del sistema.
 * Heredan: Usuario, Cliente
 */
public abstract class Persona {

    protected int    id;
    protected String nombre;
    protected String contacto;

    public Persona() {}

    public Persona(int id, String nombre, String contacto) {
        this.id       = id;
        this.nombre   = nombre;
        this.contacto = contacto;
    }

    // ── Métodos abstractos ─────────────────────────────────────
    /** Devuelve una descripción del tipo de persona. */
    public abstract String getTipo();

    /** Valida que los campos obligatorios estén correctos. */
    public abstract boolean validar();

    // ── Getters / Setters ──────────────────────────────────────
    public int    getId()       { return id; }
    public String getNombre()   { return nombre; }
    public String getContacto() { return contacto; }

    public void setId(int id)             { this.id       = id; }
    public void setNombre(String nombre)  { this.nombre   = nombre; }
    public void setContacto(String c)     { this.contacto = c; }

    @Override
    public String toString() {
        return "[" + getTipo() + "] " + nombre;
    }
}
