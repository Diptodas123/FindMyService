package com.FindMyService.service;

import com.FindMyService.model.Provider;
import com.FindMyService.repository.ProviderRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProviderService {

    private final ProviderRepository providerRepository;

    public ProviderService(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    public List<Provider> getAllProviders() {
        return providerRepository.findAll();
    }

    public Optional<Provider> getProviderById(String providerId) {
        return providerRepository.findById(providerId);
    }

    public Provider createProvider(Provider provider) {
        return providerRepository.save(provider);
    }

    public Optional<Provider> updateProvider(String providerId, Provider provider) {
        Provider existingProvider = providerRepository.findById(providerId).orElse(null);
        if (existingProvider == null) {
            return Optional.empty();
        }
        provider.setProviderId(providerId);
        Provider updatedProvider = providerRepository.save(provider);
        return Optional.of(updatedProvider);
    }

    public boolean deleteProvider(String providerId) {
        return providerRepository.findById(providerId).map(provider -> {
            providerRepository.delete(provider);
            return true;
        }).orElse(false);
    }
}
