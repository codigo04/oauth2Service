package com.unit.oauth2Service.repository;

import com.unit.oauth2Service.entity.ProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface productoRepository extends JpaRepository<ProductoEntity, Long> {
}
