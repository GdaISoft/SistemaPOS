package com.possemillas.services;

import com.possemillas.dao.RolDAO;
import com.possemillas.dao.UsuarioDAO;
import com.possemillas.dao.impl.RolDAOImpl;
import com.possemillas.dao.impl.UsuarioDAOImpl;
import com.possemillas.models.Rol;
import com.possemillas.models.Usuario;
import com.possemillas.security.PasswordUtil;

import java.sql.SQLException;
import java.util.List;

public class UsuarioService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
    private final RolDAO rolDAO = new RolDAOImpl();

    public List<Usuario> listar() throws SQLException {
        return usuarioDAO.listarTodos();
    }

    public List<Rol> listarRoles() throws SQLException {
        return rolDAO.listarActivos();
    }

    public void crear(Usuario usuario, String passwordPlano) throws SQLException {
        usuario.setPasswordHash(PasswordUtil.hashPassword(passwordPlano));
        usuarioDAO.insertar(usuario);
    }

    public void editar(Usuario usuario) throws SQLException {
        usuarioDAO.actualizar(usuario);
    }

    public void cambiarEstado(int idUsuario, boolean activo) throws SQLException {
        usuarioDAO.cambiarEstado(idUsuario, activo);
    }
}
