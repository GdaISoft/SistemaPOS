package com.possemillas.dao.impl;

import com.possemillas.connection.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

/**
 * Maneja el ciclo de vida de las sesiones (tabla sesiones) y el registro
 * de auditoría (tabla auditoria) relacionados a seguridad.
 */
public class SesionDAOImpl {

    public void invalidarSesionesActivas(int idUsuario) throws SQLException {
        String sql = "UPDATE sesiones SET activa = 0, fecha_cierre = NOW() "
                + "WHERE id_usuario = ? AND activa = 1";
        try (Connection con = ConnectionFactory.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        }
    }

    public String crearSesion(int idUsuario, String equipo, String ip) throws SQLException {
        String token = UUID.randomUUID().toString();
        String sql = "INSERT INTO sesiones (id_usuario, token_sesion, equipo, ip) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setString(2, token);
            ps.setString(3, equipo);
            ps.setString(4, ip);
            ps.executeUpdate();
        }
        return token;
    }

    public void registrarAuditoria(Integer idUsuario, String modulo, String accion,
            String descripcion, String equipo) throws SQLException {
        String sql = "INSERT INTO auditoria (id_usuario, modulo, accion, descripcion, equipo) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            if (idUsuario != null) {
                ps.setInt(1, idUsuario);
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, modulo);
            ps.setString(3, accion);
            ps.setString(4, descripcion);
            ps.setString(5, equipo);
            ps.executeUpdate();
        }
    }
}
