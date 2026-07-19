package com.mashi.omnicanal.catalogo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProductoRequest(
        @NotBlank(message = "el sku es obligatorio")
        String sku,

        @NotBlank(message = "el nombre es obligatorio")
        String nombre,

        String descripcion,

        @NotNull(message = "el precio es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "el precio debe ser mayor a 0")
        BigDecimal precio,

        @NotNull(message = "la categoria es obligatoria")
        Long categoriaId,

        @NotNull(message = "el stock inicial es obligatorio")
        @PositiveOrZero(message = "el stock no puede ser negativo")
        Integer stockDisponible
) {
}
