package com.mashi.omnicanal.auth.dto;

public record AuthResponse(
        String token,
        String tipo,
        UsuarioDTO usuario
) {
    public static AuthResponse of(String token, UsuarioDTO usuario) {
        return new AuthResponse(token, "Bearer", usuario);
    }
}
