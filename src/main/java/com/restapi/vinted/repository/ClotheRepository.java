package com.restapi.vinted.repository;

import com.restapi.vinted.entity.Clothe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClotheRepository extends JpaRepository<Clothe, Long> {
    Page<Clothe> findByCategoryId(long categoryId, Pageable pageable);
    List<Clothe> findByUserId(long userId);
    Page<Clothe> findByUserId(long userId, Pageable pageable);
}
