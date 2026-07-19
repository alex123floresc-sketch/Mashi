package com.mashi.omnicanal.pos.dto;

import jakarta.validation.constraints.NotBlank;

public record EscanearQrRequest(
        @NotBlank(message = "el codigo escaneado es obligatorio")
        String codigo
) {
}
