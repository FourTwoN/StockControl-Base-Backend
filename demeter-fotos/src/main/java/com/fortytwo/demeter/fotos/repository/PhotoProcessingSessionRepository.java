package com.fortytwo.demeter.fotos.repository;

import com.fortytwo.demeter.fotos.model.PhotoProcessingSession;
import com.fortytwo.demeter.fotos.model.ProcessingStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class PhotoProcessingSessionRepository implements PanacheRepositoryBase<PhotoProcessingSession, UUID> {

    public List<PhotoProcessingSession> findByStatus(ProcessingStatus status) {
        return find("status", status).list();
    }

    public List<PhotoProcessingSession> findByProductId(UUID productId) {
        return find("productId", productId).list();
    }
}
