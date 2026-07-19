package com.mashi.omnicanal.facturacion.repository;

import com.mashi.omnicanal.facturacion.entity.Comprobante;
import com.mashi.omnicanal.facturacion.entity.OrigenComprobante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ComprobanteRepository extends JpaRepository<Comprobante, Long> {
    Optional<Comprobante> findByOrigenAndOrigenId(OrigenComprobante origen, Long origenId);
    Optional<Comprobante> findTopBySerieOrderByCorrelativoDesc(String serie);
    List<Comprobante> findByOrigenAndOrigenIdInOrderByFechaEmisionDesc(OrigenComprobante origen, List<Long> origenIds);
    List<Comprobante> findAllByOrderByFechaEmisionDesc();
}
