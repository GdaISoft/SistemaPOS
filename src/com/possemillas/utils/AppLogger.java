package com.possemillas.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Logger centralizado de la aplicación POS_SEMILLAS.
 *
 * Escribe en un archivo de texto plano dentro de la carpeta "logs/"
 * (al lado del .jar / del proyecto). Si ocurre un error, comparte
 * ese archivo para que se pueda revisar la causa exacta sin depender
 * de capturas de pantalla del mensaje genérico que ve el usuario.
 *
 * Uso:
 *   AppLogger.info("Mensaje informativo");
 *   AppLogger.error("Descripción del contexto", excepcion);
 */
public class AppLogger {

    private static final Logger LOGGER = Logger.getLogger("POS_SEMILLAS");
    private static final String CARPETA_LOGS = "logs";
    private static final String ARCHIVO_LOG = CARPETA_LOGS + "/pos_semillas.log";
    private static boolean inicializado = false;

    private AppLogger() {
    }

    private static synchronized void inicializar() {
        if (inicializado) {
            return;
        }
        try {
            Path carpeta = Paths.get(CARPETA_LOGS);
            if (!Files.exists(carpeta)) {
                Files.createDirectories(carpeta);
            }

            FileHandler fileHandler = new FileHandler(ARCHIVO_LOG, 5_000_000, 3, true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);

            LOGGER.setUseParentHandlers(false);
            LOGGER.addHandler(fileHandler);
            LOGGER.setLevel(Level.ALL);

            inicializado = true;
        } catch (IOException e) {
            // Si ni siquiera se puede crear el log, lo mandamos a consola como último recurso.
            System.err.println("No se pudo inicializar AppLogger: " + e.getMessage());
        }
    }

    public static void info(String mensaje) {
        inicializar();
        LOGGER.info(mensaje);
    }

    public static void warning(String mensaje) {
        inicializar();
        LOGGER.warning(mensaje);
    }

    /**
     * Registra un error con su stack trace completo.
     *
     * @param contexto breve descripción de dónde/qué se intentaba hacer
     * @param t        la excepción capturada
     */
    public static void error(String contexto, Throwable t) {
        inicializar();
        LOGGER.log(Level.SEVERE, contexto, t);
    }

    public static String getRutaArchivoLog() {
        return Paths.get(ARCHIVO_LOG).toAbsolutePath().toString();
    }
}
