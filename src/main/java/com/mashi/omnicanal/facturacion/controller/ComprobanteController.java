package com.mashi.omnicanal.facturacion.controller;

import com.mashi.omnicanal.facturacion.dto.ComprobanteDTO;
import com.mashi.omnicanal.facturacion.dto.EmitirComprobanteRequest;
import com.mashi.omnicanal.facturacion.service.ComprobanteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/comprobantes")
public class ComprobanteController {

    private final ComprobanteService comprobanteService;

    public ComprobanteController(ComprobanteService comprobanteService) {
        this.comprobanteService = comprobanteService;
    }

    @PostMapping
    public ResponseEntity<ComprobanteDTO> emitir(@Valid @RequestBody EmitirComprobanteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(comprobanteService.emitir(request));
    }

    @GetMapping("/mios")
    public List<ComprobanteDTO> listarMios() {
        return comprobanteService.listarMios();
    }

    @GetMapping("/{id}")
    public ComprobanteDTO obtener(@PathVariable Long id) {
        return comprobanteService.obtener(id);
    }

    @PostMapping("/{id}/anular")
    public ComprobanteDTO anular(@PathVariable Long id) {
        return comprobanteService.anular(id);
    }
}
