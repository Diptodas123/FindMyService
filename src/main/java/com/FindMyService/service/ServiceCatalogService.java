package com.FindMyService.service;

import com.FindMyService.repository.ServiceCatalogRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceCatalogService {

    private final ServiceCatalogRepository serviceCatalogRepository;

    public ServiceCatalogService(ServiceCatalogRepository serviceCatalogRepository) {
        this.serviceCatalogRepository = serviceCatalogRepository;
    }

    public List<com.FindMyService.model.Service> getAllServices() {
        return serviceCatalogRepository.findAll();
    }

    public Optional<com.FindMyService.model.Service> getServiceById(Long serviceId) {
        return serviceCatalogRepository.findById(serviceId);
    }

    public com.FindMyService.model.Service createService(com.FindMyService.model.Service service) {
        return serviceCatalogRepository.save(service);
    }

    public Optional<com.FindMyService.model.Service> updateService(Long serviceId, com.FindMyService.model.Service service) {
        com.FindMyService.model.Service existingService = serviceCatalogRepository.findById(serviceId).orElse(null);
        if (existingService == null) {
            return Optional.empty();
        }
        service.setServiceId(serviceId);
        com.FindMyService.model.Service updatedService = serviceCatalogRepository.save(service);
        return Optional.of(updatedService);
    }

    public boolean deleteService(Long serviceId) {
        return serviceCatalogRepository.findById(serviceId).map(service -> {
            serviceCatalogRepository.delete(service);
            return true;
        }).orElse(false);
    }
}
