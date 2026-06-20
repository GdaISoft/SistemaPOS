package com.possemillas.security;

import com.possemillas.models.Usuario;

/**
 * Mantiene en memoria el usuario y token de la sesión activa de la
 * aplicación de escritorio (un único usuario logueado por instancia).
 */
public class SesionActual {

    private static Usuario usuarioActivo;
    private static String token;

    private SesionActual() {
    }

    public static void iniciar(Usuario usuario, String tokenSesion) {
        usuarioActivo = usuario;
        token = tokenSesion;
    }

    public static Usuario getUsuarioActivo() {
        return usuarioActivo;
    }

    public static String getToken() {
        return token;
    }

    public static void cerrar() {
        usuarioActivo = null;
        token = null;
    }
}
