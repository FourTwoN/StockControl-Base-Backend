package com.fortytwo.demeter.fotos.storage;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import io.quarkus.arc.profile.UnlessBuildProfile;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Google Cloud Storage implementation for production.
 *
 * <p>Implements the ImageStorageService interface using GCS SDK.
 * Can be swapped for S3StorageService or other implementations.
 *
 * <p>This implementation is active in all profiles except "dev".
 *
 * <p>Required configuration (provider-agnostic names):
 * <ul>
 *   <li>{@code demeter.storage.bucket} - Bucket/container name
 *   <li>{@code demeter.storage.project-id} - Cloud project ID
 *   <li>{@code GOOGLE_APPLICATION_CREDENTIALS} - Path to service account JSON (or use workload identity)
 * </ul>
 */
@ApplicationScoped
@UnlessBuildProfile("dev")
public class GcsStorageService implements ImageStorageService {

    private static final Logger log = Logger.getLogger(GcsStorageService.class);

    @ConfigProperty(name = "demeter.storage.bucket")
    String bucketName;

    @ConfigProperty(name = "demeter.storage.project-id")
    String projectId;

    @ConfigProperty(name = "demeter.storage.base-path", defaultValue = "images")
    String basePath;

    private Storage storage;

    @PostConstruct
    void init() {
        try {
            storage = StorageOptions.newBuilder()
                    .setProjectId(projectId)
                    .setCredentials(GoogleCredentials.getApplicationDefault())
                    .build()
                    .getService();

            log.infof("Cloud storage initialized: bucket=%s, project=%s, provider=gcs", bucketName, projectId);

            // Verify bucket exists
            Bucket bucket = storage.get(bucketName);
            if (bucket == null) {
                log.warnf("Bucket does not exist: %s (will be created on first upload)", bucketName);
            }

        } catch (IOException e) {
            log.errorf("Failed to initialize cloud storage: %s", e.getMessage());
            throw new RuntimeException("Cannot initialize cloud storage", e);
        }
    }

    @Override
    public String upload(byte[] data, String path, String contentType) {
        String fullPath = basePath + "/" + path;

        BlobId blobId = BlobId.of(bucketName, fullPath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(contentType)
                .build();

        try {
            Blob blob = storage.create(blobInfo, data);
            String storageUrl = String.format("gs://%s/%s", bucketName, fullPath);
            log.infof("Uploaded to cloud storage: %s (%d bytes)", storageUrl, data.length);
            return storageUrl;

        } catch (StorageException e) {
            log.errorf("Failed to upload to cloud storage: %s - %s", fullPath, e.getMessage());
            throw new RuntimeException("Failed to upload to cloud storage: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateReadUrl(String storagePath, Duration expiration) {
        String blobPath = extractBlobPath(storagePath);
        BlobId blobId = BlobId.of(bucketName, blobPath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

        try {
            URL signedUrl = storage.signUrl(
                    blobInfo,
                    expiration.toMinutes(),
                    TimeUnit.MINUTES,
                    Storage.SignUrlOption.withV4Signature()
            );

            log.debugf("Generated signed read URL for: %s (expires in %d min)",
                    storagePath, expiration.toMinutes());

            return signedUrl.toString();

        } catch (StorageException e) {
            log.errorf("Failed to generate signed URL: %s - %s", storagePath, e.getMessage());
            throw new RuntimeException("Failed to generate signed URL: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateUploadUrl(String path, String contentType, Duration expiration) {
        String fullPath = basePath + "/" + path;

        BlobId blobId = BlobId.of(bucketName, fullPath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(contentType)
                .build();

        try {
            URL signedUrl = storage.signUrl(
                    blobInfo,
                    expiration.toMinutes(),
                    TimeUnit.MINUTES,
                    Storage.SignUrlOption.withV4Signature(),
                    Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
                    Storage.SignUrlOption.withContentType()
            );

            log.debugf("Generated signed upload URL for: %s (expires in %d min)",
                    fullPath, expiration.toMinutes());

            return signedUrl.toString();

        } catch (StorageException e) {
            log.errorf("Failed to generate upload URL: %s - %s", fullPath, e.getMessage());
            throw new RuntimeException("Failed to generate upload URL: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<byte[]> download(String storagePath) {
        String blobPath = extractBlobPath(storagePath);
        BlobId blobId = BlobId.of(bucketName, blobPath);

        try {
            Blob blob = storage.get(blobId);
            if (blob == null || !blob.exists()) {
                log.warnf("Blob not found in cloud storage: %s", storagePath);
                return Optional.empty();
            }

            byte[] data = blob.getContent();
            log.debugf("Downloaded from cloud storage: %s (%d bytes)", storagePath, data.length);
            return Optional.of(data);

        } catch (StorageException e) {
            log.errorf("Failed to download from cloud storage: %s - %s", storagePath, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public boolean delete(String storagePath) {
        String blobPath = extractBlobPath(storagePath);
        BlobId blobId = BlobId.of(bucketName, blobPath);

        try {
            boolean deleted = storage.delete(blobId);
            if (deleted) {
                log.infof("Deleted from cloud storage: %s", storagePath);
            }
            return deleted;

        } catch (StorageException e) {
            log.errorf("Failed to delete from cloud storage: %s - %s", storagePath, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean exists(String storagePath) {
        String blobPath = extractBlobPath(storagePath);
        BlobId blobId = BlobId.of(bucketName, blobPath);
        Blob blob = storage.get(blobId);
        return blob != null && blob.exists();
    }

    @Override
    public String getProviderName() {
        return "gcs";
    }

    /**
     * Extract the blob path from a cloud storage URL or relative path.
     *
     * @param storagePath Either gs://bucket/path or just path
     * @return The blob path (without gs://bucket/ prefix)
     */
    private String extractBlobPath(String storagePath) {
        if (storagePath.startsWith("gs://")) {
            // Extract path from gs://bucket/path format
            String withoutPrefix = storagePath.substring(5); // Remove "gs://"
            int slashIndex = withoutPrefix.indexOf('/');
            if (slashIndex > 0) {
                return withoutPrefix.substring(slashIndex + 1);
            }
            throw new IllegalArgumentException("Invalid cloud storage URL: " + storagePath);
        }
        return storagePath;
    }
}
