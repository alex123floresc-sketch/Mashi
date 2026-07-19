package com.mashi.omnicanal.pos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ItemVentaPosRequest(
        @NotBlank(message = "el sku es obligatorio")
        String sku,

        @Positive(message = "la cantidad debe ser mayor a 0")
        int cantidad
) {
}
