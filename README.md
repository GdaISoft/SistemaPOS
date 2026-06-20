# POS_SEMILLAS

Sistema Punto de Venta — proyecto generado paso a paso (Fase 1 y Fase 2).

## Cómo abrirlo en NetBeans

1. NetBeans → **File → Open Project** → selecciona la carpeta `POS_SEMILLAS`.
2. NetBeans regenerará automáticamente `nbproject/build-impl.xml` y `nbproject/genfiles.properties` al abrir el proyecto — es normal y correcto, no los toques manualmente.
3. Click derecho en el proyecto → **Properties → Libraries** → confirma que aparezcan:
   - `lib/mysql-connector-j-8.4.0.jar`
   - `lib/jbcrypt-0.4.jar`

   Estos dos JARs **no vienen incluidos** en este ZIP (son binarios de terceros). Descárgalos y colócalos dentro de la carpeta `lib/`:
   - MySQL Connector/J: https://dev.mysql.com/downloads/connector/j/
   - jBCrypt: https://search.maven.org/artifact/org.mindrot/jbcrypt (0.4)

   Si NetBeans no los detecta automáticamente, click derecho en **Libraries → Add JAR/Folder** y apunta a cada archivo dentro de `lib/`.

## Base de datos

1. Si aún no tienes la base, importa primero tu dump original: `sql/00_dump_original_db_semillas.sql`.
2. Luego aplica las actualizaciones de Fase 1/2: `sql/01_actualizacion_fase1_fase2.sql`.
3. **Importante:** actualiza el password del usuario `admin` (el dump trae un hash placeholder que no es válido). Genera un hash real con `PasswordUtil.hashPassword("tu_password")` y actualízalo con:
   ```sql
   UPDATE usuarios SET password_hash = 'HASH_GENERADO' WHERE username = 'admin';
   ```

## Configurar la conexión

Edita `src/com/possemillas/connection/ConnectionFactory.java` y coloca tu password real de MySQL (línea `PASSWORD`).

## Log de errores

A partir de esta versión, los errores técnicos (fallas de conexión a BD, excepciones inesperadas, etc.) ya **no se muestran en detalle al usuario** — se guardan en un archivo de log para que lo revisemos juntos:

```
<carpeta donde corres la app>/logs/pos_semillas.log
```

Si NetBeans te muestra un error en pantalla, comparte ese archivo (o pega su contenido) en vez de la captura del mensaje genérico — así puedo ver el stack trace completo y darte el fix exacto.

El archivo rota automáticamente al llegar a ~5 MB (guarda hasta 3 archivos: `pos_semillas.log`, `.log.1`, `.log.2`).

## Estado actual del proyecto

- **Fase 1**: Arquitectura de paquetes + base de datos (`db_semillas` + ajustes de auditoría/costeo/sesiones). ✔
- **Fase 2**: Login y seguridad (BCrypt, sesión única, auditoría, `frmLogin`). ✔
- **Utilidad transversal**: `AppLogger` (log de errores a archivo). ✔
- **Fase 3**: Usuarios, Roles y Permisos — `frmUsuarios` (listar/crear/editar/activar-desactivar), permisos dirigidos 100% por datos (`PermisoService`, tabla `permisos`/`rol_permiso`, sin roles hardcodeados en Java). ✔
- **Fase 4 en adelante**: pendiente — catálogos, inventario, compras, ventas, caja, configuración, reportes.

## Scripts SQL — orden de aplicación

1. `sql/00_dump_original_db_semillas.sql` (solo si aún no tienes la base creada)
2. `sql/01_actualizacion_fase1_fase2.sql`
3. `sql/02_fase3_permisos_y_empleados.sql` ⚠️ **importante**: sin este script, `rol_permiso` está vacía y NINGÚN rol (ni ADMIN) tiene acceso a `frmUsuarios`.

## Estructura de paquetes

```
com.possemillas
 ├── config          (pendiente, fases futuras)
 ├── connection       (ConnectionFactory)
 ├── models           (Usuario, Rol)
 ├── dao              (UsuarioDAO, RolDAO, PermisoDAO)
 ├── dao.impl         (UsuarioDAOImpl, SesionDAOImpl, RolDAOImpl, PermisoDAOImpl)
 ├── controllers      (LoginController)
 ├── views            (Main, frmLogin, frmMenuPrincipal, frmUsuarios, dlgUsuarioForm)
 ├── services         (AuthService, UsuarioService)
 ├── security         (PasswordUtil, SesionActual, AutenticacionException, PermisoService)
 ├── utils            (AppLogger)
 ├── reports          (pendiente)
 └── tests            (pendiente)
```

## Nota sobre frmLogin

`frmLogin.java` está escrito a mano con `GroupLayout` para que el proyecto compile y corra de inmediato. Si prefieres editarlo con el diseñador visual (drag-and-drop) de NetBeans, ábrelo en modo **Design** — NetBeans generará el archivo `.form` asociado automáticamente la primera vez que lo guardes desde ahí. Los nombres de componentes ya coinciden con los definidos en el documento de la Fase 2.
