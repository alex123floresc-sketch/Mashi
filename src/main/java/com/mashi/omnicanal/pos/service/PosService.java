package com.mashi.omnicanal.pos.service;

import com.mashi.omnicanal.auth.entity.Usuario;
import com.mashi.omnicanal.auth.service.UsuarioActualResolver;
import com.mashi.omnicanal.catalogo.dto.ProductoDTO;
import com.mashi.omnicanal.catalogo.entity.Producto;
import com.mashi.omnicanal.catalogo.service.ProductoService;
import com.mashi.omnicanal.pos.dto.ItemVentaPosRequest;
import com.mashi.omnicanal.pos.dto.VentaPosDTO;
import com.mashi.omnicanal.pos.entity.VentaPos;
import com.mashi.omnicanal.pos.entity.VentaPosItem;
import com.mashi.omnicanal.pos.repository.VentaPosRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class PosService {

    private final VentaPosRepository ventaPosRepository;
    private final ProductoService productoService;
    private final UsuarioActualResolver usuarioActualResolver;

    public PosService(VentaPosRepository ventaPosRepository,
                       ProductoService productoService,
                       UsuarioActualResolver usuarioActualResolver) {
        this.ventaPosRepository = ventaPosRepository;
        this.productoService = productoService;
        this.usuarioActualResolver = usuarioActualResolver;
    }

    public ProductoDTO escanear(String codigo) {
        return productoService.obtenerPorSku(codigo);
    }

    @Transactional
    public VentaPosDTO registrarVenta(UUID transaccionId, List<ItemVentaPosRequest> items) {
        var existente = ventaPosRepository.findByTransaccionId(transaccionId);
        if (existente.isPresent()) {
            return VentaPosDTO.from(existente.get());
        }

        Usuario vendedor = usuarioActualResolver.obtenerUsuarioActual();

        VentaPos ventaPos = new VentaPos();
        ventaPos.setVendedor(vendedor);
        ventaPos.setTransaccionId(transaccionId);

        BigDecimal total = BigDecimal.ZERO;
        for (ItemVentaPosRequest itemRequest : items) {
            Producto producto = productoService.descontarStock(itemRequest.sku(), itemRequest.cantidad());
            VentaPosItem item = new VentaPosItem(ventaPos, producto, itemRequest.cantidad(), producto.getPrecio());
            ventaPos.getItems().add(item);
            total = total.add(producto.getPrecio().multiply(BigDecimal.valueOf(itemRequest.cantidad())));
        }

        ventaPos.setTotal(total);

        try {
            ventaPos = ventaPosRepository.saveAndFlush(ventaPos);
        } catch (DataIntegrityViolationException ex) {
            return VentaPosDTO.from(ventaPosRepository.findByTransaccionId(transaccionId)
                    .orElseThrow(() -> ex));
        }

        return VentaPosDTO.from(ventaPos);
    }
}
