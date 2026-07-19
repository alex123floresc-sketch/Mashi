package com.mashi.omnicanal.analitica.dto;

import java.math.BigDecimal;
import java.util.List;

public record ResumenAnaliticaDTO(
        BigDecimal totalVentasOnline,
        BigDecimal totalVentasPos,
        BigDecimal totalVentasGeneral,
        long cantidadPedidos,
        long cantidadVentasPos,
        List<VentaPorDiaDTO> ventasPorDia,
        List<ProductoMasVendidoDTO> topProductos
) {
}
