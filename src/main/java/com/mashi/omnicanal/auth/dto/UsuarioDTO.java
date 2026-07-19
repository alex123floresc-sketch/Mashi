package com.mashi.omnicanal.auth.dto;

import com.mashi.omnicanal.auth.entity.RolUsuario;
import com.mashi.omnicanal.auth.entity.Usuario;

public record UsuarioDTO(
        Long id,
        String nombre,
        String email,
        RolUsuario rol
) {
    public static UsuarioDTO from(Usuario usuario) {
        return new UsuarioDTO(usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.getRol());
    }
}
