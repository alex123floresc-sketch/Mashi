package com.mashi.omnicanal.catalogo.controller;

import com.mashi.omnicanal.catalogo.dto.ActualizarStockRequest;
import com.mashi.omnicanal.catalogo.dto.ProductoDTO;
import com.mashi.omnicanal.catalogo.dto.ProductoRequest;
import com.mashi.omnicanal.catalogo.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<ProductoDTO> listar() {
        return productoService.listarActivos();
    }

    @GetMapping("/todos")
    public List<ProductoDTO> listarTodos() {
        return productoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ProductoDTO obtener(@PathVariable Long id) {
        return productoService.obtener(id);
    }

    @GetMapping("/sku/{sku}")
    public ProductoDTO obtenerPorSku(@PathVariable String sku) {
        return productoService.obtenerPorSku(sku);
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> crear(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(request));
    }

    @PutMapping("/{id}")
    public ProductoDTO actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        return productoService.actualizar(id, request);
    }

    @PatchMapping("/{id}/stock")
    public ProductoDTO actualizarStock(@PathVariable Long id, @Valid @RequestBody ActualizarStockRequest request) {
        return productoService.actualizarStock(id, request.cantidad());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        productoService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
