package com.mashi.omnicanal.ventasonline.dto;

import com.mashi.omnicanal.ventasonline.entity.CarritoItem;

import java.math.BigDecimal;

public record CarritoItemDTO(
        Long itemId,
        Long productoId,
        String productoNombre,
        BigDecimal precioUnitario,
        int cantidad,
        BigDecimal subtotal
) {
    public static CarritoItemDTO from(CarritoItem item) {
        BigDecimal precio = item.getProducto().getPrecio();
        return new CarritoItemDTO(
                item.getId(),
                item.getProducto().getId(),
                item.getProducto().getNombre(),
                precio,
                item.getCantidad(),
                precio.multiply(BigDecimal.valueOf(item.getCantidad()))
        );
    }
}
