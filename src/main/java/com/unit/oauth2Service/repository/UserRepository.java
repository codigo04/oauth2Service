package com.unit.oauth2Service.repository;

import com.unit.oauth2Service.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,UUID > {

    boolean existsByUsername(String username);
    UserEntity findByUsername(String username);
}
