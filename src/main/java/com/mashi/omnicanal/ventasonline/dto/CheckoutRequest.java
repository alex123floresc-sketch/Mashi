package com.mashi.omnicanal.ventasonline.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CheckoutRequest(
        @NotNull(message = "el transaccionId es obligatorio")
        UUID transaccionId
) {
}
