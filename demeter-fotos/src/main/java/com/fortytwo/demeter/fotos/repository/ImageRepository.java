package com.fortytwo.demeter.fotos.repository;

import com.fortytwo.demeter.fotos.model.Image;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ImageRepository implements PanacheRepositoryBase<Image, UUID> {

    public List<Image> findBySessionId(UUID sessionId) {
        return find("session.id", sessionId).list();
    }
}
