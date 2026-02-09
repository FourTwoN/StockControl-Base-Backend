package com.fortytwo.demeter.empaquetado.repository;

import com.fortytwo.demeter.empaquetado.model.PackagingColor;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class PackagingColorRepository implements PanacheRepositoryBase<PackagingColor, UUID> {}
