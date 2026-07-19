package com.mashi.omnicanal.pos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record VentaPosRequest(
        @NotNull(message = "el transaccionId es obligatorio")
        UUID transaccionId,

        @NotEmpty(message = "la venta debe tener al menos un item")
        @Valid
        List<ItemVentaPosRequest> items
) {
}
