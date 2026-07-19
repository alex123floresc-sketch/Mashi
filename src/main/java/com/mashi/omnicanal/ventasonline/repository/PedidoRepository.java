package com.mashi.omnicanal.ventasonline.repository;

import com.mashi.omnicanal.ventasonline.entity.EstadoPedido;
import com.mashi.omnicanal.ventasonline.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    Optional<Pedido> findByTransaccionId(UUID transaccionId);
    List<Pedido> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);
    List<Pedido> findByEstadoAndFechaCreacionGreaterThanEqual(EstadoPedido estado, Instant desde);
}
