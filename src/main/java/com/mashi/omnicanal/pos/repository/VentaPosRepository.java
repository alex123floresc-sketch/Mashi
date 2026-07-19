package com.mashi.omnicanal.pos.repository;

import com.mashi.omnicanal.pos.entity.VentaPos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VentaPosRepository extends JpaRepository<VentaPos, Long> {
    Optional<VentaPos> findByTransaccionId(UUID transaccionId);
    List<VentaPos> findByVendedorIdOrderByFechaCreacionDesc(Long vendedorId);
    List<VentaPos> findByFechaCreacionGreaterThanEqual(Instant desde);
}
