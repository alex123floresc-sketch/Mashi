package com.mashi.omnicanal.ventasonline.service;

import com.mashi.omnicanal.auth.entity.Usuario;
import com.mashi.omnicanal.auth.service.UsuarioActualResolver;
import com.mashi.omnicanal.catalogo.entity.Producto;
import com.mashi.omnicanal.catalogo.service.ProductoService;
import com.mashi.omnicanal.shared.exception.ApiException;
import com.mashi.omnicanal.ventasonline.dto.PedidoDTO;
import com.mashi.omnicanal.ventasonline.entity.Carrito;
import com.mashi.omnicanal.ventasonline.entity.CarritoItem;
import com.mashi.omnicanal.ventasonline.entity.EstadoPedido;
import com.mashi.omnicanal.ventasonline.entity.Pedido;
import com.mashi.omnicanal.ventasonline.entity.PedidoItem;
import com.mashi.omnicanal.ventasonline.repository.PedidoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class CheckoutService {

    private final PedidoRepository pedidoRepository;
    private final CarritoService carritoService;
    private final ProductoService productoService;
    private final UsuarioActualResolver usuarioActualResolver;

    public CheckoutService(PedidoRepository pedidoRepository,
                            CarritoService carritoService,
                            ProductoService productoService,
                            UsuarioActualResolver usuarioActualResolver) {
        this.pedidoRepository = pedidoRepository;
        this.carritoService = carritoService;
        this.productoService = productoService;
        this.usuarioActualResolver = usuarioActualResolver;
    }

    @Transactional
    public PedidoDTO checkout(UUID transaccionId) {
        var existente = pedidoRepository.findByTransaccionId(transaccionId);
        if (existente.isPresent()) {
            return PedidoDTO.from(existente.get());
        }

        Usuario usuario = usuarioActualResolver.obtenerUsuarioActual();
        Carrito carrito = carritoService.obtenerOCrearCarrito();

        if (carrito.getItems().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "El carrito esta vacio");
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setTransaccionId(transaccionId);

        BigDecimal total = BigDecimal.ZERO;
        for (CarritoItem carritoItem : carrito.getItems()) {
            Producto producto = productoService.descontarStock(carritoItem.getProducto().getSku(), carritoItem.getCantidad());
            PedidoItem pedidoItem = new PedidoItem(pedido, producto, carritoItem.getCantidad(), producto.getPrecio());
            pedido.getItems().add(pedidoItem);
            total = total.add(producto.getPrecio().multiply(BigDecimal.valueOf(carritoItem.getCantidad())));
        }

        pedido.setTotal(total);
        pedido.setEstado(EstadoPedido.PAGADO);

        try {
            pedido = pedidoRepository.saveAndFlush(pedido);
        } catch (DataIntegrityViolationException ex) {
            return PedidoDTO.from(pedidoRepository.findByTransaccionId(transaccionId)
                    .orElseThrow(() -> ex));
        }

        carritoService.vaciarCarrito(carrito);

        return PedidoDTO.from(pedido);
    }
}
