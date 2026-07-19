package com.mashi.omnicanal.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "el email es obligatorio")
        String email,

        @NotBlank(message = "la contrasena es obligatoria")
        String password
) {
}
