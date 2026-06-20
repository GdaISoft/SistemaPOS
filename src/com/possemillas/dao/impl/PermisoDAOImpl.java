package com.possemillas.dao.impl;

import com.possemillas.connection.ConnectionFactory;
import com.possemillas.dao.PermisoDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class PermisoDAOImpl implements PermisoDAO {

    private static final String SQL =
            "SELECT p.accion "
            + "FROM rol_permiso rp "
            + "INNER JOIN permisos p ON rp.id_permiso = p.id_permiso "
            + "WHERE rp.id_rol = ? AND p.modulo = ?";

    @Override
    public Set<String> obtenerAccionesPermitidas(int idRol, String modulo) throws SQLException {
        Set<String> acciones = new HashSet<>();
        try (Connection con = ConnectionFactory.getConnection();
                PreparedStatement ps = con.prepareStatement(SQL)) {
            ps.setInt(1, idRol);
            ps.setString(2, modulo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    acciones.add(rs.getString("accion"));
                }
            }
        }
        return acciones;
    }
}
