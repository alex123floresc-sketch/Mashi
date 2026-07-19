package com.mashi.omnicanal.facturacion.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comprobantes", uniqueConstraints = {
        @UniqueConstraint(name = "uk_comprobante_serie_correlativo", columnNames = {"serie", "correlativo"}),
        @UniqueConstraint(name = "uk_comprobante_origen", columnNames = {"origen", "origen_id"})
})
@Getter
@Setter
@NoArgsConstructor
public class Comprobante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoComprobante tipo;

    @Column(nullable = false, length = 4)
    private String serie;

    @Column(nullable = false)
    private int correlativo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrigenComprobante origen;

    @Column(name = "origen_id", nullable = false)
    private Long origenId;

    @Column(name = "cliente_documento", nullable = false, length = 11)
    private String clienteDocumento;

    @Column(name = "cliente_nombre", nullable = false, length = 150)
    private String clienteNombre;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal igv;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoComprobante estado;

    @OneToMany(mappedBy = "comprobante", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComprobanteItem> items = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "fecha_emision", nullable = false, updatable = false)
    private Instant fechaEmision;

    public String getNumero() {
        return serie + "-" + String.format("%06d", correlativo);
    }
}
