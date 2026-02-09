package com.fortytwo.demeter.fotos.repository;

import com.fortytwo.demeter.fotos.model.S3Image;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class S3ImageRepository implements PanacheRepositoryBase<S3Image, UUID> {

    public List<S3Image> findBySessionId(UUID sessionId) {
        return find("session.id", sessionId).list();
    }
}
