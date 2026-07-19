package com.mashi.omnicanal.facturacion.dto;

import com.mashi.omnicanal.facturacion.entity.ComprobanteItem;

import java.math.BigDecimal;

public record ComprobanteItemDTO(
        String descripcion,
        int cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {
    public static ComprobanteItemDTO from(ComprobanteItem item) {
        return new ComprobanteItemDTO(
                item.getDescripcion(),
                item.getCantidad(),
                item.getPrecioUnitario(),
                item.getSubtotal()
        );
    }
}
