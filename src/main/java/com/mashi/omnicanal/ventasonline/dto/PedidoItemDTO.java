package com.mashi.omnicanal.ventasonline.dto;

import com.mashi.omnicanal.ventasonline.entity.PedidoItem;

import java.math.BigDecimal;

public record PedidoItemDTO(
        Long productoId,
        String productoNombre,
        int cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {
    public static PedidoItemDTO from(PedidoItem item) {
        BigDecimal subtotal = item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad()));
        return new PedidoItemDTO(
                item.getProducto().getId(),
                item.getProducto().getNombre(),
                item.getCantidad(),
                item.getPrecioUnitario(),
                subtotal
        );
    }
}
