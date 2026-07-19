package com.mashi.omnicanal.pos.dto;

import com.mashi.omnicanal.pos.entity.VentaPos;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record VentaPosDTO(
        Long id,
        UUID transaccionId,
        String vendedorNombre,
        BigDecimal total,
        List<VentaPosItemDTO> items,
        Instant fechaCreacion
) {
    public static VentaPosDTO from(VentaPos ventaPos) {
        return new VentaPosDTO(
                ventaPos.getId(),
                ventaPos.getTransaccionId(),
                ventaPos.getVendedor().getNombre(),
                ventaPos.getTotal(),
                ventaPos.getItems().stream().map(VentaPosItemDTO::from).toList(),
                ventaPos.getFechaCreacion()
        );
    }
}
