package com.possemillas.views;

import com.possemillas.controllers.LoginController;
import com.possemillas.models.Usuario;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.GroupLayout;
import javax.swing.SwingConstants;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Ventana de inicio de sesión.
 * NOTA: este JFrame se escribió a mano para que el proyecto compile
 * y corra de inmediato. Puedes abrirlo en el editor de NetBeans y
 * recrearlo con el diseñador visual (arrastrar y soltar) sin perder
 * la lógica, ya que los nombres de componentes coinciden con los
 * definidos en la Fase 2 (txtUsuario, txtPassword, btnIngresar, btnSalir).
 */
public class frmLogin extends JFrame {

    private JPanel pnlPrincipal;
    private JLabel lblTituloApp;
    private JLabel lblUsuario;
    private JTextField txtUsuario;
    private JLabel lblPassword;
    private JPasswordField txtPassword;
    private JButton btnIngresar;
    private JButton btnSalir;

    public frmLogin() {
        initComponents();
        setTitle("POS_SEMILLAS - Iniciar sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        pnlPrincipal = new JPanel();
        lblTituloApp = new JLabel("POS SEMILLAS", SwingConstants.CENTER);
        lblUsuario = new JLabel("Usuario");
        txtUsuario = new JTextField(20);
        lblPassword = new JLabel("Contraseña");
        txtPassword = new JPasswordField(20);
        btnIngresar = new JButton("Ingresar");
        btnSalir = new JButton("Salir");

        btnIngresar.addActionListener(this::btnIngresarActionPerformed);
        btnSalir.addActionListener((ActionEvent evt) -> System.exit(0));

        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnIngresar.doClick();
                }
            }
        });

        GroupLayout layout = new GroupLayout(pnlPrincipal);
        pnlPrincipal.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(lblTituloApp, GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                .addComponent(lblUsuario)
                .addComponent(txtUsuario, GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                .addComponent(lblPassword)
                .addComponent(txtPassword, GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(btnIngresar, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                        .addComponent(btnSalir, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)));

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(lblTituloApp)
                .addGap(15)
                .addComponent(lblUsuario)
                .addComponent(txtUsuario)
                .addGap(10)
                .addComponent(lblPassword)
                .addComponent(txtPassword)
                .addGap(20)
                .addGroup(layout.createParallelGroup()
                        .addComponent(btnIngresar)
                        .addComponent(btnSalir)));

        getContentPane().add(pnlPrincipal);
    }

    private void btnIngresarActionPerformed(ActionEvent evt) {
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Usuario y contraseña son obligatorios");
            return;
        }

        LoginController controller = new LoginController();
        Usuario logueado = controller.intentarLogin(usuario, password);

        if (logueado != null) {
            JOptionPane.showMessageDialog(this,
                    "Bienvenido " + logueado.getUsername() + " (" + logueado.getNombreRol() + ")");
            this.dispose();
            new frmMenuPrincipal().setVisible(true);
        }
    }
}
