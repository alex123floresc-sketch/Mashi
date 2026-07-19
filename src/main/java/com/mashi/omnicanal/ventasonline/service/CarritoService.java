package com.mashi.omnicanal.ventasonline.service;

import com.mashi.omnicanal.auth.entity.Usuario;
import com.mashi.omnicanal.auth.service.UsuarioActualResolver;
import com.mashi.omnicanal.catalogo.entity.Producto;
import com.mashi.omnicanal.catalogo.service.ProductoService;
import com.mashi.omnicanal.shared.exception.RecursoNoEncontradoException;
import com.mashi.omnicanal.shared.exception.StockInsuficienteException;
import com.mashi.omnicanal.ventasonline.dto.CarritoDTO;
import com.mashi.omnicanal.ventasonline.entity.Carrito;
import com.mashi.omnicanal.ventasonline.entity.CarritoItem;
import com.mashi.omnicanal.ventasonline.repository.CarritoItemRepository;
import com.mashi.omnicanal.ventasonline.repository.CarritoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final ProductoService productoService;
    private final UsuarioActualResolver usuarioActualResolver;

    public CarritoService(CarritoRepository carritoRepository,
                           CarritoItemRepository carritoItemRepository,
                           ProductoService productoService,
                           UsuarioActualResolver usuarioActualResolver) {
        this.carritoRepository = carritoRepository;
        this.carritoItemRepository = carritoItemRepository;
        this.productoService = productoService;
        this.usuarioActualResolver = usuarioActualResolver;
    }

    public CarritoDTO obtenerCarritoActual() {
        return CarritoDTO.from(obtenerOCrearCarrito());
    }

    @Transactional
    public CarritoDTO agregarItem(Long productoId, int cantidad) {
        Carrito carrito = obtenerOCrearCarrito();
        Producto producto = productoService.buscarPorId(productoId);

        if (!producto.isActivo()) {
            throw new RecursoNoEncontradoException("Producto no disponible: " + productoId);
        }

        CarritoItem existente = carrito.getItems().stream()
                .filter(item -> item.getProducto().getId().equals(productoId))
                .findFirst()
                .orElse(null);

        int cantidadTotal = (existente != null ? existente.getCantidad() : 0) + cantidad;
        if (producto.getStockDisponible() < cantidadTotal) {
            throw new StockInsuficienteException("Stock insuficiente para el producto " + producto.getSku());
        }

        if (existente != null) {
            existente.setCantidad(cantidadTotal);
        } else {
            carrito.getItems().add(new CarritoItem(carrito, producto, cantidad));
        }

        carritoRepository.save(carrito);
        return CarritoDTO.from(carrito);
    }

    @Transactional
    public CarritoDTO actualizarItem(Long itemId, int cantidad) {
        Carrito carrito = obtenerOCrearCarrito();
        CarritoItem item = buscarItemDelCarrito(carrito, itemId);

        if (item.getProducto().getStockDisponible() < cantidad) {
            throw new StockInsuficienteException("Stock insuficiente para el producto " + item.getProducto().getSku());
        }

        item.setCantidad(cantidad);
        carritoRepository.save(carrito);
        return CarritoDTO.from(carrito);
    }

    @Transactional
    public CarritoDTO eliminarItem(Long itemId) {
        Carrito carrito = obtenerOCrearCarrito();
        CarritoItem item = buscarItemDelCarrito(carrito, itemId);
        carrito.getItems().remove(item);
        carritoItemRepository.delete(item);
        return CarritoDTO.from(carrito);
    }

    @Transactional
    public void vaciarCarrito(Carrito carrito) {
        carrito.getItems().clear();
        carritoRepository.save(carrito);
    }

    @Transactional
    public Carrito obtenerOCrearCarrito() {
        Usuario usuario = usuarioActualResolver.obtenerUsuarioActual();
        return carritoRepository.findByUsuarioId(usuario.getId())
                .orElseGet(() -> carritoRepository.save(new Carrito(usuario)));
    }

    private CarritoItem buscarItemDelCarrito(Carrito carrito, Long itemId) {
        return carrito.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RecursoNoEncontradoException("Item de carrito no encontrado: " + itemId));
    }
}
