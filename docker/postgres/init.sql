-- ====================
-- SYNCRIA - PostgreSQL Init
-- ====================
-- Este script se ejecuta al iniciar el contenedor por primera vez

-- Crear extensiones útiles
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Verificar que la BD está lista
SELECT 'Syncria database initialized successfully!' AS status;
