package com.mashi.omnicanal.facturacion.service;

import com.mashi.omnicanal.auth.entity.RolUsuario;
import com.mashi.omnicanal.auth.entity.Usuario;
import com.mashi.omnicanal.auth.service.UsuarioActualResolver;
import com.mashi.omnicanal.facturacion.dto.ComprobanteDTO;
import com.mashi.omnicanal.facturacion.dto.EmitirComprobanteRequest;
import com.mashi.omnicanal.facturacion.entity.Comprobante;
import com.mashi.omnicanal.facturacion.entity.ComprobanteItem;
import com.mashi.omnicanal.facturacion.entity.EstadoComprobante;
import com.mashi.omnicanal.facturacion.entity.OrigenComprobante;
import com.mashi.omnicanal.facturacion.entity.TipoComprobante;
import com.mashi.omnicanal.facturacion.repository.ComprobanteRepository;
import com.mashi.omnicanal.pos.entity.VentaPos;
import com.mashi.omnicanal.pos.repository.VentaPosRepository;
import com.mashi.omnicanal.shared.exception.ApiException;
import com.mashi.omnicanal.shared.exception.RecursoNoEncontradoException;
import com.mashi.omnicanal.ventasonline.entity.EstadoPedido;
import com.mashi.omnicanal.ventasonline.entity.Pedido;
import com.mashi.omnicanal.ventasonline.repository.PedidoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ComprobanteService {

    private static final BigDecimal UNO_MAS_IGV = new BigDecimal("1.18");
    private static final int MAX_INTENTOS_CORRELATIVO = 5;

    private final ComprobanteRepository comprobanteRepository;
    private final PedidoRepository pedidoRepository;
    private final VentaPosRepository ventaPosRepository;
    private final UsuarioActualResolver usuarioActualResolver;

    public ComprobanteService(ComprobanteRepository comprobanteRepository,
                               PedidoRepository pedidoRepository,
                               VentaPosRepository ventaPosRepository,
                               UsuarioActualResolver usuarioActualResolver) {
        this.comprobanteRepository = comprobanteRepository;
        this.pedidoRepository = pedidoRepository;
        this.ventaPosRepository = ventaPosRepository;
        this.usuarioActualResolver = usuarioActualResolver;
    }

    @Transactional
    public ComprobanteDTO emitir(EmitirComprobanteRequest request) {
        var existente = comprobanteRepository.findByOrigenAndOrigenId(request.origen(), request.origenId());
        if (existente.isPresent()) {
            return ComprobanteDTO.from(existente.get());
        }

        validarDocumentoParaTipo(request.tipo(), request.clienteDocumento());

        Usuario usuarioActual = usuarioActualResolver.obtenerUsuarioActual();
        DatosOrigen datos = resolverOrigen(request.origen(), request.origenId(), usuarioActual);

        BigDecimal total = datos.total();
        BigDecimal subtotal = total.divide(UNO_MAS_IGV, 2, RoundingMode.HALF_UP);
        BigDecimal igv = total.subtract(subtotal);

        for (int intento = 0; intento < MAX_INTENTOS_CORRELATIVO; intento++) {
            String serie = serieParaTipo(request.tipo());
            int correlativo = siguienteCorrelativo(serie);

            Comprobante comprobante = new Comprobante();
            comprobante.setTipo(request.tipo());
            comprobante.setSerie(serie);
            comprobante.setCorrelativo(correlativo);
            comprobante.setOrigen(request.origen());
            comprobante.setOrigenId(request.origenId());
            comprobante.setClienteDocumento(request.clienteDocumento());
            comprobante.setClienteNombre(request.clienteNombre());
            comprobante.setSubtotal(subtotal);
            comprobante.setIgv(igv);
            comprobante.setTotal(total);
            comprobante.setEstado(EstadoComprobante.EMITIDO);
            for (ItemOrigen item : datos.items()) {
                comprobante.getItems().add(new ComprobanteItem(
                        comprobante, item.descripcion(), item.cantidad(), item.precioUnitario(), item.subtotal()));
            }

            try {
                return ComprobanteDTO.from(comprobanteRepository.saveAndFlush(comprobante));
            } catch (DataIntegrityViolationException ex) {
                var porOrigen = comprobanteRepository.findByOrigenAndOrigenId(request.origen(), request.origenId());
                if (porOrigen.isPresent()) {
                    return ComprobanteDTO.from(porOrigen.get());
                }
                // colision de correlativo: reintentar con el siguiente disponible
            }
        }

        throw new ApiException(HttpStatus.CONFLICT, "No se pudo generar el numero de comprobante, intente nuevamente");
    }

    @Transactional
    public ComprobanteDTO anular(Long id) {
        Comprobante comprobante = buscarPorId(id);
        if (comprobante.getEstado() == EstadoComprobante.ANULADO) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "El comprobante ya esta anulado");
        }
        comprobante.setEstado(EstadoComprobante.ANULADO);
        return ComprobanteDTO.from(comprobanteRepository.save(comprobante));
    }

    public ComprobanteDTO obtener(Long id) {
        return ComprobanteDTO.from(buscarPorId(id));
    }

    public List<ComprobanteDTO> listarMios() {
        Usuario usuarioActual = usuarioActualResolver.obtenerUsuarioActual();

        if (usuarioActual.getRol() == RolUsuario.ADMINISTRADOR) {
            return comprobanteRepository.findAllByOrderByFechaEmisionDesc().stream().map(ComprobanteDTO::from).toList();
        }

        if (usuarioActual.getRol() == RolUsuario.VENDEDOR) {
            List<Long> ids = ventaPosRepository.findByVendedorIdOrderByFechaCreacionDesc(usuarioActual.getId())
                    .stream().map(VentaPos::getId).toList();
            return comprobanteRepository.findByOrigenAndOrigenIdInOrderByFechaEmisionDesc(OrigenComprobante.VENTA_POS, ids)
                    .stream().map(ComprobanteDTO::from).toList();
        }

        List<Long> ids = pedidoRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioActual.getId())
                .stream().map(Pedido::getId).toList();
        return comprobanteRepository.findByOrigenAndOrigenIdInOrderByFechaEmisionDesc(OrigenComprobante.PEDIDO, ids)
                .stream().map(ComprobanteDTO::from).toList();
    }

    private DatosOrigen resolverOrigen(OrigenComprobante origen, Long origenId, Usuario usuarioActual) {
        if (origen == OrigenComprobante.PEDIDO) {
            Pedido pedido = pedidoRepository.findById(origenId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado: " + origenId));

            boolean esDueno = pedido.getUsuario().getId().equals(usuarioActual.getId());
            if (!esDueno && usuarioActual.getRol() != RolUsuario.ADMINISTRADOR) {
                throw new ApiException(HttpStatus.FORBIDDEN, "No tiene permisos para emitir comprobante de este pedido");
            }
            if (pedido.getEstado() != EstadoPedido.PAGADO) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "El pedido no esta pagado");
            }

            List<ItemOrigen> items = pedido.getItems().stream()
                    .map(i -> new ItemOrigen(
                            i.getProducto().getNombre(),
                            i.getCantidad(),
                            i.getPrecioUnitario(),
                            i.getPrecioUnitario().multiply(BigDecimal.valueOf(i.getCantidad()))))
                    .toList();
            return new DatosOrigen(pedido.getTotal(), items);
        }

        VentaPos ventaPos = ventaPosRepository.findById(origenId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta POS no encontrada: " + origenId));

        boolean esVendedor = ventaPos.getVendedor().getId().equals(usuarioActual.getId());
        if (!esVendedor && usuarioActual.getRol() != RolUsuario.ADMINISTRADOR) {
            throw new ApiException(HttpStatus.FORBIDDEN, "No tiene permisos para emitir comprobante de esta venta");
        }

        List<ItemOrigen> items = ventaPos.getItems().stream()
                .map(i -> new ItemOrigen(
                        i.getProducto().getNombre(),
                        i.getCantidad(),
                        i.getPrecioUnitario(),
                        i.getPrecioUnitario().multiply(BigDecimal.valueOf(i.getCantidad()))))
                .toList();
        return new DatosOrigen(ventaPos.getTotal(), items);
    }

    private void validarDocumentoParaTipo(TipoComprobante tipo, String documento) {
        if (tipo == TipoComprobante.FACTURA && documento.length() != 11) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "La factura requiere RUC de 11 digitos");
        }
        if (tipo == TipoComprobante.BOLETA && documento.length() != 8) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "La boleta requiere DNI de 8 digitos");
        }
    }

    private String serieParaTipo(TipoComprobante tipo) {
        return tipo == TipoComprobante.FACTURA ? "F001" : "B001";
    }

    private int siguienteCorrelativo(String serie) {
        return comprobanteRepository.findTopBySerieOrderByCorrelativoDesc(serie)
                .map(c -> c.getCorrelativo() + 1)
                .orElse(1);
    }

    private Comprobante buscarPorId(Long id) {
        return comprobanteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Comprobante no encontrado: " + id));
    }

    private record ItemOrigen(String descripcion, int cantidad, BigDecimal precioUnitario, BigDecimal subtotal) {
    }

    private record DatosOrigen(BigDecimal total, List<ItemOrigen> items) {
    }
}
