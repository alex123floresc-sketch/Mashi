package com.mashi.omnicanal.auth.service;

import com.mashi.omnicanal.auth.dto.AuthResponse;
import com.mashi.omnicanal.auth.dto.LoginRequest;
import com.mashi.omnicanal.auth.dto.RegistroRequest;
import com.mashi.omnicanal.auth.dto.UsuarioDTO;
import com.mashi.omnicanal.auth.entity.Usuario;
import com.mashi.omnicanal.auth.repository.UsuarioRepository;
import com.mashi.omnicanal.auth.security.JwtService;
import com.mashi.omnicanal.shared.exception.RegistroDuplicadoException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UsuarioDetailsServiceImpl usuarioDetailsService;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository,
                        PasswordEncoder passwordEncoder,
                        AuthenticationManager authenticationManager,
                        UsuarioDetailsServiceImpl usuarioDetailsService,
                        JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.usuarioDetailsService = usuarioDetailsService;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new RegistroDuplicadoException("Ya existe un usuario registrado con ese email");
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.nombre())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .rol(request.rol())
                .activo(true)
                .build();

        usuario = usuarioRepository.save(usuario);

        UserDetails userDetails = usuarioDetailsService.loadUserByUsername(usuario.getEmail());
        String token = jwtService.generarToken(userDetails, usuario.getId(), usuario.getRol().name());

        return AuthResponse.of(token, UsuarioDTO.from(usuario));
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalStateException("Usuario autenticado no encontrado"));

        UserDetails userDetails = usuarioDetailsService.loadUserByUsername(usuario.getEmail());
        String token = jwtService.generarToken(userDetails, usuario.getId(), usuario.getRol().name());

        return AuthResponse.of(token, UsuarioDTO.from(usuario));
    }
}
