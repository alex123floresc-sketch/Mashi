package com.mashi.omnicanal.analitica.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VentaPorDiaDTO(
        LocalDate fecha,
        BigDecimal totalOnline,
        BigDecimal totalPos
) {
}
