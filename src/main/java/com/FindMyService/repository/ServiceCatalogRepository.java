package com.FindMyService.repository;

import com.FindMyService.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceCatalogRepository extends JpaRepository<Service, String> {
}
