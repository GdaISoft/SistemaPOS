package com.possemillas.views;

import com.possemillas.utils.AppLogger;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        AppLogger.info("Aplicación iniciada");

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) ->
                AppLogger.error("Excepción no controlada en el hilo: " + thread.getName(), throwable));

        SwingUtilities.invokeLater(() -> new frmLogin().setVisible(true));
    }
}
