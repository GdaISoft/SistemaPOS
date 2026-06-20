package com.possemillas.services;

import com.possemillas.dao.UsuarioDAO;
import com.possemillas.dao.impl.SesionDAOImpl;
import com.possemillas.dao.impl.UsuarioDAOImpl;
import com.possemillas.models.Usuario;
import com.possemillas.security.AutenticacionException;
import com.possemillas.security.PasswordUtil;
import com.possemillas.security.SesionActual;

import java.sql.SQLException;
import java.util.Optional;

public class AuthService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
    private final SesionDAOImpl sesionDAO = new SesionDAOImpl();

    public Usuario autenticar(String username, String passwordPlano, String equipo, String ip)
            throws SQLException, AutenticacionException {

        Optional<Usuario> opt = usuarioDAO.buscarPorUsername(username);

        if (opt.isEmpty()) {
            sesionDAO.registrarAuditoria(null, "SEGURIDAD", "LOGIN_FALLIDO",
                    "Usuario no encontrado: " + username, equipo);
            throw new AutenticacionException("Usuario o contraseña incorrectos");
        }

        Usuario usuario = opt.get();

        if (!PasswordUtil.verificar(passwordPlano, usuario.getPasswordHash())) {
            sesionDAO.registrarAuditoria(usuario.getIdUsuario(), "SEGURIDAD", "LOGIN_FALLIDO",
                    "Contraseña incorrecta", equipo);
            throw new AutenticacionException("Usuario o contraseña incorrectos");
        }

        sesionDAO.invalidarSesionesActivas(usuario.getIdUsuario());
        String token = sesionDAO.crearSesion(usuario.getIdUsuario(), equipo, ip);
        usuarioDAO.actualizarUltimoLogin(usuario.getIdUsuario());

        sesionDAO.registrarAuditoria(usuario.getIdUsuario(), "SEGURIDAD", "LOGIN_EXITOSO",
                "Sesión iniciada, token=" + token, equipo);

        SesionActual.iniciar(usuario, token);
        return usuario;
    }
}
