package com.mashi.omnicanal.catalogo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequest(
        @NotBlank(message = "el nombre es obligatorio")
        @Size(max = 100, message = "el nombre no puede superar los 100 caracteres")
        String nombre,

        @Size(max = 500, message = "la descripcion no puede superar los 500 caracteres")
        String descripcion
) {
}
