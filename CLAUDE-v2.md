# CLAUDE.md

## Sobre el proyecto
Mashi: e-commerce omnicanal con tienda online (Cliente), punto de venta
físico con lector QR (Vendedor), y panel de analítica (Administrador),
con comprobantes electrónicos estilo SUNAT.

## Stack
- Backend: Java 21, Spring Boot 3.x, Spring Security (JWT), Hibernate/JPA
- Base de datos: PostgreSQL (fuente de verdad transaccional). MongoDB
  solo si se justifica (ej. catálogo con atributos variables).
- Frontend: Semantic UI para la interfaz de usuario

## Arquitectura
- Package by feature, NO por capas. Cada feature con su propio
  controller/service/repository/dto/entity.
- DTOs obligatorios en todos los endpoints — nunca exponer entidades JPA.
- Módulos: auth, catalogo, ventasonline, pos, facturacion, analitica, shared
- Paquete raíz sugerido: `com.mashi.omnicanal`

## Comandos
- Backend: `./mvnw spring-boot:run`
- Tests backend: `./mvnw test`

## Convenciones
- Nombres de paquetes y clases en inglés; nombres de negocio (comprobante,
  boleta) se mantienen en español si así se usan en el dominio real.
- Idempotencia obligatoria en pagos y emisión de comprobantes (UUID de
  transacción antes de tocar la base).
- Toda respuesta de error sigue el mismo formato JSON (definir en shared/).

## Orden de desarrollo (no saltar fases)
1. Auth y roles
2. Catálogo e inventario
3. Carrito y checkout online
4. POS con lector QR
5. Facturación electrónica
6. Dashboard de analítica
