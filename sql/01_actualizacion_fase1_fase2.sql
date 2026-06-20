-- =====================================================================
-- POS_SEMILLAS - Script de actualización de base de datos
-- Aplicar SOBRE tu base db_semillas ya existente (Dump20260620.sql)
-- =====================================================================

USE db_semillas;

-- ---------------------------------------------------------------------
-- 1) Auditoría: agregar trazabilidad completa (antes/después + equipo)
-- ---------------------------------------------------------------------
ALTER TABLE auditoria
  ADD COLUMN datos_anteriores TEXT NULL AFTER descripcion,
  ADD COLUMN datos_nuevos     TEXT NULL AFTER datos_anteriores,
  ADD COLUMN equipo           VARCHAR(100) NULL AFTER ip;

-- ---------------------------------------------------------------------
-- 2) Productos: costeo promedio y stock máximo
-- ---------------------------------------------------------------------
ALTER TABLE productos
  ADD COLUMN costo_promedio DECIMAL(10,2) NOT NULL DEFAULT 0.00 AFTER precio_compra,
  ADD COLUMN stock_maximo   INT NOT NULL DEFAULT 0 AFTER stock_minimo;

-- ---------------------------------------------------------------------
-- 3) Tabla nueva: sesiones (control de sesión única por usuario)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sesiones (
  `id_sesion`        INT NOT NULL AUTO_INCREMENT,
  `id_usuario`       INT NOT NULL,
  `token_sesion`     VARCHAR(255) NOT NULL,
  `equipo`           VARCHAR(100) DEFAULT NULL,
  `ip`               VARCHAR(45) DEFAULT NULL,
  `fecha_inicio`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ultima_actividad` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_cierre`     DATETIME DEFAULT NULL,
  `activa`           TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id_sesion`),
  UNIQUE KEY `uq_sesiones_token` (`token_sesion`),
  KEY `idx_sesiones_usuario_activa` (`id_usuario`,`activa`),
  CONSTRAINT `fk_sesion_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 4) Índices de optimización
-- ---------------------------------------------------------------------
CREATE INDEX idx_auditoria_modulo ON auditoria(modulo);
CREATE INDEX idx_productos_stock ON productos(stock_actual, stock_minimo);

-- ---------------------------------------------------------------------
-- NOTA: el usuario 'admin' del dump original tiene un password_hash
-- placeholder ($2a$12$PLACEHOLDER_HASH) y NO sirve para login real.
-- Genera un hash BCrypt válido (puedes usar PasswordUtil.hashPassword
-- desde una clase de prueba) y actualízalo, por ejemplo:
--
-- UPDATE usuarios SET password_hash = 'TU_HASH_BCRYPT_AQUI'
-- WHERE username = 'admin';
-- ---------------------------------------------------------------------
