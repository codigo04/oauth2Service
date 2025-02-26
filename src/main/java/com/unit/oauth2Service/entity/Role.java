package com.unit.oauth2Service.entity;


import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "role_id")
    private UUID id;

    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;

    @Column(name = "role_description", nullable = false, unique = true)
    private String roleDescription;

    public Role() {
    }

    public Role(UUID id, String roleName, String roleDescription) {
        this.id = id;
        this.roleName = roleName;
        this.roleDescription = roleDescription;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }
}
