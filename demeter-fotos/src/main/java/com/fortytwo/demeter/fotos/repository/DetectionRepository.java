package com.fortytwo.demeter.fotos.repository;

import com.fortytwo.demeter.fotos.model.Detection;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class DetectionRepository implements PanacheRepositoryBase<Detection, UUID> {

    public List<Detection> findByImageId(UUID imageId) {
        return find("image.id", imageId).list();
    }
}
