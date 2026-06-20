package com.possemillas.views;

import com.possemillas.models.Rol;
import com.possemillas.models.Usuario;
import com.possemillas.services.UsuarioService;
import com.possemillas.utils.AppLogger;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Frame;
import java.sql.SQLException;
import java.util.List;

/**
 * Diálogo modal para alta/edición de usuarios.
 * Si recibe un Usuario existente, entra en modo edición (oculta password
 * y username, ya que no se permiten cambiar desde aquí).
 */
public class dlgUsuarioForm extends JDialog {

    private JTextField txtUsername;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JComboBox<Rol> cmbRol;
    private JButton btnGuardar;
    private JButton btnCancelar;

    private final UsuarioService usuarioService = new UsuarioService();
    private final Usuario usuarioEditando; // null = modo alta
    private boolean guardadoExitoso = false;

    public dlgUsuarioForm(Frame parent, Usuario usuarioExistente) {
        super(parent, true);
        this.usuarioEditando = usuarioExistente;
        initComponents();
        cargarRoles();
        if (usuarioEditando != null) {
            precargarDatos();
        }
        setTitle(usuarioEditando == null ? "Nuevo usuario" : "Editar usuario");
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel panel = new JPanel();

        JLabel lblUsername = new JLabel("Usuario");
        txtUsername = new JTextField(20);

        JLabel lblNombre = new JLabel("Nombre");
        txtNombre = new JTextField(20);

        JLabel lblApellido = new JLabel("Apellido");
        txtApellido = new JTextField(20);

        JLabel lblEmail = new JLabel("Email");
        txtEmail = new JTextField(20);

        JLabel lblPassword = new JLabel("Contraseña");
        txtPassword = new JPasswordField(20);

        JLabel lblRol = new JLabel("Rol");
        cmbRol = new JComboBox<>();

        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());

        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createParallelGroup()
                .addComponent(lblUsername).addComponent(txtUsername)
                .addComponent(lblNombre).addComponent(txtNombre)
                .addComponent(lblApellido).addComponent(txtApellido)
                .addComponent(lblEmail).addComponent(txtEmail)
                .addComponent(lblPassword).addComponent(txtPassword)
                .addComponent(lblRol).addComponent(cmbRol)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(btnGuardar)
                        .addComponent(btnCancelar)));

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(lblUsername).addComponent(txtUsername)
                .addComponent(lblNombre).addComponent(txtNombre)
                .addComponent(lblApellido).addComponent(txtApellido)
                .addComponent(lblEmail).addComponent(txtEmail)
                .addComponent(lblPassword).addComponent(txtPassword)
                .addComponent(lblRol).addComponent(cmbRol)
                .addGap(15)
                .addGroup(layout.createParallelGroup()
                        .addComponent(btnGuardar)
                        .addComponent(btnCancelar)));

        getContentPane().add(panel);
    }

    private void cargarRoles() {
        try {
            List<Rol> roles = usuarioService.listarRoles();
            for (Rol r : roles) {
                cmbRol.addItem(r);
            }
        } catch (SQLException e) {
            AppLogger.error("Error cargando roles en dlgUsuarioForm", e);
            JOptionPane.showMessageDialog(this,
                    "No se pudieron cargar los roles. Revisa el log:\n" + AppLogger.getRutaArchivoLog(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void precargarDatos() {
        txtUsername.setText(usuarioEditando.getUsername());
        txtUsername.setEditable(false); // el username no se cambia en edición
        txtNombre.setText(usuarioEditando.getNombre());
        txtApellido.setText(usuarioEditando.getApellido());
        txtEmail.setText(usuarioEditando.getEmail());

        txtPassword.setEnabled(false); // password no se cambia desde este formulario

        for (int i = 0; i < cmbRol.getItemCount(); i++) {
            if (cmbRol.getItemAt(i).getIdRol() == usuarioEditando.getIdRol()) {
                cmbRol.setSelectedIndex(i);
                break;
            }
        }
    }

    private void guardar() {
        String username = txtUsername.getText().trim();
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String email = txtEmail.getText().trim();
        Rol rolSeleccionado = (Rol) cmbRol.getSelectedItem();

        if (username.isEmpty() || nombre.isEmpty() || apellido.isEmpty()
                || email.isEmpty() || rolSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios");
            return;
        }

        try {
            if (usuarioEditando == null) {
                String password = new String(txtPassword.getPassword());
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "La contraseña es obligatoria");
                    return;
                }
                Usuario nuevo = new Usuario();
                nuevo.setUsername(username);
                nuevo.setNombre(nombre);
                nuevo.setApellido(apellido);
                nuevo.setEmail(email);
                nuevo.setIdRol(rolSeleccionado.getIdRol());
                usuarioService.crear(nuevo, password);
            } else {
                usuarioEditando.setNombre(nombre);
                usuarioEditando.setApellido(apellido);
                usuarioEditando.setEmail(email);
                usuarioEditando.setIdRol(rolSeleccionado.getIdRol());
                usuarioService.editar(usuarioEditando);
            }
            guardadoExitoso = true;
            dispose();

        } catch (SQLException e) {
            AppLogger.error("Error guardando usuario (username=" + username + ")", e);
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar el usuario.\nRevisa el log:\n" + AppLogger.getRutaArchivoLog(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }
}
