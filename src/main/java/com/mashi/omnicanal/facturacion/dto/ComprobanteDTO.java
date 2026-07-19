package com.mashi.omnicanal.facturacion.dto;

import com.mashi.omnicanal.facturacion.entity.Comprobante;
import com.mashi.omnicanal.facturacion.entity.EstadoComprobante;
import com.mashi.omnicanal.facturacion.entity.OrigenComprobante;
import com.mashi.omnicanal.facturacion.entity.TipoComprobante;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ComprobanteDTO(
        Long id,
        String numero,
        TipoComprobante tipo,
        OrigenComprobante origen,
        Long origenId,
        String clienteDocumento,
        String clienteNombre,
        BigDecimal subtotal,
        BigDecimal igv,
        BigDecimal total,
        EstadoComprobante estado,
        List<ComprobanteItemDTO> items,
        Instant fechaEmision
) {
    public static ComprobanteDTO from(Comprobante comprobante) {
        return new ComprobanteDTO(
                comprobante.getId(),
                comprobante.getNumero(),
                comprobante.getTipo(),
                comprobante.getOrigen(),
                comprobante.getOrigenId(),
                comprobante.getClienteDocumento(),
                comprobante.getClienteNombre(),
                comprobante.getSubtotal(),
                comprobante.getIgv(),
                comprobante.getTotal(),
                comprobante.getEstado(),
                comprobante.getItems().stream().map(ComprobanteItemDTO::from).toList(),
                comprobante.getFechaEmision()
        );
    }
}
