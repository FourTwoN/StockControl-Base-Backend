package com.fortytwo.demeter.ubicaciones.repository;

import com.fortytwo.demeter.ubicaciones.model.StorageBinType;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class StorageBinTypeRepository implements PanacheRepositoryBase<StorageBinType, UUID> {}
