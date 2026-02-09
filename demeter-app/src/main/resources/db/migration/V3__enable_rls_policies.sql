-- =============================================
-- V3: Enable Row Level Security policies
-- =============================================

-- Function to get current tenant from session variable
CREATE OR REPLACE FUNCTION current_tenant_id() RETURNS VARCHAR AS $$
BEGIN
    RETURN current_setting('app.current_tenant', true);
END;
$$ LANGUAGE plpgsql STABLE;

-- Enable RLS and create policies for each table
-- Products domain
ALTER TABLE product_categories ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_product_categories ON product_categories
    USING (tenant_id = current_tenant_id());

ALTER TABLE product_families ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_product_families ON product_families
    USING (tenant_id = current_tenant_id());

ALTER TABLE products ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_products ON products
    USING (tenant_id = current_tenant_id());

ALTER TABLE product_sizes ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_product_sizes ON product_sizes
    USING (tenant_id = current_tenant_id());

ALTER TABLE product_sample_images ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_product_sample_images ON product_sample_images
    USING (tenant_id = current_tenant_id());

ALTER TABLE density_parameters ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_density_parameters ON density_parameters
    USING (tenant_id = current_tenant_id());

-- Ubicaciones domain
ALTER TABLE warehouses ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_warehouses ON warehouses
    USING (tenant_id = current_tenant_id());

ALTER TABLE storage_areas ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_storage_areas ON storage_areas
    USING (tenant_id = current_tenant_id());

ALTER TABLE storage_locations ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_storage_locations ON storage_locations
    USING (tenant_id = current_tenant_id());

ALTER TABLE storage_bin_types ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_storage_bin_types ON storage_bin_types
    USING (tenant_id = current_tenant_id());

ALTER TABLE storage_bins ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_storage_bins ON storage_bins
    USING (tenant_id = current_tenant_id());

-- Empaquetado domain
ALTER TABLE packaging_types ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_packaging_types ON packaging_types
    USING (tenant_id = current_tenant_id());

ALTER TABLE packaging_materials ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_packaging_materials ON packaging_materials
    USING (tenant_id = current_tenant_id());

ALTER TABLE packaging_colors ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_packaging_colors ON packaging_colors
    USING (tenant_id = current_tenant_id());

ALTER TABLE packaging_catalogs ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_packaging_catalogs ON packaging_catalogs
    USING (tenant_id = current_tenant_id());

-- Usuarios domain
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_users ON users
    USING (tenant_id = current_tenant_id());

-- Inventario domain
ALTER TABLE stock_batches ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_stock_batches ON stock_batches
    USING (tenant_id = current_tenant_id());

ALTER TABLE stock_movements ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_stock_movements ON stock_movements
    USING (tenant_id = current_tenant_id());

-- stock_batch_movements doesn't have tenant_id directly
-- it's a junction table — access controlled through stock_batches and stock_movements

-- Ventas domain
ALTER TABLE sales ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_sales ON sales
    USING (tenant_id = current_tenant_id());

ALTER TABLE sale_items ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_sale_items ON sale_items
    USING (tenant_id = current_tenant_id());

-- Costos domain
ALTER TABLE costs ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_costs ON costs
    USING (tenant_id = current_tenant_id());

-- Precios domain
ALTER TABLE price_lists ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_price_lists ON price_lists
    USING (tenant_id = current_tenant_id());

ALTER TABLE price_entries ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_price_entries ON price_entries
    USING (tenant_id = current_tenant_id());

-- Fotos domain (DLC)
ALTER TABLE photo_processing_sessions ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_photo_sessions ON photo_processing_sessions
    USING (tenant_id = current_tenant_id());

ALTER TABLE images ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_images ON images
    USING (tenant_id = current_tenant_id());

ALTER TABLE detections ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_detections ON detections
    USING (tenant_id = current_tenant_id());

ALTER TABLE classifications ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_classifications ON classifications
    USING (tenant_id = current_tenant_id());

ALTER TABLE estimations ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_estimations ON estimations
    USING (tenant_id = current_tenant_id());

-- Chatbot domain (DLC)
ALTER TABLE chat_sessions ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_chat_sessions ON chat_sessions
    USING (tenant_id = current_tenant_id());

ALTER TABLE chat_messages ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_chat_messages ON chat_messages
    USING (tenant_id = current_tenant_id());

ALTER TABLE chat_tool_executions ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_chat_tool_executions ON chat_tool_executions
    USING (tenant_id = current_tenant_id());

-- IMPORTANT: The database user used by the application should NOT be a superuser
-- as superusers bypass RLS. Create a dedicated app user:
-- CREATE USER demeter_app WITH PASSWORD 'xxx';
-- GRANT ALL ON ALL TABLES IN SCHEMA public TO demeter_app;
-- ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO demeter_app;
