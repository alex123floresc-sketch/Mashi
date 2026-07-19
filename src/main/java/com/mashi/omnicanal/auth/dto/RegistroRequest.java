package com.mashi.omnicanal.auth.dto;

import com.mashi.omnicanal.auth.entity.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
        @NotBlank(message = "el nombre es obligatorio")
        String nombre,

        @NotBlank(message = "el email es obligatorio")
        @Email(message = "el email no tiene un formato valido")
        String email,

        @NotBlank(message = "la contrasena es obligatoria")
        @Size(min = 8, message = "la contrasena debe tener al menos 8 caracteres")
        String password,

        @NotNull(message = "el rol es obligatorio")
        RolUsuario rol
) {
}
