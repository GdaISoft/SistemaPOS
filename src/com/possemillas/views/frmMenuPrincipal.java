package com.possemillas.views;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.FlowLayout;

/**
 * Menú principal. Se irá ampliando en cada fase con un botón/menú
 * por módulo (Catálogos, Inventario, Compras, Ventas, Caja, etc.).
 */
public class frmMenuPrincipal extends JFrame {

    public frmMenuPrincipal() {
        setTitle("POS_SEMILLAS - Menú principal");
        setSize(420, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());

        add(new JLabel("Sesión iniciada correctamente."));

        JButton btnUsuarios = new JButton("Usuarios");
        btnUsuarios.addActionListener(e -> new frmUsuarios().setVisible(true));
        add(btnUsuarios);
    }
}
