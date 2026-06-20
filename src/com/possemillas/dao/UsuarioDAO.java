package com.possemillas.dao;

import com.possemillas.models.Usuario;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UsuarioDAO {

    Optional<Usuario> buscarPorUsername(String username) throws SQLException;

    void actualizarUltimoLogin(int idUsuario) throws SQLException;

    List<Usuario> listarTodos() throws SQLException;

    void insertar(Usuario usuario) throws SQLException;

    void actualizar(Usuario usuario) throws SQLException;

    void cambiarEstado(int idUsuario, boolean activo) throws SQLException;
}
