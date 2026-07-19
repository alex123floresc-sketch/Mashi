package com.mashi.omnicanal.ventasonline.controller;

import com.mashi.omnicanal.ventasonline.dto.ActualizarItemRequest;
import com.mashi.omnicanal.ventasonline.dto.AgregarItemRequest;
import com.mashi.omnicanal.ventasonline.dto.CarritoDTO;
import com.mashi.omnicanal.ventasonline.service.CarritoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping
    public CarritoDTO obtener() {
        return carritoService.obtenerCarritoActual();
    }

    @PostMapping("/items")
    public ResponseEntity<CarritoDTO> agregarItem(@Valid @RequestBody AgregarItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(carritoService.agregarItem(request.productoId(), request.cantidad()));
    }

    @PutMapping("/items/{itemId}")
    public CarritoDTO actualizarItem(@PathVariable Long itemId, @Valid @RequestBody ActualizarItemRequest request) {
        return carritoService.actualizarItem(itemId, request.cantidad());
    }

    @DeleteMapping("/items/{itemId}")
    public CarritoDTO eliminarItem(@PathVariable Long itemId) {
        return carritoService.eliminarItem(itemId);
    }
}
