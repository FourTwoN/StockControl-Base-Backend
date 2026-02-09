package com.fortytwo.demeter.empaquetado.repository;

import com.fortytwo.demeter.empaquetado.model.PackagingMaterial;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class PackagingMaterialRepository implements PanacheRepositoryBase<PackagingMaterial, UUID> {}
