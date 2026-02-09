-- =============================================
-- V2: Additional tenant_id composite indexes
-- =============================================

-- These are additional composite indexes for common query patterns
CREATE INDEX idx_product_categories_tenant ON product_categories(tenant_id);
CREATE INDEX idx_product_families_tenant ON product_families(tenant_id);
CREATE INDEX idx_product_sizes_tenant ON product_sizes(tenant_id);
CREATE INDEX idx_product_sample_images_tenant ON product_sample_images(tenant_id);
CREATE INDEX idx_density_parameters_tenant ON density_parameters(tenant_id);
CREATE INDEX idx_storage_areas_tenant ON storage_areas(tenant_id);
CREATE INDEX idx_storage_locations_tenant ON storage_locations(tenant_id);
CREATE INDEX idx_storage_bins_tenant ON storage_bins(tenant_id);
CREATE INDEX idx_storage_bin_types_tenant ON storage_bin_types(tenant_id);
CREATE INDEX idx_packaging_types_tenant ON packaging_types(tenant_id);
CREATE INDEX idx_packaging_materials_tenant ON packaging_materials(tenant_id);
CREATE INDEX idx_packaging_colors_tenant ON packaging_colors(tenant_id);
CREATE INDEX idx_packaging_catalogs_tenant ON packaging_catalogs(tenant_id);
CREATE INDEX idx_users_tenant ON users(tenant_id);
CREATE INDEX idx_stock_batch_movements_batch ON stock_batch_movements(batch_id);
CREATE INDEX idx_costs_tenant ON costs(tenant_id);
CREATE INDEX idx_price_lists_tenant ON price_lists(tenant_id);
CREATE INDEX idx_price_entries_tenant ON price_entries(tenant_id);
CREATE INDEX idx_photo_sessions_tenant ON photo_processing_sessions(tenant_id);
CREATE INDEX idx_images_tenant ON images(tenant_id);
CREATE INDEX idx_detections_tenant ON detections(tenant_id);
CREATE INDEX idx_classifications_tenant ON classifications(tenant_id);
CREATE INDEX idx_estimations_tenant ON estimations(tenant_id);
CREATE INDEX idx_chat_sessions_tenant ON chat_sessions(tenant_id);
CREATE INDEX idx_chat_messages_tenant ON chat_messages(tenant_id);
CREATE INDEX idx_chat_tool_executions_tenant ON chat_tool_executions(tenant_id);
