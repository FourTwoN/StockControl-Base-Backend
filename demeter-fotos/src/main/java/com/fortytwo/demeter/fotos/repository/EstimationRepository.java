package com.fortytwo.demeter.fotos.repository;

import com.fortytwo.demeter.fotos.model.Estimation;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class EstimationRepository implements PanacheRepositoryBase<Estimation, UUID> {

    public List<Estimation> findBySessionId(UUID sessionId) {
        return find("session.id", sessionId).list();
    }
}
