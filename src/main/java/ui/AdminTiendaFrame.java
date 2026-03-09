/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import dto.UsuarioSesionDTO;

import dao.ProductoDAO;
import dto.RankingJugadorDTO;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.Producto;
import service.JuegoService;
import dao.ReporteDAO;
import dto.EstadisticaSucursalDTO;
import java.io.File;
import javax.swing.JFileChooser;

/**
 *
 * @author aguil
 */
public class AdminTiendaFrame extends javax.swing.JFrame {

    private final UsuarioSesionDTO sesion;
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final JuegoService juegoService = new JuegoService();
    private DefaultTableModel modeloTabla;
    private Integer idProductoSeleccionado = null;
    private final ReporteDAO reporteDAO = new ReporteDAO();
    private EstadisticaSucursalDTO estadisticasActuales;

    public AdminTiendaFrame(UsuarioSesionDTO sesion) {

        this.sesion = sesion;
        initComponents();
        setLocationRelativeTo(null);
        setTitle("Admin Tienda - " + sesion.getNickname());
        lblTitulo.setText("Bienvenido: " + sesion.getNickname() + " (" + sesion.getNombreRol() + ")");
        inicializarVista();
    }

    private AdminTiendaFrame() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Creates new form AdminTiendaFrame
     */
    private void inicializarVista() {
        configurarTabla();
        cargarDatosAdmin();
        limpiarFormulario();
        cargarProductos();
        
        lblTotalPartidas.setText("Total partidas: -");
    lblMejorPuntaje.setText("Mejor puntaje: -");
    lblPromedioPuntaje.setText("Promedio puntaje: -");
    lblTotalEntregados.setText("Pedidos entregados: -");
    lblTotalCancelados.setText("Pedidos cancelados: -");
    lblTotalNoEntregados.setText("Pedidos no entregados: -");
    }

    private void configurarTabla() {
        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Precio", "Activo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblProductos.setModel(modeloTabla);
    }

    private void cargarDatosAdmin() {
        lblTitulo.setText("Administración de Tienda - Productos");
        lblAdmin.setText("Admin: " + sesion.getNickname());
        lblSucursal.setText("Sucursal ID: " + sesion.getIdSucursal());
        lblEstado.setText("Estado: listo");
    }

    private void cargarProductos() {
        modeloTabla.setRowCount(0);

        List<Producto> productos = productoDAO.listarTodosPorSucursal(sesion.getIdSucursal());

        for (Producto p : productos) {
            modeloTabla.addRow(new Object[]{
                p.getIdProducto(),
                p.getNombreProducto(),
                p.getPrecio(),
                p.isProductoActivo() ? "Sí" : "No"
            });
        }
    }

    private void limpiarFormulario() {
        idProductoSeleccionado = null;
        txtNombreProducto.setText("");
        txtPrecioProducto.setText("");
        chkProductoActivo.setSelected(true);
    }

    private Integer obtenerIdProductoSeleccionado() {
        int fila = tblProductos.getSelectedRow();
        if (fila == -1) {
            return null;
        }

        Object valor = tblProductos.getValueAt(fila, 0);
        if (valor == null) {
            return null;
        }

        return Integer.valueOf(valor.toString());
    }

    private Producto construirProductoDesdeFormulario(boolean incluirId) {
        String nombre = txtNombreProducto.getText().trim();
        String precioTexto = txtPrecioProducto.getText().trim();

        if (nombre.isEmpty()) {
            throw new RuntimeException("Ingresa el nombre del producto.");
        }

        if (precioTexto.isEmpty()) {
            throw new RuntimeException("Ingresa el precio del producto.");
        }

        BigDecimal precio;
        try {
            precio = new BigDecimal(precioTexto);
        } catch (Exception e) {
            throw new RuntimeException("El precio no tiene un formato válido.");
        }

        Producto producto = new Producto();
        producto.setIdSucursal(sesion.getIdSucursal());
        producto.setNombreProducto(nombre);
        producto.setPrecio(precio);
        producto.setProductoActivo(chkProductoActivo.isSelected());

        if (incluirId) {
            if (idProductoSeleccionado == null) {
                throw new RuntimeException("Selecciona un producto.");
            }
            producto.setIdProducto(idProductoSeleccionado);
        }

        return producto;
    }

    private void cargarEstadisticasSucursal() {
        estadisticasActuales = reporteDAO.obtenerEstadisticasSucursal(sesion.getIdSucursal());

        lblTotalPartidas.setText("Total partidas: " + estadisticasActuales.getTotalPartidas());
        lblMejorPuntaje.setText("Mejor puntaje: " + estadisticasActuales.getMejorPuntaje());
        lblPromedioPuntaje.setText("Promedio puntaje: " + String.format("%.2f", estadisticasActuales.getPromedioPuntaje()));
        lblTotalEntregados.setText("Pedidos entregados: " + estadisticasActuales.getTotalEntregados());
        lblTotalCancelados.setText("Pedidos cancelados: " + estadisticasActuales.getTotalCancelados());
        lblTotalNoEntregados.setText("Pedidos no entregados: " + estadisticasActuales.getTotalNoEntregados());

        lblEstado.setText("Estado: estadísticas cargadas");
    }

    private String elegirRutaCSV(String nombreSugerido) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(nombreSugerido));

        int resultado = chooser.showSaveDialog(this);
        if (resultado != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File archivo = chooser.getSelectedFile();
        String ruta = archivo.getAbsolutePath();

        if (!ruta.toLowerCase().endsWith(".csv")) {
            ruta = ruta + ".csv";
        }

        return ruta;
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
        lblSucursal = new javax.swing.JLabel();
        lblEstado = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProductos = new javax.swing.JTable();
        txtNombreProducto = new javax.swing.JTextField();
        txtPrecioProducto = new javax.swing.JTextField();
        chkProductoActivo = new javax.swing.JCheckBox();
        btnNuevoProducto = new javax.swing.JButton();
        btnGuardarProducto = new javax.swing.JButton();
        btnCambiarEstadoProducto = new javax.swing.JButton();
        btnRefrescarProductos = new javax.swing.JButton();
        btnVerRankingSucursal = new javax.swing.JButton();
        btnActualizarProducto = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblTotalPartidas = new javax.swing.JLabel();
        lblMejorPuntaje = new javax.swing.JLabel();
        lblPromedioPuntaje = new javax.swing.JLabel();
        lblTotalEntregados = new javax.swing.JLabel();
        lblTotalCancelados = new javax.swing.JLabel();
        lblTotalNoEntregados = new javax.swing.JLabel();
        btnCargarEstadisticas = new javax.swing.JButton();
        btnExportarCSV = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblTitulo.setText("Bienvenid@ ");

        lblAdmin.setText("Admin");

        lblSucursal.setText("Sucursal");

        lblEstado.setText("Estado");

        tblProductos.setModel(new javax.swing.table.DefaultTableModel(
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
        tblProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProductosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblProductos);

        txtNombreProducto.setText("Nombre Producto");

        txtPrecioProducto.setText("Precio Producto");

        chkProductoActivo.setText("Producto Activo");

        btnNuevoProducto.setText("Nuevo Producto");
        btnNuevoProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoProductoActionPerformed(evt);
            }
        });

        btnGuardarProducto.setText("Guardar Producto");
        btnGuardarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarProductoActionPerformed(evt);
            }
        });

        btnCambiarEstadoProducto.setText("Cambiar Estado Producto");
        btnCambiarEstadoProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCambiarEstadoProductoActionPerformed(evt);
            }
        });

        btnRefrescarProductos.setText("Refrescar Productos");
        btnRefrescarProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefrescarProductosActionPerformed(evt);
            }
        });

        btnVerRankingSucursal.setText("Ver Ranking Sucursal");
        btnVerRankingSucursal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerRankingSucursalActionPerformed(evt);
            }
        });

        btnActualizarProducto.setText("Actualizar Producto");
        btnActualizarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarProductoActionPerformed(evt);
            }
        });

        jLabel1.setText("Nombre del Producto:");

        jLabel2.setText("Precio del Producto:");

        lblTotalPartidas.setText("Total Partidas: ");

        lblMejorPuntaje.setText("Mejor Puntaje: ");

        lblPromedioPuntaje.setText("Promedio Puntaje: ");

        lblTotalEntregados.setText("Total Entregados: ");

        lblTotalCancelados.setText("Total Cancelados: ");

        lblTotalNoEntregados.setText("Total No Entregados: ");

        btnCargarEstadisticas.setText("Cargar Estadisticas");
        btnCargarEstadisticas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCargarEstadisticasActionPerformed(evt);
            }
        });

        btnExportarCSV.setText("Exportar CSV");
        btnExportarCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportarCSVActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnNuevoProducto)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel2))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtNombreProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtPrecioProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(chkProductoActivo)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnGuardarProducto))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(308, 308, 308)
                                .addComponent(lblTitulo)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 499, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblEstado)
                                    .addComponent(lblTotalPartidas)
                                    .addComponent(lblMejorPuntaje)
                                    .addComponent(lblSucursal)
                                    .addComponent(lblAdmin))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblPromedioPuntaje)
                                    .addComponent(lblTotalEntregados))
                                .addGap(85, 85, 85)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblTotalNoEntregados)
                                    .addComponent(lblTotalCancelados))
                                .addGap(158, 158, 158)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnCambiarEstadoProducto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnVerRankingSucursal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnRefrescarProductos, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnActualizarProducto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnExportarCSV, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnCargarEstadisticas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(24, 24, 24))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(lblTitulo)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnActualizarProducto)
                        .addGap(18, 18, 18)
                        .addComponent(btnRefrescarProductos)
                        .addGap(18, 18, 18)
                        .addComponent(btnVerRankingSucursal)
                        .addGap(18, 18, 18)
                        .addComponent(btnCambiarEstadoProducto)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(lblAdmin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSucursal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblEstado)
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNuevoProducto)
                    .addComponent(txtNombreProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPrecioProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkProductoActivo)
                    .addComponent(btnGuardarProducto))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalPartidas)
                    .addComponent(lblPromedioPuntaje)
                    .addComponent(lblTotalCancelados)
                    .addComponent(btnCargarEstadisticas))
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMejorPuntaje)
                    .addComponent(lblTotalEntregados)
                    .addComponent(lblTotalNoEntregados)
                    .addComponent(btnExportarCSV))
                .addGap(46, 46, 46))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblProductosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProductosMouseClicked

        int fila = tblProductos.getSelectedRow();
        if (fila == -1) {
            return;
        }

        idProductoSeleccionado = Integer.valueOf(tblProductos.getValueAt(fila, 0).toString());
        txtNombreProducto.setText(tblProductos.getValueAt(fila, 1).toString());
        txtPrecioProducto.setText(tblProductos.getValueAt(fila, 2).toString());
        chkProductoActivo.setSelected("Sí".equals(tblProductos.getValueAt(fila, 3).toString()));


    }//GEN-LAST:event_tblProductosMouseClicked

    private void btnNuevoProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoProductoActionPerformed

        limpiarFormulario();
        lblEstado.setText("Estado: formulario listo para nuevo producto");
    }//GEN-LAST:event_btnNuevoProductoActionPerformed

    private void btnGuardarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarProductoActionPerformed

        try {
            Producto producto = construirProductoDesdeFormulario(false);
            int idGenerado = productoDAO.insertarProducto(producto);

            cargarProductos();
            limpiarFormulario();
            lblEstado.setText("Estado: producto creado con ID " + idGenerado);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

    }//GEN-LAST:event_btnGuardarProductoActionPerformed

    private void btnCambiarEstadoProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCambiarEstadoProductoActionPerformed

        try {
            if (idProductoSeleccionado == null) {
                throw new RuntimeException("Selecciona un producto.");
            }

            boolean nuevoEstado = chkProductoActivo.isSelected();
            boolean ok = productoDAO.cambiarEstadoProducto(
                    idProductoSeleccionado,
                    sesion.getIdSucursal(),
                    nuevoEstado
            );

            if (!ok) {
                throw new RuntimeException("No se pudo cambiar el estado del producto.");
            }

            cargarProductos();
            lblEstado.setText("Estado: producto actualizado a " + (nuevoEstado ? "activo" : "inactivo"));

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

    }//GEN-LAST:event_btnCambiarEstadoProductoActionPerformed

    private void btnRefrescarProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefrescarProductosActionPerformed

        cargarProductos();
        lblEstado.setText("Estado: productos recargados");

    }//GEN-LAST:event_btnRefrescarProductosActionPerformed

    private void btnVerRankingSucursalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerRankingSucursalActionPerformed
        try {
            List<RankingJugadorDTO> ranking = juegoService.obtenerRankingPorSucursal(sesion.getIdSucursal(), 10);
            RankingFrame frame = new RankingFrame("Ranking de Sucursal", ranking);
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar ranking de sucursal: " + e.getMessage());
        }

    }//GEN-LAST:event_btnVerRankingSucursalActionPerformed

    private void btnActualizarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarProductoActionPerformed
        try {
            Producto producto = construirProductoDesdeFormulario(true);
            boolean ok = productoDAO.actualizarProducto(producto);

            if (!ok) {
                throw new RuntimeException("No se pudo actualizar el producto.");
            }

            cargarProductos();
            lblEstado.setText("Estado: producto actualizado");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_btnActualizarProductoActionPerformed

    private void btnCargarEstadisticasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCargarEstadisticasActionPerformed
        
        try {
            cargarEstadisticasSucursal();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error cargando estadísticas: " + e.getMessage());
        }

    }//GEN-LAST:event_btnCargarEstadisticasActionPerformed

    private void btnExportarCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportarCSVActionPerformed

        JOptionPane.showMessageDialog(this, "Entró al evento Exportar CSV");
        
        try {
        List<RankingJugadorDTO> ranking = juegoService.obtenerRankingPorSucursal(sesion.getIdSucursal(), 100);

        if (estadisticasActuales == null) {
            estadisticasActuales = reporteDAO.obtenerEstadisticasSucursal(sesion.getIdSucursal());
        }

        String rutaRanking = elegirRutaCSV("ranking_sucursal_" + sesion.getIdSucursal() + ".csv");
        if (rutaRanking == null) {
            return;
        }

        String rutaEstadisticas = elegirRutaCSV("estadisticas_sucursal_" + sesion.getIdSucursal() + ".csv");
        if (rutaEstadisticas == null) {
            return;
        }

        reporteDAO.exportarRankingSucursalCSV(rutaRanking, ranking);
        reporteDAO.exportarEstadisticasSucursalCSV(rutaEstadisticas, estadisticasActuales);

        lblEstado.setText("Estado: CSV exportado correctamente");
        JOptionPane.showMessageDialog(this, "Archivos CSV exportados correctamente.");

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error exportando CSV: " + e.getMessage());
    }

    }//GEN-LAST:event_btnExportarCSVActionPerformed

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
            java.util.logging.Logger.getLogger(AdminTiendaFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdminTiendaFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdminTiendaFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdminTiendaFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdminTiendaFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizarProducto;
    private javax.swing.JButton btnCambiarEstadoProducto;
    private javax.swing.JButton btnCargarEstadisticas;
    private javax.swing.JButton btnExportarCSV;
    private javax.swing.JButton btnGuardarProducto;
    private javax.swing.JButton btnNuevoProducto;
    private javax.swing.JButton btnRefrescarProductos;
    private javax.swing.JButton btnVerRankingSucursal;
    private javax.swing.JCheckBox chkProductoActivo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblAdmin;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblMejorPuntaje;
    private javax.swing.JLabel lblPromedioPuntaje;
    private javax.swing.JLabel lblSucursal;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblTotalCancelados;
    private javax.swing.JLabel lblTotalEntregados;
    private javax.swing.JLabel lblTotalNoEntregados;
    private javax.swing.JLabel lblTotalPartidas;
    private javax.swing.JTable tblProductos;
    private javax.swing.JTextField txtNombreProducto;
    private javax.swing.JTextField txtPrecioProducto;
    // End of variables declaration//GEN-END:variables
}
