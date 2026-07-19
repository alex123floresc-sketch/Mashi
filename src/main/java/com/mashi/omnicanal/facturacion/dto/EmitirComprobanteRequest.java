package com.mashi.omnicanal.facturacion.dto;

import com.mashi.omnicanal.facturacion.entity.OrigenComprobante;
import com.mashi.omnicanal.facturacion.entity.TipoComprobante;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record EmitirComprobanteRequest(
        @NotNull OrigenComprobante origen,
        @NotNull Long origenId,
        @NotNull TipoComprobante tipo,
        @NotBlank @Pattern(regexp = "\\d{8}|\\d{11}", message = "Debe ser un DNI de 8 digitos o un RUC de 11 digitos")
        String clienteDocumento,
        @NotBlank String clienteNombre
) {
}
