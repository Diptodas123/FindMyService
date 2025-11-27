package com.FindMyService.controller;

import com.FindMyService.model.Provider;
import com.FindMyService.model.dto.ProviderDto;
import com.FindMyService.service.ProviderService;
import com.FindMyService.utils.DtoMapper;
import com.FindMyService.utils.ErrorResponseBuilder;
import com.FindMyService.utils.OwnerCheck;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/api/v1/providers")
@RestController
public class ProviderController {

    private final ProviderService providerService;
    private final OwnerCheck ownerCheck;

    public ProviderController(ProviderService providerService, OwnerCheck ownerCheck) {
        this.providerService = providerService;
        this.ownerCheck = ownerCheck;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<ProviderDto>> getAllProviders() {
        List<ProviderDto> dtos = providerService.getAllProviders()
                .stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{providerId}")
    public ResponseEntity<?> getProvider(@PathVariable Long providerId, HttpServletRequest request) {
        try {
            ownerCheck.verifyOwner(providerId);
        } catch (AccessDeniedException ex) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ErrorResponseBuilder.forbidden("You are not authorized to access this provider"));
        }
        return providerService.getProviderById(providerId)
                .map(DtoMapper::toDto)
                .map(dto -> ResponseEntity.ok((Object) dto))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ErrorResponseBuilder.build(HttpStatus.NOT_FOUND, "Provider not found")));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> createProvider(@RequestBody Provider provider) {
        return providerService.createProvider(provider);
    }

    @PutMapping("/{providerId}")
    @PreAuthorize("hasAuthority('PROVIDER') or hasAuthority('ADMIN')")
    public ResponseEntity<?> updateProvider(@PathVariable Long providerId, @RequestBody Provider provider) {
        try {
            ownerCheck.verifyOwner(providerId);
        } catch (AccessDeniedException ex) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ErrorResponseBuilder.forbidden("You are not authorized to update this provider"));
        }

        return providerService.updateProvider(providerId, provider);
    }

    @DeleteMapping("/{providerId}")
    @PreAuthorize("hasAuthority('PROVIDER') or hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteProvider(@PathVariable Long providerId) {
        try {
            ownerCheck.verifyOwner(providerId);
        } catch (AccessDeniedException ex) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ErrorResponseBuilder.forbidden("You are not authorized to delete this provider"));
        }

        boolean deleted = providerService.deleteProvider(providerId);
        if (deleted) {
            return ResponseEntity.ok(ErrorResponseBuilder.ok("Provider deleted successfully"));
        }
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponseBuilder.build(HttpStatus.NOT_FOUND, "Provider not found"));
    }
}
