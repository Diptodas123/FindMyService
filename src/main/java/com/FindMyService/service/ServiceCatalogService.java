package com.FindMyService.service;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceCatalogService {

    public List<com.FindMyService.model.Service> getAllServices() {
        return Collections.emptyList();
    }

    public Optional<com.FindMyService.model.Service> getServiceById(String id) {
        return Optional.empty();
    }

    public com.FindMyService.model.Service createService(com.FindMyService.model.Service service) {
        return service;
    }

    public Optional<com.FindMyService.model.Service> updateService(String id, com.FindMyService.model.Service service) {
        return Optional.empty();
    }

    public boolean deleteService(String id) {
        return false;
    }
}