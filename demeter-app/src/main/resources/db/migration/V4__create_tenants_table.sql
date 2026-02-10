-- =============================================
-- V4: Tenants master table (NO tenant_id, NO RLS)
-- This is the tenant registry itself, not tenant-scoped data.
-- =============================================

CREATE TABLE tenants (
    id              VARCHAR(64)  PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    industry        VARCHAR(100) NOT NULL,
    theme           JSONB        NOT NULL DEFAULT '{}'::jsonb,
    enabled_modules JSONB        NOT NULL DEFAULT '[]'::jsonb,
    settings        JSONB        NOT NULL DEFAULT '{}'::jsonb,
    active          BOOLEAN      NOT NULL DEFAULT true,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP WITH TIME ZONE         DEFAULT now()
);
