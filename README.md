# 🔧 Sistema de Gestión de Ferretería
**Proyecto Integrador — Tercer Semestre**
Jean Alexis Chara | Ronald Hurtado | Deibit Arley Urbano — Curso 312

---

## Estructura del Proyecto

```
ferreteria/
├── schema.sql                          ← Base de datos MySQL
├── README.md
└── src/
    └── ferreteria/
        ├── Main.java                   ← Punto de entrada
        ├── conexion/
        │   └── ConexionDB.java         ← Singleton de conexión MySQL
        ├── modelo/
        │   ├── Persona.java            ← SUPERCLASE abstracta
        │   ├── Usuario.java            ← Subclase (empleados del sistema)
        │   ├── Administrador.java      ← Subclase (rol ADMIN)
        │   ├── Vendedor.java           ← Subclase (rol VENDEDOR)
        │   ├── Cliente.java            ← Subclase (compradores)
        │   ├── Producto.java           ← Inventario
        │   ├── Venta.java              ← Transacción de venta
        │   └── DetalleVenta.java       ← Ítem de una venta
        ├── dao/
        │   ├── UsuarioDAO.java         ← CRUD usuarios
        │   ├── ProductoDAO.java        ← CRUD productos
        │   ├── ClienteDAO.java         ← CRUD clientes
        │   └── VentaDAO.java           ← Registro de ventas
        └── vista/
            ├── LoginFrame.java         ← Pantalla de login
            ├── DashboardFrame.java     ← Ventana principal + métricas
            ├── ProductoPanel.java      ← Gestión de inventario
            ├── ClientePanel.java       ← Gestión de clientes
            ├── VentaPanel.java         ← Registro de ventas
            └── UsuarioPanel.java       ← Gestión de usuarios (solo Admin)
```

## Jerarquía de Clases (OOP)

```
Persona  (abstract)
├── Usuario
│   ├── Administrador    ← Acceso total
│   └── Vendedor         ← Acceso medio
└── Cliente              ← Solo datos de comprador
```

---

## Requisitos

- **JDK 11+** — https://adoptium.net
- **MySQL 8.x** corriendo en `localhost:3306`
- **mysql-connector-j** — https://dev.mysql.com/downloads/connector/j/
  - Descarga el `.jar` y ponlo en la raíz del proyecto

---

## Pasos para ejecutar

### 1. Configurar la base de datos
```sql
-- Abre MySQL Workbench o terminal MySQL y ejecuta:
source /ruta/del/proyecto/schema.sql
```

### 2. Ajustar credenciales (si es necesario)
Edita `src/ferreteria/conexion/ConexionDB.java`:
```java
private static final String USER     = "root";   // Tu usuario MySQL
private static final String PASSWORD = "";        // Tu contraseña MySQL
```

### 3. Compilar
**Windows:**
```
javac -cp ".;mysql-connector-j-8.x.x.jar" -d out -sourcepath src src/ferreteria/Main.java
```
**Linux / Mac:**
```
javac -cp ".:mysql-connector-j-8.x.x.jar" -d out -sourcepath src src/ferreteria/Main.java
```

### 4. Ejecutar
**Windows:**
```
java -cp ".;mysql-connector-j-8.x.x.jar;out" ferreteria.Main
```
**Linux / Mac:**
```
java -cp ".:mysql-connector-j-8.x.x.jar:out" ferreteria.Main
```

---

## Credenciales de prueba

| Rol          | Usuario    | Contraseña    |
|--------------|------------|---------------|
| Administrador | `admin`   | `admin123`    |
| Vendedor      | `vendedor` | `vendedor123` |

---

## Funcionalidades por rol

| Funcionalidad          | Admin | Vendedor |
|------------------------|-------|----------|
| Ver Dashboard          | ✅    | ✅       |
| CRUD Productos         | ✅    | Solo lectura |
| CRUD Clientes          | ✅    | Crear/Ver |
| Registrar Ventas       | ✅    | ✅       |
| Ver Historial Ventas   | ✅    | ✅       |
| CRUD Usuarios          | ✅    | ❌       |
| Ver alertas stock bajo | ✅    | ✅       |
