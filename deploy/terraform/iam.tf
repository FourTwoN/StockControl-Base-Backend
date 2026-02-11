# =============================================================================
# Service Account for Cloud Run
# =============================================================================

resource "google_service_account" "cloudrun" {
  account_id   = "demeter-cloudrun-${var.environment}"
  display_name = "Demeter Cloud Run Service Account (${var.environment})"
}

# =============================================================================
# IAM — Secret Manager Access
# =============================================================================

resource "google_secret_manager_secret_iam_member" "db_url_access" {
  secret_id = google_secret_manager_secret.db_url.secret_id
  role      = "roles/secretmanager.secretAccessor"
  member    = "serviceAccount:${google_service_account.cloudrun.email}"
}

resource "google_secret_manager_secret_iam_member" "db_user_access" {
  secret_id = google_secret_manager_secret.db_user.secret_id
  role      = "roles/secretmanager.secretAccessor"
  member    = "serviceAccount:${google_service_account.cloudrun.email}"
}

resource "google_secret_manager_secret_iam_member" "db_password_access" {
  secret_id = google_secret_manager_secret.db_password.secret_id
  role      = "roles/secretmanager.secretAccessor"
  member    = "serviceAccount:${google_service_account.cloudrun.email}"
}

resource "google_secret_manager_secret_iam_member" "oidc_auth_server_url_access" {
  secret_id = google_secret_manager_secret.oidc_auth_server_url.secret_id
  role      = "roles/secretmanager.secretAccessor"
  member    = "serviceAccount:${google_service_account.cloudrun.email}"
}

resource "google_secret_manager_secret_iam_member" "oidc_client_id_access" {
  secret_id = google_secret_manager_secret.oidc_client_id.secret_id
  role      = "roles/secretmanager.secretAccessor"
  member    = "serviceAccount:${google_service_account.cloudrun.email}"
}

resource "google_secret_manager_secret_iam_member" "oidc_issuer_access" {
  secret_id = google_secret_manager_secret.oidc_issuer.secret_id
  role      = "roles/secretmanager.secretAccessor"
  member    = "serviceAccount:${google_service_account.cloudrun.email}"
}

# =============================================================================
# IAM — Cloud SQL Client Access
# =============================================================================

resource "google_project_iam_member" "cloudrun_cloudsql" {
  project = var.project_id
  role    = "roles/cloudsql.client"
  member  = "serviceAccount:${google_service_account.cloudrun.email}"
}

# =============================================================================
# Service Account for Cloud Tasks Invoker
# =============================================================================

resource "google_service_account" "cloudtasks_invoker" {
  account_id   = "demeter-cloudtasks-${var.environment}"
  display_name = "Demeter Cloud Tasks Invoker (${var.environment})"
  description  = "Service account for creating Cloud Tasks to invoke ML Worker"
}

# Grant Cloud Tasks Enqueuer role to backend service account
resource "google_project_iam_member" "cloudrun_cloudtasks_enqueuer" {
  project = var.project_id
  role    = "roles/cloudtasks.enqueuer"
  member  = "serviceAccount:${google_service_account.cloudrun.email}"
}

# Grant the cloudtasks invoker permission to use service account for OIDC tokens
resource "google_service_account_iam_member" "cloudtasks_invoker_token" {
  service_account_id = google_service_account.cloudtasks_invoker.name
  role               = "roles/iam.serviceAccountTokenCreator"
  member             = "serviceAccount:${google_service_account.cloudrun.email}"
}
