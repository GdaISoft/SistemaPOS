package com.possemillas.dao.impl;

import com.possemillas.connection.ConnectionFactory;
import com.possemillas.dao.UsuarioDAO;
import com.possemillas.models.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDAOImpl implements UsuarioDAO {

    private static final String SQL_BUSCAR =
            "SELECT u.id_usuario, u.username, u.password_hash, u.email, u.activo, "
            + "       r.id_rol, r.nombre AS nombre_rol, "
            + "       e.nombre AS emp_nombre, e.apellido AS emp_apellido "
            + "FROM usuarios u "
            + "INNER JOIN roles r ON u.id_rol = r.id_rol "
            + "LEFT JOIN empleados e ON e.id_usuario = u.id_usuario "
            + "WHERE u.username = ? AND u.activo = 1";

    private static final String SQL_LISTAR =
            "SELECT u.id_usuario, u.username, u.email, u.activo, u.ultimo_login, "
            + "       r.id_rol, r.nombre AS nombre_rol, "
            + "       e.nombre AS emp_nombre, e.apellido AS emp_apellido "
            + "FROM usuarios u "
            + "INNER JOIN roles r ON u.id_rol = r.id_rol "
            + "LEFT JOIN empleados e ON e.id_usuario = u.id_usuario "
            + "ORDER BY u.username";

    private static final String SQL_UPDATE_LOGIN =
            "UPDATE usuarios SET ultimo_login = NOW() WHERE id_usuario = ?";

    private static final String SQL_INSERT_USUARIO =
            "INSERT INTO usuarios (username, password_hash, email, id_rol, activo) "
            + "VALUES (?, ?, ?, ?, 1)";

    private static final String SQL_INSERT_EMPLEADO =
            "INSERT INTO empleados (id_usuario, nombre, apellido) VALUES (?, ?, ?)";

    private static final String SQL_UPDATE_USUARIO =
            "UPDATE usuarios SET email = ?, id_rol = ? WHERE id_usuario = ?";

    private static final String SQL_UPSERT_EMPLEADO =
            "INSERT INTO empleados (id_usuario, nombre, apellido) VALUES (?, ?, ?) "
            + "ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), apellido = VALUES(apellido)";

    private static final String SQL_CAMBIAR_ESTADO =
            "UPDATE usuarios SET activo = ? WHERE id_usuario = ?";

    @Override
    public Optional<Usuario> buscarPorUsername(String username) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection();
                PreparedStatement ps = con.prepareStatement(SQL_BUSCAR)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
                return Optional.empty();
            }
        }
    }

    @Override
    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        try (Connection con = ConnectionFactory.getConnection();
                PreparedStatement ps = con.prepareStatement(SQL_LISTAR);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    @Override
    public void actualizarUltimoLogin(int idUsuario) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection();
                PreparedStatement ps = con.prepareStatement(SQL_UPDATE_LOGIN)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        }
    }

    @Override
    public void insertar(Usuario usuario) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection()) {
            con.setAutoCommit(false);
            try {
                int idGenerado;
                try (PreparedStatement ps = con.prepareStatement(SQL_INSERT_USUARIO,
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, usuario.getUsername());
                    ps.setString(2, usuario.getPasswordHash());
                    ps.setString(3, usuario.getEmail());
                    ps.setInt(4, usuario.getIdRol());
                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        idGenerado = keys.getInt(1);
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(SQL_INSERT_EMPLEADO)) {
                    ps.setInt(1, idGenerado);
                    ps.setString(2, usuario.getNombre());
                    ps.setString(3, usuario.getApellido());
                    ps.executeUpdate();
                }

                con.commit();
                usuario.setIdUsuario(idGenerado);
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    @Override
    public void actualizar(Usuario usuario) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(SQL_UPDATE_USUARIO)) {
                    ps.setString(1, usuario.getEmail());
                    ps.setInt(2, usuario.getIdRol());
                    ps.setInt(3, usuario.getIdUsuario());
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(SQL_UPSERT_EMPLEADO)) {
                    ps.setInt(1, usuario.getIdUsuario());
                    ps.setString(2, usuario.getNombre());
                    ps.setString(3, usuario.getApellido());
                    ps.executeUpdate();
                }

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    @Override
    public void cambiarEstado(int idUsuario, boolean activo) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection();
                PreparedStatement ps = con.prepareStatement(SQL_CAMBIAR_ESTADO)) {
            ps.setBoolean(1, activo);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setActivo(rs.getBoolean("activo"));
        u.setIdRol(rs.getInt("id_rol"));
        u.setNombreRol(rs.getString("nombre_rol"));
        u.setNombre(rs.getString("emp_nombre"));
        u.setApellido(rs.getString("emp_apellido"));
        // password_hash solo viene en SQL_BUSCAR; en listarTodos no se selecciona por seguridad
        try {
            u.setPasswordHash(rs.getString("password_hash"));
        } catch (SQLException ignored) {
            // columna no presente en SQL_LISTAR, es intencional
        }
        return u;
    }
}
