package com.mashi.omnicanal.ventasonline.dto;

import com.mashi.omnicanal.ventasonline.entity.EstadoPedido;
import com.mashi.omnicanal.ventasonline.entity.Pedido;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PedidoDTO(
        Long id,
        UUID transaccionId,
        EstadoPedido estado,
        BigDecimal total,
        List<PedidoItemDTO> items,
        Instant fechaCreacion
) {
    public static PedidoDTO from(Pedido pedido) {
        return new PedidoDTO(
                pedido.getId(),
                pedido.getTransaccionId(),
                pedido.getEstado(),
                pedido.getTotal(),
                pedido.getItems().stream().map(PedidoItemDTO::from).toList(),
                pedido.getFechaCreacion()
        );
    }
}
