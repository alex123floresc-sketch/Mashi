package com.mashi.omnicanal.auth.service;

import com.mashi.omnicanal.auth.entity.Usuario;
import com.mashi.omnicanal.auth.repository.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioActualResolver {

    private final UsuarioRepository usuarioRepository;

    public UsuarioActualResolver(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario obtenerUsuarioActual() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuario autenticado no encontrado: " + email));
    }
}
