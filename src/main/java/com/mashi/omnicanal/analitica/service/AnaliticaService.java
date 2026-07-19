package com.mashi.omnicanal.analitica.service;

import com.mashi.omnicanal.analitica.dto.ProductoMasVendidoDTO;
import com.mashi.omnicanal.analitica.dto.ResumenAnaliticaDTO;
import com.mashi.omnicanal.analitica.dto.VentaPorDiaDTO;
import com.mashi.omnicanal.catalogo.entity.Producto;
import com.mashi.omnicanal.pos.entity.VentaPos;
import com.mashi.omnicanal.pos.repository.VentaPosRepository;
import com.mashi.omnicanal.ventasonline.entity.EstadoPedido;
import com.mashi.omnicanal.ventasonline.entity.Pedido;
import com.mashi.omnicanal.ventasonline.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnaliticaService {

    private final PedidoRepository pedidoRepository;
    private final VentaPosRepository ventaPosRepository;

    public AnaliticaService(PedidoRepository pedidoRepository, VentaPosRepository ventaPosRepository) {
        this.pedidoRepository = pedidoRepository;
        this.ventaPosRepository = ventaPosRepository;
    }

    public ResumenAnaliticaDTO resumen(int dias) {
        int rango = dias > 0 ? dias : 30;
        Instant desde = Instant.now().minus(rango, ChronoUnit.DAYS);

        List<Pedido> pedidos = pedidoRepository.findByEstadoAndFechaCreacionGreaterThanEqual(EstadoPedido.PAGADO, desde);
        List<VentaPos> ventasPos = ventaPosRepository.findByFechaCreacionGreaterThanEqual(desde);

        BigDecimal totalOnline = pedidos.stream().map(Pedido::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPos = ventasPos.stream().map(VentaPos::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        List<VentaPorDiaDTO> ventasPorDia = calcularVentasPorDia(pedidos, ventasPos, rango);
        List<ProductoMasVendidoDTO> topProductos = calcularTopProductos(pedidos, ventasPos);

        return new ResumenAnaliticaDTO(
                totalOnline,
                totalPos,
                totalOnline.add(totalPos),
                pedidos.size(),
                ventasPos.size(),
                ventasPorDia,
                topProductos);
    }

    private List<VentaPorDiaDTO> calcularVentasPorDia(List<Pedido> pedidos, List<VentaPos> ventasPos, int dias) {
        Map<LocalDate, BigDecimal> onlinePorDia = new HashMap<>();
        for (Pedido pedido : pedidos) {
            LocalDate fecha = LocalDate.ofInstant(pedido.getFechaCreacion(), ZoneOffset.UTC);
            onlinePorDia.merge(fecha, pedido.getTotal(), BigDecimal::add);
        }

        Map<LocalDate, BigDecimal> posPorDia = new HashMap<>();
        for (VentaPos venta : ventasPos) {
            LocalDate fecha = LocalDate.ofInstant(venta.getFechaCreacion(), ZoneOffset.UTC);
            posPorDia.merge(fecha, venta.getTotal(), BigDecimal::add);
        }

        List<VentaPorDiaDTO> resultado = new ArrayList<>();
        LocalDate hoy = LocalDate.now(ZoneOffset.UTC);
        for (int i = dias - 1; i >= 0; i--) {
            LocalDate fecha = hoy.minusDays(i);
            resultado.add(new VentaPorDiaDTO(
                    fecha,
                    onlinePorDia.getOrDefault(fecha, BigDecimal.ZERO),
                    posPorDia.getOrDefault(fecha, BigDecimal.ZERO)));
        }
        return resultado;
    }

    private List<ProductoMasVendidoDTO> calcularTopProductos(List<Pedido> pedidos, List<VentaPos> ventasPos) {
        Map<String, ProductoAcumulado> acumulado = new HashMap<>();
        pedidos.forEach(pedido -> pedido.getItems().forEach(item ->
                acumular(acumulado, item.getProducto(), item.getCantidad(), item.getPrecioUnitario())));
        ventasPos.forEach(venta -> venta.getItems().forEach(item ->
                acumular(acumulado, item.getProducto(), item.getCantidad(), item.getPrecioUnitario())));

        return acumulado.values().stream()
                .sorted(Comparator.comparingLong(ProductoAcumulado::cantidad).reversed())
                .limit(5)
                .map(pa -> new ProductoMasVendidoDTO(pa.sku(), pa.nombre(), pa.cantidad(), pa.total()))
                .toList();
    }

    private void acumular(Map<String, ProductoAcumulado> acumulado, Producto producto, int cantidad, BigDecimal precioUnitario) {
        BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        acumulado.merge(
                producto.getSku(),
                new ProductoAcumulado(producto.getSku(), producto.getNombre(), cantidad, subtotal),
                (a, b) -> new ProductoAcumulado(a.sku(), a.nombre(), a.cantidad() + b.cantidad(), a.total().add(b.total())));
    }

    private record ProductoAcumulado(String sku, String nombre, long cantidad, BigDecimal total) {
    }
}
