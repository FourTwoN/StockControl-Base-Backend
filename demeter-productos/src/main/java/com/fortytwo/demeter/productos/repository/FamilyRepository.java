package com.fortytwo.demeter.productos.repository;

import com.fortytwo.demeter.productos.model.ProductFamily;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class FamilyRepository implements PanacheRepositoryBase<ProductFamily, UUID> {
}
