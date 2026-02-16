# =============================================================================
# Cloud SQL — PostgreSQL 17
# =============================================================================

resource "google_sql_database_instance" "demeter" {
  name             = "demeter-db-${var.environment}"
  database_version = "POSTGRES_17"
  region           = var.region

  settings {
    tier              = var.db_tier
    availability_type = "ZONAL"  # Single zone, no HA
    disk_size         = var.db_disk_size
    disk_type         = "PD_SSD"
    disk_autoresize   = true

    database_flags {
      name  = "max_connections"
      value = tostring(var.db_max_connections)
    }

    backup_configuration {
      enabled    = true
      start_time = "03:00"
    }

    ip_configuration {
      ipv4_enabled    = false
      private_network = google_compute_network.vpc.id
    }
  }

  deletion_protection = false  # Allow deletion for dev

  depends_on = [google_service_networking_connection.private_vpc]
}

# =============================================================================
# Database
# =============================================================================

resource "google_sql_database" "demeter" {
  name     = "demeter"
  instance = google_sql_database_instance.demeter.name
}

# =============================================================================
# Database User
# =============================================================================

resource "google_sql_user" "demeter_app" {
  name     = "demeter_app"
  instance = google_sql_database_instance.demeter.name
  password = var.db_password
}
