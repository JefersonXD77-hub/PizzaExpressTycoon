/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import dao.RolDAO;
import dao.SucursalDAO;
import dao.UsuarioDAO;
import dto.UsuarioAdminDTO;
import dto.UsuarioSesionDTO;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.Rol;
import models.Sucursal;
import models.Usuario;

/**
 *
 * @author aguil
 */
public class GestionUsuariosFrame extends javax.swing.JFrame {

    private final UsuarioSesionDTO sesion;
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final RolDAO rolDAO = new RolDAO();
    private final SucursalDAO sucursalDAO = new SucursalDAO();

    private DefaultTableModel modeloTabla;
    private List<Rol> roles;
    private List<Sucursal> sucursales;

    private Integer idUsuarioSeleccionado = null;

    /**
     * Creates new form GestionarUsuariosFrame
     */
    public GestionUsuariosFrame(UsuarioSesionDTO sesion) {
        this.sesion = sesion;
        initComponents();
        setLocationRelativeTo(null);
        inicializarVista();
    }

    private GestionUsuariosFrame() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void inicializarVista() {
        configurarTabla();
        cargarDatosAdmin();
        cargarRoles();
        cargarSucursales();
        limpiarFormulario();
        cargarUsuarios();
    }

    private void configurarTabla() {
        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Nickname", "Rol", "Sucursal", "Activo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblUsuarios.setModel(modeloTabla);
    }

    private void cargarDatosAdmin() {
        lblTitulo.setText("Super Admin - Gestión de Usuarios");
        lblAdmin.setText("Usuario: " + sesion.getNickname());
        lblEstado.setText("Estado: listo");
    }

    private void cargarRoles() {
        cmbRol.removeAllItems();
        roles = rolDAO.listarRolesGestionables();

        for (Rol rol : roles) {
            cmbRol.addItem(rol.getNombreRol());
        }
    }

    private void cargarSucursales() {
        cmbSucursal.removeAllItems();
        cmbSucursal.addItem("SIN_SUCURSAL");

        sucursales = sucursalDAO.listarTodas();
        for (Sucursal sucursal : sucursales) {
            cmbSucursal.addItem(sucursal.getNombreSucursal());
        }
    }

    private void cargarUsuarios() {
        modeloTabla.setRowCount(0);

        List<UsuarioAdminDTO> usuarios = usuarioDAO.listarTodosConRolYSucursal();

        for (UsuarioAdminDTO dto : usuarios) {
            modeloTabla.addRow(new Object[]{
                dto.getIdUsuario(),
                dto.getNickname(),
                dto.getNombreRol(),
                dto.getNombreSucursal() != null ? dto.getNombreSucursal() : "SIN_SUCURSAL",
                dto.isUsuarioActivo() ? "Sí" : "No"
            });
        }
    }

    private void limpiarFormulario() {
        idUsuarioSeleccionado = null;
        txtNickname.setText("");
        txtPassword.setText("");
        chkUsuarioActivo.setSelected(true);

        if (cmbRol.getItemCount() > 0) {
            cmbRol.setSelectedIndex(0);
        }

        if (cmbSucursal.getItemCount() > 0) {
            cmbSucursal.setSelectedIndex(0);
        }
        
        txtNickname.requestFocus();
    }

    private Usuario construirUsuarioDesdeFormulario() {
        String nickname = txtNickname.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (nickname.isEmpty()) {
            throw new RuntimeException("Ingresa el nickname.");
        }

        if (password.isEmpty()) {
            throw new RuntimeException("Ingresa la contraseña.");
        }

        if (usuarioDAO.existeNickname(nickname)) {
            throw new RuntimeException("Ese nickname ya existe.");
        }

        int indiceRol = cmbRol.getSelectedIndex();
        if (indiceRol < 0 || roles == null || roles.isEmpty()) {
            throw new RuntimeException("Selecciona un rol.");
        }

        Rol rol = roles.get(indiceRol);

        Integer idSucursal = null;
        int indiceSucursal = cmbSucursal.getSelectedIndex();

        if (indiceSucursal > 0) {
            Sucursal sucursal = sucursales.get(indiceSucursal - 1);
            idSucursal = sucursal.getIdSucursal();
        }

        Usuario usuario = new Usuario();
        usuario.setNickname(nickname);
        usuario.setPassword(password);
        usuario.setIdRol(rol.getIdRol());
        usuario.setIdSucursal(idSucursal);
        usuario.setUsuarioActivo(chkUsuarioActivo.isSelected());

        return usuario;
    }

    private Usuario construirUsuarioActualizadoDesdeFormulario() {
        if (idUsuarioSeleccionado == null) {
            throw new RuntimeException("Selecciona un usuario.");
        }

        int indiceRol = cmbRol.getSelectedIndex();
        if (indiceRol < 0 || roles == null || roles.isEmpty()) {
            throw new RuntimeException("Selecciona un rol.");
        }

        Rol rol = roles.get(indiceRol);

        Integer idSucursal = null;
        int indiceSucursal = cmbSucursal.getSelectedIndex();

        if (indiceSucursal > 0) {
            Sucursal sucursal = sucursales.get(indiceSucursal - 1);
            idSucursal = sucursal.getIdSucursal();
        }

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuarioSeleccionado);
        usuario.setIdRol(rol.getIdRol());
        usuario.setIdSucursal(idSucursal);
        usuario.setUsuarioActivo(chkUsuarioActivo.isSelected());

        return usuario;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitulo = new javax.swing.JLabel();
        lblAdmin = new javax.swing.JLabel();
        lblEstado = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblUsuarios = new javax.swing.JTable();
        txtNickname = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();
        cmbRol = new javax.swing.JComboBox<>();
        cmbSucursal = new javax.swing.JComboBox<>();
        chkUsuarioActivo = new javax.swing.JCheckBox();
        btnNuevoUsuario = new javax.swing.JButton();
        btnGuardarUsuario = new javax.swing.JButton();
        btnCambiarEstadoUsuario = new javax.swing.JButton();
        btnRefrescarUsuarios = new javax.swing.JButton();
        btnActualizarUsuario = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblTitulo.setText("Titulo");

        lblAdmin.setText("Admin");

        lblEstado.setText("Estado");

        jScrollPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jScrollPane1MouseClicked(evt);
            }
        });

        tblUsuarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblUsuarios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblUsuariosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblUsuarios);

        txtNickname.setText("Nickname");

        cmbRol.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cmbSucursal.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        chkUsuarioActivo.setText("Usuario Activo");

        btnNuevoUsuario.setText("Nuevo");
        btnNuevoUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoUsuarioActionPerformed(evt);
            }
        });

        btnGuardarUsuario.setText("Guardar");
        btnGuardarUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarUsuarioActionPerformed(evt);
            }
        });

        btnCambiarEstadoUsuario.setText("Activar/Desactivar");
        btnCambiarEstadoUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCambiarEstadoUsuarioActionPerformed(evt);
            }
        });

        btnRefrescarUsuarios.setText("Refrescar");
        btnRefrescarUsuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefrescarUsuariosActionPerformed(evt);
            }
        });

        btnActualizarUsuario.setText("Actualizar Usuario");
        btnActualizarUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarUsuarioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblAdmin)
                    .addComponent(lblEstado)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 66, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txtNickname)
                        .addComponent(txtPassword)
                        .addComponent(chkUsuarioActivo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cmbSucursal, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cmbRol, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCambiarEstadoUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnGuardarUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnRefrescarUsuarios, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnActualizarUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(btnNuevoUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(125, 125, 125))
            .addGroup(layout.createSequentialGroup()
                .addGap(266, 266, 266)
                .addComponent(lblTitulo)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(lblTitulo)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAdmin)
                    .addComponent(txtNickname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEstado)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cmbRol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbSucursal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkUsuarioActivo)
                        .addGap(56, 56, 56)
                        .addComponent(btnNuevoUsuario)
                        .addGap(18, 18, 18)
                        .addComponent(btnGuardarUsuario)
                        .addGap(18, 18, 18)
                        .addComponent(btnActualizarUsuario)
                        .addGap(18, 18, 18)
                        .addComponent(btnCambiarEstadoUsuario)
                        .addGap(18, 18, 18)
                        .addComponent(btnRefrescarUsuarios)))
                .addContainerGap(41, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jScrollPane1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jScrollPane1MouseClicked

    private void btnNuevoUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoUsuarioActionPerformed
        limpiarFormulario();
        lblEstado.setText("Estado: formulario listo para nuevo usuario");

    }//GEN-LAST:event_btnNuevoUsuarioActionPerformed

    private void btnGuardarUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarUsuarioActionPerformed

        try {
            Usuario usuario = construirUsuarioDesdeFormulario();
            int idGenerado = usuarioDAO.crearUsuario(usuario);

            cargarUsuarios();
            limpiarFormulario();
            lblEstado.setText("Estado: usuario creado con ID " + idGenerado);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

    }//GEN-LAST:event_btnGuardarUsuarioActionPerformed

    private void btnCambiarEstadoUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCambiarEstadoUsuarioActionPerformed

        try {
            if (idUsuarioSeleccionado == null) {
                throw new RuntimeException("Selecciona un usuario.");
            }

            boolean ok = usuarioDAO.cambiarEstadoUsuario(idUsuarioSeleccionado, chkUsuarioActivo.isSelected());

            if (!ok) {
                throw new RuntimeException("No se pudo cambiar el estado del usuario.");
            }

            cargarUsuarios();
            lblEstado.setText("Estado: usuario actualizado");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

    }//GEN-LAST:event_btnCambiarEstadoUsuarioActionPerformed

    private void btnRefrescarUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefrescarUsuariosActionPerformed

        cargarUsuarios();
        lblEstado.setText("Estado: usuarios recargados");

    }//GEN-LAST:event_btnRefrescarUsuariosActionPerformed

    private void tblUsuariosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblUsuariosMouseClicked
        int fila = tblUsuarios.getSelectedRow();
        if (fila == -1) {
            return;
        }

        idUsuarioSeleccionado = Integer.valueOf(tblUsuarios.getValueAt(fila, 0).toString());

        txtNickname.setText(tblUsuarios.getValueAt(fila, 1).toString());
        txtPassword.setText("");

        String rol = tblUsuarios.getValueAt(fila, 2).toString();
        cmbRol.setSelectedItem(rol);

        String sucursal = tblUsuarios.getValueAt(fila, 3).toString();
        if (sucursal == null || sucursal.trim().isEmpty()) {
            cmbSucursal.setSelectedItem("SIN_SUCURSAL");
        } else {
            cmbSucursal.setSelectedItem(sucursal);
        }

        chkUsuarioActivo.setSelected("Sí".equals(tblUsuarios.getValueAt(fila, 4).toString()));

    }//GEN-LAST:event_tblUsuariosMouseClicked

    private void btnActualizarUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarUsuarioActionPerformed

        try {
            Usuario usuario = construirUsuarioActualizadoDesdeFormulario();
            boolean ok = usuarioDAO.actualizarUsuarioAdmin(usuario);

            if (!ok) {
                throw new RuntimeException("No se pudo actualizar el usuario.");
            }

            cargarUsuarios();
            lblEstado.setText("Estado: usuario actualizado correctamente");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

    }//GEN-LAST:event_btnActualizarUsuarioActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GestionUsuariosFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GestionUsuariosFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GestionUsuariosFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GestionUsuariosFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GestionUsuariosFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizarUsuario;
    private javax.swing.JButton btnCambiarEstadoUsuario;
    private javax.swing.JButton btnGuardarUsuario;
    private javax.swing.JButton btnNuevoUsuario;
    private javax.swing.JButton btnRefrescarUsuarios;
    private javax.swing.JCheckBox chkUsuarioActivo;
    private javax.swing.JComboBox<String> cmbRol;
    private javax.swing.JComboBox<String> cmbSucursal;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblAdmin;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JTable tblUsuarios;
    private javax.swing.JTextField txtNickname;
    private javax.swing.JPasswordField txtPassword;
    // End of variables declaration//GEN-END:variables
}
