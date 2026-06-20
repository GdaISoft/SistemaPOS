package com.possemillas.dao.impl;

import com.possemillas.connection.ConnectionFactory;
import com.possemillas.dao.RolDAO;
import com.possemillas.models.Rol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RolDAOImpl implements RolDAO {

    private static final String SQL = "SELECT id_rol, nombre FROM roles WHERE activo = 1 ORDER BY nombre";

    @Override
    public List<Rol> listarActivos() throws SQLException {
        List<Rol> roles = new ArrayList<>();
        try (Connection con = ConnectionFactory.getConnection();
                PreparedStatement ps = con.prepareStatement(SQL);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                roles.add(new Rol(rs.getInt("id_rol"), rs.getString("nombre")));
            }
        }
        return roles;
    }
}
