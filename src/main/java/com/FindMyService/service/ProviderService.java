package com.FindMyService.service;

import com.FindMyService.model.Provider;
import com.FindMyService.repository.ProviderRepository;
import com.FindMyService.utils.ErrorResponseBuilder;
import com.FindMyService.utils.OwnerCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ProviderService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ProviderRepository providerRepository;

    public ProviderService(ProviderRepository providerRepository, OwnerCheck ownerCheck) {
        this.providerRepository = providerRepository;
    }

    public List<Provider> getAllProviders() {
        return providerRepository.findAll();
    }

    public Optional<Provider> getProviderById(Long providerId) {
        return providerRepository.findById(providerId);
    }

    @Transactional
    public ResponseEntity<?> createProvider(Provider provider) {
        if (provider.getEmail() == null || provider.getEmail().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponseBuilder.build(HttpStatus.BAD_REQUEST, "Email is required"));
        }

        if (provider.getPassword() == null || provider.getPassword().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponseBuilder.build(HttpStatus.BAD_REQUEST, "Password is required"));
        }

        try {
            provider.setPassword(passwordEncoder.encode(provider.getPassword()));
            Provider created = providerRepository.save(provider);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponseBuilder.serverError("Failed to create provider: " + e.getMessage()));
        }
    }

    @Transactional
    public ResponseEntity<?> updateProvider(Long providerId, Provider provider) {
        Optional<Provider> existingProvider = providerRepository.findById(providerId);
        if (existingProvider.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponseBuilder.build(HttpStatus.NOT_FOUND, "Provider not found"));
        }

        try {
            provider.setProviderId(providerId);
            if (provider.getPassword() != null && !provider.getPassword().isEmpty()) {
                provider.setPassword(passwordEncoder.encode(provider.getPassword()));
            } else {
                provider.setPassword(existingProvider.get().getPassword());
            }
            Provider updated = providerRepository.save(provider);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponseBuilder.serverError("Failed to update provider: " + e.getMessage()));
        }
    }

    @Transactional
    public boolean deleteProvider(Long providerId) {
        return providerRepository.findById(providerId).map(provider -> {
            providerRepository.delete(provider);
            return true;
        }).orElse(false);
    }
}
