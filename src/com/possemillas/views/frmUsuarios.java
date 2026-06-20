package com.possemillas.views;

import com.possemillas.models.Usuario;
import com.possemillas.security.PermisoService;
import com.possemillas.security.SesionActual;
import com.possemillas.services.UsuarioService;
import com.possemillas.utils.AppLogger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.util.List;

public class frmUsuarios extends JFrame {

    private static final String MODULO = "USUARIOS";

    private JTextField txtBuscar;
    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnCambiarEstado;
    private JTable tblUsuarios;
    private DefaultTableModel modeloTabla;
    private JLabel lblSesionActual;

    private final UsuarioService usuarioService = new UsuarioService();
    private final PermisoService permisoService = new PermisoService();
    private List<Usuario> usuariosCargados;

    public frmUsuarios() {
        // Verificación de acceso ANTES de construir la UI: si no tiene
        // permiso de consulta sobre el módulo, no se le permite ni ver la pantalla.
        if (!permisoService.puedeConsultar(MODULO)) {
            JOptionPane.showMessageDialog(null,
                    "No tienes permiso para acceder al módulo de Usuarios.",
                    "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }

        initComponents();
        aplicarPermisosUI();
        cargarUsuarios();

        setTitle("POS_SEMILLAS - Gestión de Usuarios");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtBuscar = new JTextField(20);
        txtBuscar.setToolTipText("Buscar usuario...");
        btnNuevo = new JButton("+ Nuevo");
        btnEditar = new JButton("Editar");
        btnCambiarEstado = new JButton("Activar/Desactivar");

        panelSuperior.add(new JLabel("Buscar:"));
        panelSuperior.add(txtBuscar);
        panelSuperior.add(btnNuevo);
        panelSuperior.add(btnEditar);
        panelSuperior.add(btnCambiarEstado);

        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Usuario", "Nombre", "Rol", "Estado", "Último acceso"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblUsuarios = new JTable(modeloTabla);
        tblUsuarios.getColumnModel().getColumn(0).setMinWidth(0);
        tblUsuarios.getColumnModel().getColumn(0).setMaxWidth(0); // columna ID oculta

        lblSesionActual = new JLabel();
        lblSesionActual.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        if (SesionActual.getUsuarioActivo() != null) {
            lblSesionActual.setText("Sesión: " + SesionActual.getUsuarioActivo().getUsername()
                    + " (" + SesionActual.getUsuarioActivo().getNombreRol() + ")");
        }

        btnNuevo.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> editarSeleccionado());
        btnCambiarEstado.addActionListener(e -> cambiarEstadoSeleccionado());
        txtBuscar.addActionListener(e -> filtrar());

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panelSuperior, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(tblUsuarios), BorderLayout.CENTER);
        getContentPane().add(lblSesionActual, BorderLayout.SOUTH);
    }

    /**
     * Oculta/deshabilita botones según los permisos reales del rol —
     * no solo se valida al guardar, también se evita mostrar opciones
     * que el usuario no podría ejecutar.
     */
    private void aplicarPermisosUI() {
        btnNuevo.setVisible(permisoService.puedeCrear(MODULO));
        btnEditar.setVisible(permisoService.puedeEditar(MODULO));
        btnCambiarEstado.setVisible(permisoService.puedeEditar(MODULO));
    }

    private void cargarUsuarios() {
        try {
            usuariosCargados = usuarioService.listar();
            poblarTabla(usuariosCargados);
        } catch (SQLException e) {
            AppLogger.error("Error cargando lista de usuarios", e);
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar la lista de usuarios.\nRevisa el log:\n"
                    + AppLogger.getRutaArchivoLog(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void poblarTabla(List<Usuario> usuarios) {
        modeloTabla.setRowCount(0);
        for (Usuario u : usuarios) {
            modeloTabla.addRow(new Object[]{
                u.getIdUsuario(),
                u.getUsername(),
                u.getNombreCompleto(),
                u.getNombreRol(),
                u.isActivo() ? "Activo" : "Inactivo",
                "—" // último login formateado se agrega en una mejora futura
            });
        }
    }

    private void filtrar() {
        String texto = txtBuscar.getText().trim().toLowerCase();
        if (texto.isEmpty()) {
            poblarTabla(usuariosCargados);
            return;
        }
        List<Usuario> filtrados = usuariosCargados.stream()
                .filter(u -> u.getUsername().toLowerCase().contains(texto)
                        || u.getNombreCompleto().toLowerCase().contains(texto))
                .toList();
        poblarTabla(filtrados);
    }

    private Usuario obtenerSeleccionado() {
        int fila = tblUsuarios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un usuario de la tabla");
            return null;
        }
        int idUsuario = (int) modeloTabla.getValueAt(fila, 0);
        return usuariosCargados.stream()
                .filter(u -> u.getIdUsuario() == idUsuario)
                .findFirst()
                .orElse(null);
    }

    private void abrirFormulario(Usuario usuario) {
        if (usuario == null && !permisoService.puedeCrear(MODULO)) {
            JOptionPane.showMessageDialog(this, "No tienes permiso para crear usuarios");
            return;
        }
        if (usuario != null && !permisoService.puedeEditar(MODULO)) {
            JOptionPane.showMessageDialog(this, "No tienes permiso para editar usuarios");
            return;
        }

        dlgUsuarioForm dialogo = new dlgUsuarioForm(this, usuario);
        dialogo.setVisible(true);

        if (dialogo.isGuardadoExitoso()) {
            cargarUsuarios();
        }
    }

    private void editarSeleccionado() {
        Usuario seleccionado = obtenerSeleccionado();
        if (seleccionado != null) {
            abrirFormulario(seleccionado);
        }
    }

    private void cambiarEstadoSeleccionado() {
        if (!permisoService.puedeEditar(MODULO)) {
            JOptionPane.showMessageDialog(this, "No tienes permiso para cambiar el estado de usuarios");
            return;
        }
        Usuario seleccionado = obtenerSeleccionado();
        if (seleccionado == null) {
            return;
        }

        boolean nuevoEstado = !seleccionado.isActivo();
        String accion = nuevoEstado ? "activar" : "desactivar";

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Seguro que deseas " + accion + " a " + seleccionado.getUsername() + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            usuarioService.cambiarEstado(seleccionado.getIdUsuario(), nuevoEstado);
            cargarUsuarios();
        } catch (SQLException e) {
            AppLogger.error("Error cambiando estado del usuario id=" + seleccionado.getIdUsuario(), e);
            JOptionPane.showMessageDialog(this,
                    "No se pudo cambiar el estado.\nRevisa el log:\n" + AppLogger.getRutaArchivoLog(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
