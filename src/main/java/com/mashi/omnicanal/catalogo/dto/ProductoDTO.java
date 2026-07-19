package com.mashi.omnicanal.catalogo.dto;

import com.mashi.omnicanal.catalogo.entity.Producto;

import java.math.BigDecimal;

public record ProductoDTO(
        Long id,
        String sku,
        String nombre,
        String descripcion,
        BigDecimal precio,
        Long categoriaId,
        String categoriaNombre,
        int stockDisponible,
        boolean activo
) {
    public static ProductoDTO from(Producto producto) {
        return new ProductoDTO(
                producto.getId(),
                producto.getSku(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getCategoria() != null ? producto.getCategoria().getId() : null,
                producto.getCategoria() != null ? producto.getCategoria().getNombre() : null,
                producto.getStockDisponible(),
                producto.isActivo()
        );
    }
}
