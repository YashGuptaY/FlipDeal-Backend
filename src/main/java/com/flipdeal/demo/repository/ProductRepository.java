package com.flipdeal.demo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.flipdeal.demo.entity.ProductEntity;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
	
    @Query("SELECT p FROM ProductEntity p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.category) LIKE LOWER(CONCAT('%', :keyword, '%'))")
     List<ProductEntity> searchByKeyword(String keyword);

     List<ProductEntity> findByCategoryIgnoreCase(String category);

     @Query("SELECT p FROM ProductEntity p WHERE " +
            "LOWER(p.category) LIKE LOWER(CONCAT('%', :category, '%')) AND " +
            "(" +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
            ")")
     List<ProductEntity> searchByKeywordAndCategory(String keyword, String category);


}
