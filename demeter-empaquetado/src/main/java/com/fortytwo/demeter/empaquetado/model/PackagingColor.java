package com.fortytwo.demeter.empaquetado.model;

import com.fortytwo.demeter.common.model.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "packaging_colors")
public class PackagingColor extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "hex_code", nullable = false, length = 7)
    private String hexCode;

    public String getName() { return name; }
    public String getHexCode() { return hexCode; }

    public void setName(String name) { this.name = name; }
    public void setHexCode(String hexCode) { this.hexCode = hexCode; }
}
