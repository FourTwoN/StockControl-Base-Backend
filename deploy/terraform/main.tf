terraform {
  required_version = ">= 1.5"
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 5.0"
    }
  }
  backend "gcs" {
    bucket = "demeter-terraform-state"
    prefix = "backend"
  }
}

variable "project_id" {
  type        = string
  description = "GCP Project ID"
}

variable "region" {
  type        = string
  default     = "us-central1"
  description = "GCP Region"
}

variable "db_password" {
  type        = string
  sensitive   = true
  description = "Database password"
}

provider "google" {
  project = var.project_id
  region  = var.region
}

# =============================================
# Cloud SQL — PostgreSQL 17
# =============================================
resource "google_sql_database_instance" "demeter" {
  name             = "demeter-db"
  database_version = "POSTGRES_17"
  region           = var.region

  settings {
    tier              = "db-custom-2-4096"
    availability_type = "REGIONAL"
    disk_size         = 20
    disk_type         = "PD_SSD"

    database_flags {
      name  = "max_connections"
      value = "200"
    }

    backup_configuration {
      enabled                        = true
      point_in_time_recovery_enabled = true
      start_time                     = "03:00"
    }

    ip_configuration {
      ipv4_enabled    = false
      private_network = google_compute_network.vpc.id
    }
  }

  deletion_protection = true
}

resource "google_sql_database" "demeter" {
  name     = "demeter"
  instance = google_sql_database_instance.demeter.name
}

resource "google_sql_user" "demeter_app" {
  name     = "demeter_app"
  instance = google_sql_database_instance.demeter.name
  password = var.db_password
}

# =============================================
# VPC Network
# =============================================
resource "google_compute_network" "vpc" {
  name                    = "demeter-vpc"
  auto_create_subnetworks = false
}

resource "google_compute_subnetwork" "subnet" {
  name          = "demeter-subnet"
  ip_cidr_range = "10.0.0.0/24"
  region        = var.region
  network       = google_compute_network.vpc.id
}

resource "google_compute_global_address" "private_ip" {
  name          = "demeter-private-ip"
  purpose       = "VPC_PEERING"
  address_type  = "INTERNAL"
  prefix_length = 16
  network       = google_compute_network.vpc.id
}

resource "google_service_networking_connection" "private_vpc" {
  network                 = google_compute_network.vpc.id
  service                 = "servicenetworking.googleapis.com"
  reserved_peering_ranges = [google_compute_global_address.private_ip.name]
}

# =============================================
# Artifact Registry
# =============================================
resource "google_artifact_registry_repository" "demeter" {
  location      = var.region
  repository_id = "demeter"
  format        = "DOCKER"
}

# =============================================
# Cloud Run
# =============================================
resource "google_cloud_run_v2_service" "backend" {
  name     = "demeter-backend"
  location = var.region

  template {
    scaling {
      min_instance_count = 0
      max_instance_count = 10
    }

    volumes {
      name = "cloudsql"
      cloud_sql_instance {
        instances = [google_sql_database_instance.demeter.connection_name]
      }
    }

    containers {
      image = "${var.region}-docker.pkg.dev/${var.project_id}/demeter/backend:latest"

      ports {
        container_port = 8080
      }

      resources {
        limits = {
          cpu    = "2"
          memory = "1Gi"
        }
        cpu_idle          = false
        startup_cpu_boost = true
      }

      env {
        name  = "QUARKUS_PROFILE"
        value = "prod"
      }

      env {
        name = "DB_URL"
        value_source {
          secret_key_ref {
            secret  = "demeter-db-url"
            version = "latest"
          }
        }
      }

      env {
        name = "DB_USER"
        value_source {
          secret_key_ref {
            secret  = "demeter-db-user"
            version = "latest"
          }
        }
      }

      env {
        name = "DB_PASSWORD"
        value_source {
          secret_key_ref {
            secret  = "demeter-db-password"
            version = "latest"
          }
        }
      }

      volume_mounts {
        name       = "cloudsql"
        mount_path = "/cloudsql"
      }

      startup_probe {
        http_get {
          path = "/q/health/started"
          port = 8080
        }
        initial_delay_seconds = 5
        period_seconds        = 3
        failure_threshold     = 10
      }

      liveness_probe {
        http_get {
          path = "/q/health/live"
          port = 8080
        }
        period_seconds = 30
      }
    }
  }

  traffic {
    type    = "TRAFFIC_TARGET_ALLOCATION_TYPE_LATEST"
    percent = 100
  }
}

# =============================================
# Outputs
# =============================================
output "cloud_run_url" {
  value = google_cloud_run_v2_service.backend.uri
}

output "db_connection_name" {
  value = google_sql_database_instance.demeter.connection_name
}
