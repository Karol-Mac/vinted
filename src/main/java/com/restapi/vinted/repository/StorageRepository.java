package com.restapi.vinted.repository;

import com.restapi.vinted.entity.Storage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageRepository extends JpaRepository<Storage, Long> {

}
