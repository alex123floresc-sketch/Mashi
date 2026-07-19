package com.mashi.omnicanal.catalogo.service;

import com.mashi.omnicanal.catalogo.dto.ProductoDTO;
import com.mashi.omnicanal.catalogo.dto.ProductoRequest;
import com.mashi.omnicanal.catalogo.entity.Categoria;
import com.mashi.omnicanal.catalogo.entity.Producto;
import com.mashi.omnicanal.catalogo.repository.CategoriaRepository;
import com.mashi.omnicanal.catalogo.repository.ProductoRepository;
import com.mashi.omnicanal.shared.exception.RecursoNoEncontradoException;
import com.mashi.omnicanal.shared.exception.RegistroDuplicadoException;
import com.mashi.omnicanal.shared.exception.StockInsuficienteException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<ProductoDTO> listarActivos() {
        return productoRepository.findByActivoTrue().stream().map(ProductoDTO::from).toList();
    }

    public List<ProductoDTO> listarTodos() {
        return productoRepository.findAll().stream().map(ProductoDTO::from).toList();
    }

    public ProductoDTO obtener(Long id) {
        return ProductoDTO.from(buscarPorId(id));
    }

    public ProductoDTO obtenerPorSku(String sku) {
        return ProductoDTO.from(buscarPorSku(sku));
    }

    @Transactional
    public ProductoDTO crear(ProductoRequest request) {
        if (productoRepository.existsBySku(request.sku())) {
            throw new RegistroDuplicadoException("Ya existe un producto con ese SKU");
        }
        Categoria categoria = buscarCategoria(request.categoriaId());

        Producto producto = Producto.builder()
                .sku(request.sku())
                .nombre(request.nombre())
                .descripcion(request.descripcion())
                .precio(request.precio())
                .categoria(categoria)
                .stockDisponible(request.stockDisponible())
                .activo(true)
                .build();

        return ProductoDTO.from(productoRepository.save(producto));
    }

    @Transactional
    public ProductoDTO actualizar(Long id, ProductoRequest request) {
        Producto producto = buscarPorId(id);
        Categoria categoria = buscarCategoria(request.categoriaId());

        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        producto.setCategoria(categoria);
        producto.setStockDisponible(request.stockDisponible());

        return ProductoDTO.from(productoRepository.save(producto));
    }

    @Transactional
    public ProductoDTO actualizarStock(Long id, int cantidad) {
        Producto producto = buscarPorId(id);
        producto.setStockDisponible(cantidad);
        return ProductoDTO.from(productoRepository.save(producto));
    }

    @Transactional
    public void desactivar(Long id) {
        Producto producto = buscarPorId(id);
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    @Transactional
    public Producto descontarStock(String sku, int cantidad) {
        Producto producto = buscarPorSku(sku);
        if (producto.getStockDisponible() < cantidad) {
            throw new StockInsuficienteException("Stock insuficiente para el producto " + producto.getSku());
        }
        producto.setStockDisponible(producto.getStockDisponible() - cantidad);
        return productoRepository.save(producto);
    }

    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado: " + id));
    }

    public Producto buscarPorSku(String sku) {
        return productoRepository.findBySku(sku)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado para el SKU: " + sku));
    }

    private Categoria buscarCategoria(Long categoriaId) {
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoria no encontrada: " + categoriaId));
    }
}
