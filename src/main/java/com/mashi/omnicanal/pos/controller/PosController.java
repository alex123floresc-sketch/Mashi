package com.mashi.omnicanal.pos.controller;

import com.mashi.omnicanal.catalogo.dto.ProductoDTO;
import com.mashi.omnicanal.pos.dto.EscanearQrRequest;
import com.mashi.omnicanal.pos.dto.VentaPosDTO;
import com.mashi.omnicanal.pos.dto.VentaPosRequest;
import com.mashi.omnicanal.pos.service.PosService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pos")
public class PosController {

    private final PosService posService;

    public PosController(PosService posService) {
        this.posService = posService;
    }

    @PostMapping("/escanear")
    public ProductoDTO escanear(@Valid @RequestBody EscanearQrRequest request) {
        return posService.escanear(request.codigo());
    }

    @PostMapping("/ventas")
    public ResponseEntity<VentaPosDTO> registrarVenta(@Valid @RequestBody VentaPosRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(posService.registrarVenta(request.transaccionId(), request.items()));
    }
}
