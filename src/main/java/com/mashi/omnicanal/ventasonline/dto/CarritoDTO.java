package com.mashi.omnicanal.ventasonline.dto;

import com.mashi.omnicanal.ventasonline.entity.Carrito;

import java.math.BigDecimal;
import java.util.List;

public record CarritoDTO(
        Long id,
        List<CarritoItemDTO> items,
        BigDecimal total
) {
    public static CarritoDTO from(Carrito carrito) {
        List<CarritoItemDTO> items = carrito.getItems().stream().map(CarritoItemDTO::from).toList();
        BigDecimal total = items.stream()
                .map(CarritoItemDTO::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CarritoDTO(carrito.getId(), items, total);
    }
}
