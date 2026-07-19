package com.mashi.omnicanal.pos.dto;

import com.mashi.omnicanal.pos.entity.VentaPosItem;

import java.math.BigDecimal;

public record VentaPosItemDTO(
        Long productoId,
        String sku,
        String productoNombre,
        int cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {
    public static VentaPosItemDTO from(VentaPosItem item) {
        BigDecimal subtotal = item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad()));
        return new VentaPosItemDTO(
                item.getProducto().getId(),
                item.getProducto().getSku(),
                item.getProducto().getNombre(),
                item.getCantidad(),
                item.getPrecioUnitario(),
                subtotal
        );
    }
}
