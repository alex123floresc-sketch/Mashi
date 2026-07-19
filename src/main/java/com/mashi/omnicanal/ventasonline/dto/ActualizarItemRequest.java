package com.mashi.omnicanal.ventasonline.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ActualizarItemRequest(
        @NotNull(message = "la cantidad es obligatoria")
        @Positive(message = "la cantidad debe ser mayor a 0")
        Integer cantidad
) {
}
