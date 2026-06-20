package com.possemillas.security;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilidad de hashing de contraseñas con BCrypt.
 * Requiere la librería jbcrypt-0.4.jar en el classpath del proyecto.
 */
public class PasswordUtil {

    private PasswordUtil() {
    }

    public static String hashPassword(String plano) {
        return BCrypt.hashpw(plano, BCrypt.gensalt(12));
    }

    public static boolean verificar(String plano, String hash) {
        return BCrypt.checkpw(plano, hash);
    }
}
