package com.mashi.omnicanal.analitica.dto;

import java.math.BigDecimal;

public record ProductoMasVendidoDTO(
        String sku,
        String nombre,
        long cantidadVendida,
        BigDecimal totalVendido
) {
}
