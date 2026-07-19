package com.mashi.omnicanal.catalogo.dto;

import jakarta.validation.constraints.NotNull;

public record ActualizarStockRequest(
        @NotNull(message = "la cantidad es obligatoria")
        Integer cantidad
) {
}
