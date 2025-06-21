package com.flipdeal.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flipdeal.demo.entity.CartEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRespository extends JpaRepository<CartEntity, UUID> {

    Optional<CartEntity> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}
