package com.possemillas.dao;

import java.sql.SQLException;
import java.util.Set;

public interface PermisoDAO {

    /**
     * Devuelve el conjunto de acciones (READ, CREATE, UPDATE, DELETE)
     * que el rol indicado tiene autorizadas en el módulo indicado.
     */
    Set<String> obtenerAccionesPermitidas(int idRol, String modulo) throws SQLException;
}
