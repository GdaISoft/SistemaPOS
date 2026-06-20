package com.possemillas.controllers;

import com.possemillas.models.Usuario;
import com.possemillas.security.AutenticacionException;
import com.possemillas.services.AuthService;
import com.possemillas.utils.AppLogger;

import javax.swing.JOptionPane;
import java.net.InetAddress;
import java.sql.SQLException;

public class LoginController {

    private final AuthService authService = new AuthService();

    public Usuario intentarLogin(String username, String password) {
        try {
            String equipo = InetAddress.getLocalHost().getHostName();
            String ip = InetAddress.getLocalHost().getHostAddress();

            return authService.autenticar(username, password, equipo, ip);

        } catch (AutenticacionException e) {
            // Error esperado de negocio (credenciales incorrectas): se muestra tal cual,
            // no requiere quedar en el log de errores técnicos.
            JOptionPane.showMessageDialog(null, e.getMessage(),
                    "Error de autenticación", JOptionPane.ERROR_MESSAGE);

        } catch (SQLException e) {
            AppLogger.error("Fallo de conexión/consulta a base de datos durante login "
                    + "(usuario intentado: " + username + ")", e);
            JOptionPane.showMessageDialog(null,
                    "No se pudo conectar a la base de datos.\n"
                    + "Se generó un registro en el archivo de logs:\n"
                    + AppLogger.getRutaArchivoLog(),
                    "Error de conexión", JOptionPane.ERROR_MESSAGE);

        } catch (Exception e) {
            AppLogger.error("Error inesperado durante login "
                    + "(usuario intentado: " + username + ")", e);
            JOptionPane.showMessageDialog(null,
                    "Ocurrió un error inesperado.\n"
                    + "Se generó un registro en el archivo de logs:\n"
                    + AppLogger.getRutaArchivoLog(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
}
