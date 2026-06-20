package com.possemillas.security;

import com.possemillas.dao.PermisoDAO;
import com.possemillas.dao.impl.PermisoDAOImpl;
import com.possemillas.utils.AppLogger;

import java.util.Collections;
import java.util.Set;

/**
 * Punto único para validar permisos por módulo y acción. Cualquier
 * pantalla futura (Productos, Ventas, Caja, etc.) reutiliza esta misma
 * clase, solo cambia el nombre del módulo/acción consultados.
 *
 * Las acciones estándar usadas en la BD son: READ, CREATE, UPDATE, DELETE.
 */
public class PermisoService {

    private final PermisoDAO permisoDAO = new PermisoDAOImpl();

    public boolean puede(String modulo, String accion) {
        Set<String> acciones = obtenerAcciones(modulo);
        return acciones.contains(accion);
    }

    public boolean puedeConsultar(String modulo) {
        return puede(modulo, "READ");
    }

    public boolean puedeCrear(String modulo) {
        return puede(modulo, "CREATE");
    }

    public boolean puedeEditar(String modulo) {
        return puede(modulo, "UPDATE");
    }

    public boolean puedeEliminar(String modulo) {
        return puede(modulo, "DELETE");
    }

    private Set<String> obtenerAcciones(String modulo) {
        try {
            int idRol = SesionActual.getUsuarioActivo().getIdRol();
            return permisoDAO.obtenerAccionesPermitidas(idRol, modulo);
        } catch (Exception e) {
            AppLogger.error("Error consultando permisos del módulo " + modulo, e);
            return Collections.emptySet(); // ante el error, se niega el acceso por seguridad
        }
    }
}
