package com.fortytwo.demeter.usuarios.repository;

import com.fortytwo.demeter.usuarios.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<User, UUID> {
    public Optional<User> findByExternalId(String externalId) {
        return find("externalId", externalId).firstResultOptional();
    }
    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }
    public List<User> findActive() {
        return find("active", true).list();
    }
}
