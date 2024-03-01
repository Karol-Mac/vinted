package com.restapi.vinted.repository;

import com.restapi.vinted.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {}