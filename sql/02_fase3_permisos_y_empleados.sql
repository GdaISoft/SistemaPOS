-- =====================================================================
-- POS_SEMILLAS - Fase 3: Usuarios, Roles y Permisos
-- Aplicar SOBRE db_semillas, después de 01_actualizacion_fase1_fase2.sql
-- =====================================================================

USE db_semillas;

-- ---------------------------------------------------------------------
-- 1) Asignar permisos a cada rol (rol_permiso estaba vacía)
--    ADMIN = acceso total a todos los módulos definidos hasta ahora.
-- ---------------------------------------------------------------------
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 1, id_permiso FROM permisos
ON DUPLICATE KEY UPDATE id_rol = id_rol;

-- GERENTE: solo consulta (reportes, ventas, inventario, compras)
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 2, id_permiso FROM permisos
WHERE (modulo, accion) IN (
    ('REPORTES','READ'),
    ('VENTAS','READ'),
    ('INVENTARIO','READ'),
    ('COMPRAS','READ')
)
ON DUPLICATE KEY UPDATE id_rol = id_rol;

-- CAJERO: ventas y caja
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 3, id_permiso FROM permisos
WHERE (modulo, accion) IN (
    ('VENTAS','READ'),
    ('VENTAS','CREATE'),
    ('CAJA','CREATE'),
    ('CAJA','UPDATE')
)
ON DUPLICATE KEY UPDATE id_rol = id_rol;

-- ALMACENISTA: inventario, compras y productos
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 4, id_permiso FROM permisos
WHERE (modulo, accion) IN (
    ('INVENTARIO','READ'),
    ('INVENTARIO','UPDATE'),
    ('COMPRAS','READ'),
    ('COMPRAS','CREATE'),
    ('PRODUCTOS','READ'),
    ('PRODUCTOS','CREATE'),
    ('PRODUCTOS','UPDATE')
)
ON DUPLICATE KEY UPDATE id_rol = id_rol;

-- ---------------------------------------------------------------------
-- 2) Datos de empleado para el usuario admin (empleados estaba vacía,
--    y frmUsuarios necesita nombre/apellido para mostrarlos)
-- ---------------------------------------------------------------------
INSERT INTO empleados (id_usuario, nombre, apellido, puesto, fecha_ingreso)
SELECT id_usuario, 'Administrador', 'General', 'Administrador del sistema', CURDATE()
FROM usuarios
WHERE username = 'admin'
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

-- ---------------------------------------------------------------------
-- NOTA: si ya habías creado usuarios de prueba adicionales, agrégales
-- también su fila en empleados, o el nombre se mostrará vacío en
-- frmUsuarios (no falla, simplemente queda en blanco por el LEFT JOIN).
-- ---------------------------------------------------------------------
