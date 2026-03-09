/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import dto.UsuarioSesionDTO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import models.Pedido;
import service.JuegoService;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author aguil
 */
public class JuegoFrame extends javax.swing.JFrame {

    private UsuarioSesionDTO sesion;
    private int idPartida;

    private final JuegoService juegoService = new JuegoService();

    private DefaultTableModel modeloTabla;
    private Timer timerJuego;

    private int puntajeActual = 0;
    private int nivelActual = 1;
    private int nivelMaximo = 1;

    private int segundosRestantesPartida;
    private int segundosParaSiguienteGeneracion;

    private boolean partidaTerminada = false;
    private Integer idPedidoSeleccionado = null;

    private static final int SEGUNDOS_CIERRE_RECEPCION = 10;

    private List<Pedido> pedidosActuales;

    /**
     * Creates new form JuegoFrame
     */
    public JuegoFrame() {
        initComponents();
        setLocationRelativeTo(null);
        getContentPane().setBackground(new java.awt.Color(220, 230, 240));
    }

    public JuegoFrame(UsuarioSesionDTO sesion, int idPartida) {
        this.sesion = sesion;
        this.idPartida = idPartida;

        initComponents();
        setLocationRelativeTo(null);

        inicializarJuego();
    }

    private void inicializarJuego() {
        segundosRestantesPartida = juegoService.getDuracionPartidaSegundos();
        segundosParaSiguienteGeneracion = 1;

        configurarTabla();
        configurarVistaInicial();
        generarPrimerPedido();
        iniciarTimerJuego();
    }

    private void configurarTabla() {
        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Estado", "Detalle", "Tiempo restante"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblPedidos.setModel(modeloTabla);

        tblPedidos.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    return c;
                }

                c.setBackground(Color.WHITE);

                if (pedidosActuales != null && row >= 0 && row < pedidosActuales.size()) {
                    Pedido pedido = pedidosActuales.get(row);
                    long restantes = juegoService.calcularSegundosRestantes(pedido);
                    int tiempoLimite = pedido.getTiempoLimite();

                    if (tiempoLimite > 0) {
                        double porcentaje = (double) restantes / (double) tiempoLimite;

                        if (porcentaje <= 0.20) {
                            c.setBackground(new Color(255, 153, 153)); // rojo suave
                        } else if (porcentaje <= 0.50) {
                            c.setBackground(new Color(255, 255, 153)); // amarillo suave
                        } else {
                            c.setBackground(new Color(153, 255, 153)); // verde suave
                        }
                    }
                }

                return c;
            }
        });
    }

    private void configurarVistaInicial() {
        lblTitulo.setText("Pizza Express Tycoon - Juego");
        lblJugador.setText("Jugador: " + sesion.getNickname());
        lblPartida.setText("Partida ID: " + idPartida);
        lblNivel.setText("Nivel: " + nivelActual);
        lblPuntaje.setText("Puntaje: " + puntajeActual);
        lblTiempoGlobal.setText("Tiempo restante: " + segundosRestantesPartida + " s");
        lblEstado.setText("Estado: partida iniciada");
        txtDetallePedido.setText("");
        txtDetallePedido.setEditable(false);

        recargarTablaPedidos();
    }

    private void generarPrimerPedido() {
        try {
            JuegoService.ResultadoPedido resultado = juegoService.generarPedidoAleatorio(
                    idPartida,
                    sesion.getIdSucursal(),
                    nivelActual,
                    sesion.getIdUsuario()
            );

            lblEstado.setText("Estado: " + resultado.getMensaje());
            recargarTablaPedidos();

        } catch (Exception e) {
            e.printStackTrace();
            lblEstado.setText("Estado: error al generar primer pedido");
        }
    }

    private void iniciarTimerJuego() {
        timerJuego = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                procesarTickJuego();
            }
        });

        timerJuego.start();
    }

    private void procesarTickJuego() {
        if (partidaTerminada) {
            return;
        }

        segundosRestantesPartida--;

        JuegoService.ResultadoExpiracion expiracion = juegoService.expirarPedidosVencidos(idPartida, sesion.getIdUsuario());
        if (expiracion.getCantidadExpirados() > 0) {
            puntajeActual += expiracion.getDeltaPuntaje();
            lblEstado.setText("Estado: " + expiracion.getCantidadExpirados() + " pedido(s) expiraron");
        }

        if (segundosRestantesPartida <= 0) {
            finalizarPartidaInternamente("Se agotó el tiempo de la partida.");
            return;
        }

        boolean recepcionAbierta = segundosRestantesPartida > SEGUNDOS_CIERRE_RECEPCION;

        if (recepcionAbierta) {
            segundosParaSiguienteGeneracion--;

            if (segundosParaSiguienteGeneracion <= 0) {
                try {
                    JuegoService.ResultadoPedido resultado = juegoService.generarPedidoAleatorio(
                            idPartida,
                            sesion.getIdSucursal(),
                            nivelActual,
                            sesion.getIdUsuario()
                    );

                    lblEstado.setText("Estado: " + resultado.getMensaje());

                } catch (Exception e) {
                    e.printStackTrace();
                    lblEstado.setText("Estado: error al generar pedido");
                }

                segundosParaSiguienteGeneracion = juegoService.getIntervaloGeneracionSegundos();
            }
        } else {
            lblEstado.setText("Estado: recepción cerrada");
        }

        actualizarNivel();
        actualizarEncabezado();
        recargarTablaPedidos();
        refrescarDetalleSeleccionado();
    }

    private void actualizarEncabezado() {
        lblNivel.setText("Nivel: " + nivelActual);
        lblPuntaje.setText("Puntaje: " + puntajeActual);
        lblTiempoGlobal.setText("Tiempo restante: " + segundosRestantesPartida + " s");
    }

    private void actualizarNivel() {
        int pedidosEntregados = juegoService.contarPedidosEntregados(idPartida);

        JuegoService.NivelResultado nivelResultado = juegoService.recalcularNivel(
                puntajeActual,
                pedidosEntregados,
                nivelMaximo
        );

        nivelActual = nivelResultado.getNivelActual();
        nivelMaximo = nivelResultado.getNivelMaximo();
    }

    private void actualizarPedidoSeleccionadoDesdeTabla() {
        int fila = tblPedidos.getSelectedRow();

        if (fila == -1) {
            idPedidoSeleccionado = null;
            return;
        }

        Object valor = tblPedidos.getValueAt(fila, 0);
        if (valor == null) {
            idPedidoSeleccionado = null;
            return;
        }

        try {
            idPedidoSeleccionado = Integer.valueOf(valor.toString());
        } catch (NumberFormatException e) {
            idPedidoSeleccionado = null;
        }
    }

    private void restaurarSeleccionPedido() {
        if (idPedidoSeleccionado == null) {
            return;
        }

        for (int i = 0; i < tblPedidos.getRowCount(); i++) {
            Object valor = tblPedidos.getValueAt(i, 0);

            if (valor != null) {
                try {
                    Integer idFila = Integer.valueOf(valor.toString());

                    if (idPedidoSeleccionado.equals(idFila)) {
                        tblPedidos.setRowSelectionInterval(i, i);
                        return;
                    }
                } catch (NumberFormatException e) {

                }
            }
        }

        idPedidoSeleccionado = null;
    }

    private void recargarTablaPedidos() {
        Integer seleccionAnterior = idPedidoSeleccionado;

        modeloTabla.setRowCount(0);

        pedidosActuales = juegoService.listarPedidosActivos(idPartida);

        for (Pedido pedido : pedidosActuales) {
            Object[] fila = new Object[4];
            fila[0] = pedido.getIdPedido();
            fila[1] = pedido.getEstadoActual().name();
            fila[2] = juegoService.construirResumenCortoPedido(pedido.getIdPedido());
            fila[3] = juegoService.calcularSegundosRestantes(pedido) + " s";

            modeloTabla.addRow(fila);
        }

        idPedidoSeleccionado = seleccionAnterior;
        restaurarSeleccionPedido();
    }

    private Integer obtenerIdPedidoSeleccionado() {
        if (idPedidoSeleccionado != null) {
            return idPedidoSeleccionado;
        }

        int fila = tblPedidos.getSelectedRow();
        if (fila == -1) {
            return null;
        }

        Object valor = tblPedidos.getValueAt(fila, 0);
        if (valor == null) {
            return null;
        }

        try {
            return Integer.valueOf(valor.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void mostrarDetallePedidoSeleccionado() {
        Integer idPedido = obtenerIdPedidoSeleccionado();

        if (idPedido == null) {
            txtDetallePedido.setText("");
            return;
        }

        String detalle = juegoService.construirDescripcionPedido(idPedido);
        txtDetallePedido.setText(detalle);
    }

    private void refrescarDetalleSeleccionado() {
        if (obtenerIdPedidoSeleccionado() != null) {
            mostrarDetallePedidoSeleccionado();
        } else {
            txtDetallePedido.setText("");
        }
    }

    private void finalizarPartidaInternamente(String motivo) {
        if (partidaTerminada) {
            return;
        }

        partidaTerminada = true;

        if (timerJuego != null) {
            timerJuego.stop();
        }

        try {
            JuegoService.ResultadoFinalPartida resultado = juegoService.finalizarPartidaYGuardar(
                    idPartida,
                    puntajeActual,
                    nivelMaximo,
                    sesion.getIdUsuario()
            );

            puntajeActual = resultado.getPuntajeFinal();
            actualizarEncabezado();

            JuegoService.ResumenPartidaDTO resumen = juegoService.construirResumenPartida(
                    idPartida,
                    resultado.getPuntajeFinal(),
                    resultado.getNivelMaximo()
            );

            String mensaje = motivo
                    + "\n\nJugador: " + sesion.getNickname()
                    + "\nPartida ID: " + idPartida
                    + "\nPuntaje final: " + resumen.getPuntajeFinal()
                    + "\nNivel máximo alcanzado: " + resumen.getNivelMaximo()
                    + "\nTotal de pedidos: " + resumen.getTotalPedidos()
                    + "\nEntregados: " + resumen.getPedidosEntregados()
                    + "\nCancelados: " + resumen.getPedidosCancelados()
                    + "\nNo entregados: " + resumen.getPedidosNoEntregados()
                    + "\nPendientes cerrados al finalizar: " + resultado.getPedidosPendientesCerrados();

            JOptionPane.showMessageDialog(this, mensaje);

            new JugadorFrame(sesion).setVisible(true);
            this.dispose();

        } catch (Exception e) {
            partidaTerminada = false;

            if (timerJuego != null) {
                timerJuego.start();
            }

            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al finalizar la partida: " + e.getMessage());
        }
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
        lblJugador = new javax.swing.JLabel();
        lblPartida = new javax.swing.JLabel();
        lblNivel = new javax.swing.JLabel();
        lblPuntaje = new javax.swing.JLabel();
        lblTiempoGlobal = new javax.swing.JLabel();
        lblEstado = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPedidos = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDetallePedido = new javax.swing.JTextArea();
        btnAvanzarEstado = new javax.swing.JButton();
        btnCancelarPedido = new javax.swing.JButton();
        btnRefrescar = new javax.swing.JButton();
        btnFinalizarPartida = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lblTitulo.setText("Pizza Express Tycoon - Juego");

        lblJugador.setText("Jugador");

        lblPartida.setText("Partida");

        lblNivel.setText("Nivel");

        lblPuntaje.setText("Puntaje");

        lblTiempoGlobal.setText("Tiempo Global");

        lblEstado.setText("Estado");

        jScrollPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jScrollPane1MouseClicked(evt);
            }
        });

        tblPedidos.setModel(new javax.swing.table.DefaultTableModel(
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
        tblPedidos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPedidosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblPedidos);

        txtDetallePedido.setColumns(20);
        txtDetallePedido.setRows(5);
        jScrollPane2.setViewportView(txtDetallePedido);

        btnAvanzarEstado.setText("Avanzar Estado");
        btnAvanzarEstado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAvanzarEstadoActionPerformed(evt);
            }
        });

        btnCancelarPedido.setText("Cancelar Pedido");
        btnCancelarPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarPedidoActionPerformed(evt);
            }
        });

        btnRefrescar.setText("Refrescar");
        btnRefrescar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefrescarActionPerformed(evt);
            }
        });

        btnFinalizarPartida.setText("Finalizar Partida");
        btnFinalizarPartida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinalizarPartidaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblEstado)
                    .addComponent(lblTiempoGlobal)
                    .addComponent(lblJugador)
                    .addComponent(lblPuntaje)
                    .addComponent(lblNivel)
                    .addComponent(lblPartida))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 77, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnAvanzarEstado)
                        .addGap(34, 34, 34)
                        .addComponent(btnCancelarPedido)
                        .addGap(36, 36, 36)
                        .addComponent(btnRefrescar)
                        .addGap(31, 31, 31)
                        .addComponent(btnFinalizarPartida))
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane2))
                .addContainerGap(195, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(365, 365, 365)
                .addComponent(lblTitulo)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(67, 67, 67)
                        .addComponent(lblJugador)
                        .addGap(54, 54, 54)
                        .addComponent(lblPartida)
                        .addGap(51, 51, 51)
                        .addComponent(lblNivel)
                        .addGap(49, 49, 49)
                        .addComponent(lblPuntaje))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(lblTitulo)
                        .addGap(28, 28, 28)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTiempoGlobal)
                        .addGap(41, 41, 41)
                        .addComponent(lblEstado))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelarPedido)
                    .addComponent(btnRefrescar)
                    .addComponent(btnFinalizarPartida)
                    .addComponent(btnAvanzarEstado))
                .addGap(36, 36, 36))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRefrescarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefrescarActionPerformed
        try {
            JuegoService.ResultadoExpiracion expiracion = juegoService.expirarPedidosVencidos(idPartida, sesion.getIdUsuario());
            puntajeActual += expiracion.getDeltaPuntaje();

            actualizarNivel();
            actualizarEncabezado();
            recargarTablaPedidos();
            refrescarDetalleSeleccionado();

            lblEstado.setText("Estado: vista actualizada");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al refrescar: " + e.getMessage());
        }    }//GEN-LAST:event_btnRefrescarActionPerformed

    private void tblPedidosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPedidosMouseClicked

        actualizarPedidoSeleccionadoDesdeTabla();
        mostrarDetallePedidoSeleccionado();    }//GEN-LAST:event_tblPedidosMouseClicked

    private void btnAvanzarEstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAvanzarEstadoActionPerformed
        Integer idPedido = obtenerIdPedidoSeleccionado();

        if (idPedido == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un pedido.");
            return;
        }

        try {
            JuegoService.ResultadoPedido resultado = juegoService.avanzarEstadoPedido(idPedido, sesion.getIdUsuario());

            if (!resultado.isExito()) {
                JOptionPane.showMessageDialog(this, resultado.getMensaje());
                return;
            }

            puntajeActual += resultado.getDeltaPuntaje();
            lblEstado.setText("Estado: " + resultado.getMensaje());

            actualizarNivel();
            actualizarEncabezado();
            recargarTablaPedidos();

            if (resultado.getPedido() != null && resultado.getPedido().getEstadoActual().esFinal()) {
                idPedidoSeleccionado = null;
                txtDetallePedido.setText("");
            } else {
                refrescarDetalleSeleccionado();
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al avanzar estado: " + e.getMessage());
        }
    }//GEN-LAST:event_btnAvanzarEstadoActionPerformed

    private void btnCancelarPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarPedidoActionPerformed
        Integer idPedido = obtenerIdPedidoSeleccionado();

        if (idPedido == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un pedido.");
            return;
        }

        try {
            JuegoService.ResultadoPedido resultado = juegoService.cancelarPedido(idPedido, sesion.getIdUsuario());

            if (!resultado.isExito()) {
                JOptionPane.showMessageDialog(this, resultado.getMensaje());
                return;
            }

            puntajeActual += resultado.getDeltaPuntaje();
            lblEstado.setText("Estado: " + resultado.getMensaje());

            actualizarNivel();
            actualizarEncabezado();
            recargarTablaPedidos();

            idPedidoSeleccionado = null;
            txtDetallePedido.setText("");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cancelar pedido: " + e.getMessage());
        }    }//GEN-LAST:event_btnCancelarPedidoActionPerformed

    private void btnFinalizarPartidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinalizarPartidaActionPerformed
        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas finalizar la partida?",
                "Confirmación",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            finalizarPartidaInternamente("La partida fue finalizada manualmente.");
        }    }//GEN-LAST:event_btnFinalizarPartidaActionPerformed

    private void jScrollPane1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jScrollPane1MouseClicked

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
            java.util.logging.Logger.getLogger(JuegoFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JuegoFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JuegoFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JuegoFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JuegoFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAvanzarEstado;
    private javax.swing.JButton btnCancelarPedido;
    private javax.swing.JButton btnFinalizarPartida;
    private javax.swing.JButton btnRefrescar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblJugador;
    private javax.swing.JLabel lblNivel;
    private javax.swing.JLabel lblPartida;
    private javax.swing.JLabel lblPuntaje;
    private javax.swing.JLabel lblTiempoGlobal;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JTable tblPedidos;
    private javax.swing.JTextArea txtDetallePedido;
    // End of variables declaration//GEN-END:variables
}
