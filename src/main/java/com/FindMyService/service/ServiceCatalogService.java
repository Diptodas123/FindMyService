package com.FindMyService.service;

import com.FindMyService.model.Provider;
import com.FindMyService.model.ServiceCatalog;
import com.FindMyService.repository.ProviderRepository;
import com.FindMyService.repository.ServiceCatalogRepository;
import com.FindMyService.utils.ErrorResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceCatalogService {

    private final ServiceCatalogRepository serviceCatalogRepository;
    private final ProviderRepository providerRepository;

    public ServiceCatalogService(ServiceCatalogRepository serviceCatalogRepository,
                                 ProviderRepository providerRepository) {
        this.serviceCatalogRepository = serviceCatalogRepository;
        this.providerRepository = providerRepository;
    }

    public List<ServiceCatalog> getAllServices() {
        return serviceCatalogRepository.findAll();
    }

    public Optional<ServiceCatalog> getServiceById(Long serviceId) {
        return serviceCatalogRepository.findById(serviceId);
    }

    @Transactional
    public ResponseEntity<?> createService(ServiceCatalog service) {
        Optional<Provider> provider = providerRepository.findById(service.getProviderId().getProviderId());
        if (provider.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponseBuilder.build(HttpStatus.BAD_REQUEST, "Provider from payload not found"));
        }

        try {
            ServiceCatalog saved = serviceCatalogRepository.save(service);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponseBuilder.serverError("Failed to create service: " + e.getMessage()));
        }
    }

    @Transactional
    public ResponseEntity<?> updateService(Long serviceId, ServiceCatalog service) {
        Optional<ServiceCatalog> existingService = serviceCatalogRepository.findById(serviceId);
        if (existingService.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponseBuilder.build(HttpStatus.NOT_FOUND, "Service not found"));
        }

        Optional<Provider> provider = providerRepository.findById(service.getProviderId().getProviderId());
        if (provider.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponseBuilder.build(HttpStatus.BAD_REQUEST, "Provider from payload not found"));
        }

        try {
            service.setServiceId(serviceId);
            ServiceCatalog updatedService = serviceCatalogRepository.save(service);
            return ResponseEntity.ok(updatedService);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponseBuilder.serverError("Failed to update service: " + e.getMessage()));
        }
    }

    @Transactional
    public boolean deleteService(Long serviceId) {
        return serviceCatalogRepository.findById(serviceId).map(service -> {
            serviceCatalogRepository.delete(service);
            return true;
        }).orElse(false);
    }
}
