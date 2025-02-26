package com.unit.oauth2Service.repository;

import com.unit.oauth2Service.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RolRepository extends JpaRepository<Role, UUID> {

    Role findByRoleName(String name);
}
