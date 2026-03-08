package service;

import dao.DetallePedidoDAO;
import dao.HistorialEstadoPedidoDAO;
import dao.ParametroJuegoDAO;
import dao.PartidaDAO;
import dao.PedidoDAO;
import dao.ProductoDAO;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import models.DetallePedido;
import models.Partida;
import models.Pedido;
import models.PedidoEstado;
import models.PedidoMotivoFinalizado;
import models.Producto;
import dao.RankingDAO;
import dto.RankingJugadorDTO;

public class JuegoService {

    private final PartidaDAO partidaDAO;
    private final PedidoDAO pedidoDAO;
    private final DetallePedidoDAO detallePedidoDAO;
    private final HistorialEstadoPedidoDAO historialEstadoPedidoDAO;
    private final ProductoDAO productoDAO;
    private final ParametroJuegoDAO parametroJuegoDAO;
    private final RankingDAO rankingDAO;

    private final Random random;

    private static final int MAX_PEDIDOS_ACTIVOS = 5;
    private static final int INTERVALO_GENERACION_SEGUNDOS = 15;
    private static final int DURACION_PARTIDA_SEGUNDOS = 180;
    private static final int MAX_PRODUCTOS_POR_PEDIDO = 3;

    public JuegoService() {
        this.partidaDAO = new PartidaDAO();
        this.pedidoDAO = new PedidoDAO();
        this.detallePedidoDAO = new DetallePedidoDAO();
        this.historialEstadoPedidoDAO = new HistorialEstadoPedidoDAO();
        this.productoDAO = new ProductoDAO();
        this.parametroJuegoDAO = new ParametroJuegoDAO();
        this.random = new Random();
        this.rankingDAO = new RankingDAO();
    }

    public int getMaxPedidosActivos() {
        return MAX_PEDIDOS_ACTIVOS;
    }

    public int getIntervaloGeneracionSegundos() {
        return INTERVALO_GENERACION_SEGUNDOS;
    }

    public int getDuracionPartidaSegundos() {
        return DURACION_PARTIDA_SEGUNDOS;
    }

    public boolean puedeGenerarPedido(int idPartida) {
        int activos = pedidoDAO.contarPedidosActivosPorPartida(idPartida);
        return activos < MAX_PEDIDOS_ACTIVOS;
    }

    public ResultadoPedido generarPedidoAleatorio(int idPartida, int idSucursal, int nivel, Integer idUsuarioAccion) {
        ResultadoPedido resultado = new ResultadoPedido();

        if (!puedeGenerarPedido(idPartida)) {
            resultado.setExito(false);
            resultado.setMensaje("Ya alcanzaste el máximo de pedidos activos.");
            return resultado;
        }

        List<Producto> productosActivos = productoDAO.listarActivosPorSucursal(idSucursal);
        if (productosActivos == null || productosActivos.isEmpty()) {
            resultado.setExito(false);
            resultado.setMensaje("No hay productos activos en la sucursal del jugador.");
            return resultado;
        }

        int tiempoBase = parametroJuegoDAO.obtenerTiempoBasePorNivel(nivel);
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime expiracion = ahora.plusSeconds(tiempoBase);

        Pedido pedido = new Pedido();
        pedido.setIdPartida(idPartida);
        pedido.setEstadoActual(PedidoEstado.RECIBIDA);
        pedido.setTiempoLimite(tiempoBase);
        pedido.setTiempoDeExpiracion(expiracion);
        pedido.setMotivoFinalizado(null);

        int idPedido = pedidoDAO.crearPedido(pedido);
        pedido.setIdPedido(idPedido);

        List<DetallePedido> detalles = construirDetalleAleatorio(idPedido, productosActivos);
        detallePedidoDAO.insertarDetalles(detalles);

        historialEstadoPedidoDAO.registrarCambioEstado(idPedido, PedidoEstado.RECIBIDA, idUsuarioAccion);

        Pedido pedidoGuardado = pedidoDAO.buscarPorId(idPedido);

        resultado.setExito(true);
        resultado.setMensaje("Pedido generado correctamente.");
        resultado.setPedido(pedidoGuardado);
        resultado.setDetalles(detalles);
        return resultado;
    }

    private List<DetallePedido> construirDetalleAleatorio(int idPedido, List<Producto> productosActivos) {
        List<Producto> copia = new ArrayList<Producto>(productosActivos);
        Collections.shuffle(copia, random);

        int maxProductos = Math.min(MAX_PRODUCTOS_POR_PEDIDO, copia.size());
        int cantidadProductos = 1 + random.nextInt(maxProductos);

        List<DetallePedido> detalles = new ArrayList<DetallePedido>();

        for (int i = 0; i < cantidadProductos; i++) {
            Producto producto = copia.get(i);

            DetallePedido detalle = new DetallePedido();
            detalle.setIdPedido(idPedido);
            detalle.setIdProducto(producto.getIdProducto());
            detalle.setCantidad(1 + random.nextInt(3));

            detalles.add(detalle);
        }

        return detalles;
    }

    public List<Pedido> listarPedidosActivos(int idPartida) {
        return pedidoDAO.listarPedidosActivosPorPartida(idPartida);
    }

    public List<Pedido> listarTodosLosPedidos(int idPartida) {
        return pedidoDAO.listarTodosPorPartida(idPartida);
    }

    public List<DetallePedido> listarDetallePedido(int idPedido) {
        return detallePedidoDAO.listarPorPedido(idPedido);
    }

    public String construirDescripcionPedido(int idPedido) {
        List<DetallePedido> detalles = detallePedidoDAO.listarPorPedido(idPedido);
        if (detalles == null || detalles.isEmpty()) {
            return "Sin detalle.";
        }

        StringBuilder sb = new StringBuilder();
        for (DetallePedido detalle : detalles) {
            Producto producto = productoDAO.buscarPorId(detalle.getIdProducto());
            String nombreProducto = "Producto " + detalle.getIdProducto();

            if (producto != null) {
                nombreProducto = producto.getNombreProducto();
            }

            sb.append("- ")
                    .append(nombreProducto)
                    .append(" x")
                    .append(detalle.getCantidad())
                    .append("\n");
        }

        return sb.toString();
    }

    public String construirResumenCortoPedido(int idPedido) {
        List<DetallePedido> detalles = detallePedidoDAO.listarPorPedido(idPedido);
        if (detalles == null || detalles.isEmpty()) {
            return "Sin productos";
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < detalles.size(); i++) {
            DetallePedido detalle = detalles.get(i);
            Producto producto = productoDAO.buscarPorId(detalle.getIdProducto());
            String nombreProducto = (producto != null) ? producto.getNombreProducto() : ("Producto " + detalle.getIdProducto());

            sb.append(nombreProducto).append(" x").append(detalle.getCantidad());

            if (i < detalles.size() - 1) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }

    public ResultadoPedido avanzarEstadoPedido(int idPedido, Integer idUsuarioAccion) {
        ResultadoPedido resultado = new ResultadoPedido();

        Pedido pedido = pedidoDAO.buscarPorId(idPedido);
        if (pedido == null) {
            resultado.setExito(false);
            resultado.setMensaje("El pedido no existe.");
            return resultado;
        }

        if (pedido.getEstadoActual().esFinal()) {
            resultado.setExito(false);
            resultado.setMensaje("El pedido ya está finalizado.");
            return resultado;
        }

        if (pedido.getTiempoDeExpiracion() != null && LocalDateTime.now().isAfter(pedido.getTiempoDeExpiracion())) {
            return marcarPedidoNoEntregado(idPedido, idUsuarioAccion);
        }

        PedidoEstado actual = pedido.getEstadoActual();

        if (actual == PedidoEstado.RECIBIDA) {
            pedidoDAO.actualizarEstado(idPedido, PedidoEstado.PREPARANDO);
            historialEstadoPedidoDAO.registrarCambioEstado(idPedido, PedidoEstado.PREPARANDO, idUsuarioAccion);

            resultado.setExito(true);
            resultado.setMensaje("Pedido movido a PREPARANDO.");
            resultado.setPedido(pedidoDAO.buscarPorId(idPedido));
            return resultado;
        }

        if (actual == PedidoEstado.PREPARANDO) {
            pedidoDAO.actualizarEstado(idPedido, PedidoEstado.EN_HORNO);
            historialEstadoPedidoDAO.registrarCambioEstado(idPedido, PedidoEstado.EN_HORNO, idUsuarioAccion);

            resultado.setExito(true);
            resultado.setMensaje("Pedido movido a EN_HORNO.");
            resultado.setPedido(pedidoDAO.buscarPorId(idPedido));
            return resultado;
        }

        if (actual == PedidoEstado.EN_HORNO) {
            int puntos = calcularPuntajeEntrega(pedido);

            pedidoDAO.finalizarPedido(idPedido, PedidoEstado.ENTREGADA, PedidoMotivoFinalizado.ENTREGADA);
            historialEstadoPedidoDAO.registrarCambioEstado(idPedido, PedidoEstado.ENTREGADA, idUsuarioAccion);

            resultado.setExito(true);
            resultado.setMensaje("Pedido entregado correctamente.");
            resultado.setDeltaPuntaje(puntos);
            resultado.setPedido(pedidoDAO.buscarPorId(idPedido));
            return resultado;
        }

        resultado.setExito(false);
        resultado.setMensaje("No existe una transición válida para ese estado.");
        return resultado;
    }

    public ResultadoPedido cancelarPedido(int idPedido, Integer idUsuarioAccion) {
        ResultadoPedido resultado = new ResultadoPedido();

        Pedido pedido = pedidoDAO.buscarPorId(idPedido);
        if (pedido == null) {
            resultado.setExito(false);
            resultado.setMensaje("El pedido no existe.");
            return resultado;
        }

        if (pedido.getEstadoActual().esFinal()) {
            resultado.setExito(false);
            resultado.setMensaje("El pedido ya está finalizado.");
            return resultado;
        }

        if (pedido.getEstadoActual() != PedidoEstado.RECIBIDA && pedido.getEstadoActual() != PedidoEstado.PREPARANDO) {
            resultado.setExito(false);
            resultado.setMensaje("Solo se puede cancelar en RECIBIDA o PREPARANDO.");
            return resultado;
        }

        pedidoDAO.finalizarPedido(idPedido, PedidoEstado.CANCELADA, PedidoMotivoFinalizado.CANCELADA);
        historialEstadoPedidoDAO.registrarCambioEstado(idPedido, PedidoEstado.CANCELADA, idUsuarioAccion);

        resultado.setExito(true);
        resultado.setMensaje("Pedido cancelado.");
        resultado.setDeltaPuntaje(-30);
        resultado.setPedido(pedidoDAO.buscarPorId(idPedido));
        return resultado;
    }

    public ResultadoExpiracion expirarPedidosVencidos(int idPartida, Integer idUsuarioAccion) {
        ResultadoExpiracion resultado = new ResultadoExpiracion();

        List<Pedido> activos = pedidoDAO.listarPedidosActivosPorPartida(idPartida);
        LocalDateTime ahora = LocalDateTime.now();

        for (Pedido pedido : activos) {
            if (pedido.getTiempoDeExpiracion() != null && ahora.isAfter(pedido.getTiempoDeExpiracion())) {
                pedidoDAO.finalizarPedido(pedido.getIdPedido(), PedidoEstado.NO_ENTREGADO, PedidoMotivoFinalizado.NO_ENTREGADO);
                historialEstadoPedidoDAO.registrarCambioEstado(pedido.getIdPedido(), PedidoEstado.NO_ENTREGADO, idUsuarioAccion);

                resultado.setCantidadExpirados(resultado.getCantidadExpirados() + 1);
                resultado.setDeltaPuntaje(resultado.getDeltaPuntaje() - 50);
            }
        }

        return resultado;
    }

    private ResultadoPedido marcarPedidoNoEntregado(int idPedido, Integer idUsuarioAccion) {
        ResultadoPedido resultado = new ResultadoPedido();

        Pedido pedido = pedidoDAO.buscarPorId(idPedido);
        if (pedido == null) {
            resultado.setExito(false);
            resultado.setMensaje("El pedido no existe.");
            return resultado;
        }

        if (pedido.getEstadoActual().esFinal()) {
            resultado.setExito(false);
            resultado.setMensaje("El pedido ya está finalizado.");
            return resultado;
        }

        pedidoDAO.finalizarPedido(idPedido, PedidoEstado.NO_ENTREGADO, PedidoMotivoFinalizado.NO_ENTREGADO);
        historialEstadoPedidoDAO.registrarCambioEstado(idPedido, PedidoEstado.NO_ENTREGADO, idUsuarioAccion);

        resultado.setExito(true);
        resultado.setMensaje("El pedido expiró y pasó a NO_ENTREGADO.");
        resultado.setDeltaPuntaje(-50);
        resultado.setPedido(pedidoDAO.buscarPorId(idPedido));
        return resultado;
    }

    private int calcularPuntajeEntrega(Pedido pedido) {
        int puntos = 100;

        if (pedido.getFechaDePedido() != null) {
            long segundosTranscurridos = Duration.between(pedido.getFechaDePedido(), LocalDateTime.now()).getSeconds();

            if (segundosTranscurridos <= (pedido.getTiempoLimite() / 2)) {
                puntos += 50;
            }
        }

        return puntos;
    }

    public NivelResultado recalcularNivel(int puntajeActual, int pedidosEntregados, int nivelMaximoActual) {
        int nivelNuevo = 1;

        if (puntajeActual >= 1000 || pedidosEntregados >= 10) {
            nivelNuevo = 3;
        } else if (puntajeActual >= 500 || pedidosEntregados >= 5) {
            nivelNuevo = 2;
        }

        int nivelMaximoNuevo = nivelMaximoActual;
        if (nivelNuevo > nivelMaximoNuevo) {
            nivelMaximoNuevo = nivelNuevo;
        }

        NivelResultado resultado = new NivelResultado();
        resultado.setNivelActual(nivelNuevo);
        resultado.setNivelMaximo(nivelMaximoNuevo);
        return resultado;
    }

    public int contarPedidosEntregados(int idPartida) {
        List<Pedido> todos = pedidoDAO.listarTodosPorPartida(idPartida);
        int total = 0;

        for (Pedido pedido : todos) {
            if (pedido.getEstadoActual() == PedidoEstado.ENTREGADA) {
                total++;
            }
        }

        return total;
    }

    public ResultadoFinalPartida finalizarPartidaYGuardar(int idPartida, int puntajeActual, int nivelMaximo, Integer idUsuarioAccion) {
        ResultadoFinalPartida resultado = new ResultadoFinalPartida();
        int puntajeFinal = puntajeActual;

        List<Pedido> activos = pedidoDAO.listarPedidosActivosPorPartida(idPartida);
        for (Pedido pedido : activos) {
            pedidoDAO.finalizarPedido(pedido.getIdPedido(), PedidoEstado.NO_ENTREGADO, PedidoMotivoFinalizado.NO_ENTREGADO);
            historialEstadoPedidoDAO.registrarCambioEstado(pedido.getIdPedido(), PedidoEstado.NO_ENTREGADO, idUsuarioAccion);
            puntajeFinal -= 50;
            resultado.setPedidosPendientesCerrados(resultado.getPedidosPendientesCerrados() + 1);
        }

        boolean partidaFinalizada = partidaDAO.finalizarPartida(idPartida, puntajeFinal, nivelMaximo);

        if (!partidaFinalizada) {
            throw new RuntimeException("No se pudo actualizar la partida en la base de datos.");
        }

        resultado.setPuntajeFinal(puntajeFinal);
        resultado.setNivelMaximo(nivelMaximo);
        return resultado;
    }

    public long calcularSegundosRestantes(Pedido pedido) {
        if (pedido == null || pedido.getTiempoDeExpiracion() == null || pedido.getEstadoActual().esFinal()) {
            return 0;
        }

        long segundos = Duration.between(LocalDateTime.now(), pedido.getTiempoDeExpiracion()).getSeconds();
        return Math.max(segundos, 0);
    }

    public static class ResultadoPedido {

        private boolean exito;
        private String mensaje;
        private int deltaPuntaje;
        private Pedido pedido;
        private List<DetallePedido> detalles;

        public boolean isExito() {
            return exito;
        }

        public void setExito(boolean exito) {
            this.exito = exito;
        }

        public String getMensaje() {
            return mensaje;
        }

        public void setMensaje(String mensaje) {
            this.mensaje = mensaje;
        }

        public int getDeltaPuntaje() {
            return deltaPuntaje;
        }

        public void setDeltaPuntaje(int deltaPuntaje) {
            this.deltaPuntaje = deltaPuntaje;
        }

        public Pedido getPedido() {
            return pedido;
        }

        public void setPedido(Pedido pedido) {
            this.pedido = pedido;
        }

        public List<DetallePedido> getDetalles() {
            return detalles;
        }

        public void setDetalles(List<DetallePedido> detalles) {
            this.detalles = detalles;
        }
    }

    public static class ResultadoExpiracion {

        private int cantidadExpirados;
        private int deltaPuntaje;

        public int getCantidadExpirados() {
            return cantidadExpirados;
        }

        public void setCantidadExpirados(int cantidadExpirados) {
            this.cantidadExpirados = cantidadExpirados;
        }

        public int getDeltaPuntaje() {
            return deltaPuntaje;
        }

        public void setDeltaPuntaje(int deltaPuntaje) {
            this.deltaPuntaje = deltaPuntaje;
        }
    }

    public static class NivelResultado {

        private int nivelActual;
        private int nivelMaximo;

        public int getNivelActual() {
            return nivelActual;
        }

        public void setNivelActual(int nivelActual) {
            this.nivelActual = nivelActual;
        }

        public int getNivelMaximo() {
            return nivelMaximo;
        }

        public void setNivelMaximo(int nivelMaximo) {
            this.nivelMaximo = nivelMaximo;
        }
    }

    public static class ResultadoFinalPartida {

        private int puntajeFinal;
        private int nivelMaximo;
        private int pedidosPendientesCerrados;

        public int getPuntajeFinal() {
            return puntajeFinal;
        }

        public void setPuntajeFinal(int puntajeFinal) {
            this.puntajeFinal = puntajeFinal;
        }

        public int getNivelMaximo() {
            return nivelMaximo;
        }

        public void setNivelMaximo(int nivelMaximo) {
            this.nivelMaximo = nivelMaximo;
        }

        public int getPedidosPendientesCerrados() {
            return pedidosPendientesCerrados;
        }

        public void setPedidosPendientesCerrados(int pedidosPendientesCerrados) {
            this.pedidosPendientesCerrados = pedidosPendientesCerrados;
        }
    }

    public List<RankingJugadorDTO> obtenerRankingGeneral(int limite) {
        return rankingDAO.obtenerRankingGeneral(limite);
    }

    public List<RankingJugadorDTO> obtenerRankingPorSucursal(int idSucursal, int limite) {
        return rankingDAO.obtenerRankingPorSucursal(idSucursal, limite);
    }

    public ResumenPartidaDTO construirResumenPartida(int idPartida, int puntajeFinal, int nivelMaximo) {
        List<Pedido> todos = pedidoDAO.listarTodosPorPartida(idPartida);

        int entregados = 0;
        int cancelados = 0;
        int noEntregados = 0;
        int total = todos.size();

        for (Pedido pedido : todos) {
            if (pedido.getEstadoActual() == PedidoEstado.ENTREGADA) {
                entregados++;
            } else if (pedido.getEstadoActual() == PedidoEstado.CANCELADA) {
                cancelados++;
            } else if (pedido.getEstadoActual() == PedidoEstado.NO_ENTREGADO) {
                noEntregados++;
            }
        }

        ResumenPartidaDTO dto = new ResumenPartidaDTO();
        dto.setTotalPedidos(total);
        dto.setPedidosEntregados(entregados);
        dto.setPedidosCancelados(cancelados);
        dto.setPedidosNoEntregados(noEntregados);
        dto.setPuntajeFinal(puntajeFinal);
        dto.setNivelMaximo(nivelMaximo);

        return dto;
    }

    public static class ResumenPartidaDTO {

        private int totalPedidos;
        private int pedidosEntregados;
        private int pedidosCancelados;
        private int pedidosNoEntregados;
        private int puntajeFinal;
        private int nivelMaximo;

        public int getTotalPedidos() {
            return totalPedidos;
        }

        public void setTotalPedidos(int totalPedidos) {
            this.totalPedidos = totalPedidos;
        }

        public int getPedidosEntregados() {
            return pedidosEntregados;
        }

        public void setPedidosEntregados(int pedidosEntregados) {
            this.pedidosEntregados = pedidosEntregados;
        }

        public int getPedidosCancelados() {
            return pedidosCancelados;
        }

        public void setPedidosCancelados(int pedidosCancelados) {
            this.pedidosCancelados = pedidosCancelados;
        }

        public int getPedidosNoEntregados() {
            return pedidosNoEntregados;
        }

        public void setPedidosNoEntregados(int pedidosNoEntregados) {
            this.pedidosNoEntregados = pedidosNoEntregados;
        }

        public int getPuntajeFinal() {
            return puntajeFinal;
        }

        public void setPuntajeFinal(int puntajeFinal) {
            this.puntajeFinal = puntajeFinal;
        }

        public int getNivelMaximo() {
            return nivelMaximo;
        }

        public void setNivelMaximo(int nivelMaximo) {
            this.nivelMaximo = nivelMaximo;
        }
    }

}
