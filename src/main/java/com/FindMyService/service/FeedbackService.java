package com.FindMyService.service;

import com.FindMyService.model.Feedback;
import com.FindMyService.model.ServiceCatalog;
import com.FindMyService.model.User;
import com.FindMyService.repository.FeedbackRepository;
import com.FindMyService.repository.ProviderRepository;
import com.FindMyService.repository.ServiceCatalogRepository;
import com.FindMyService.repository.UserRepository;
import com.FindMyService.utils.ErrorResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final ServiceCatalogRepository serviceCatalogRepository;
    private final ProviderRepository providerRepository;

    public FeedbackService(FeedbackRepository feedbackRepository,
                           UserRepository userRepository,
                           ServiceCatalogRepository serviceCatalogRepository,
                           ProviderRepository providerRepository) {
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
        this.serviceCatalogRepository = serviceCatalogRepository;
        this.providerRepository = providerRepository;
    }

    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }

    @Transactional
    public ResponseEntity<?> createFeedback(Feedback feedback) {
        if (feedback.getRating() < 1 || feedback.getRating() > 5) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponseBuilder.build(HttpStatus.BAD_REQUEST, "Rating must be between 1 and 5"));
        }

        Optional<User> user = userRepository.findById(feedback.getUserId().getUserId());
        if (user.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponseBuilder.build(HttpStatus.BAD_REQUEST, "User from payload not found"));
        }

        Optional<ServiceCatalog> serviceCatalog = serviceCatalogRepository
                .findById(feedback.getServiceId().getServiceId());
        if (serviceCatalog.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponseBuilder.build(HttpStatus.BAD_REQUEST, "Service catalog not found"));
        }

        Feedback saved = feedbackRepository.save(feedback);

        try {
            updateRatings(feedback);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponseBuilder.serverError(e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Transactional
    public ResponseEntity<?> getAllFeedbacksForService(Long serviceId) {
        Optional<ServiceCatalog> serviceCatalog = serviceCatalogRepository.findById(serviceId);
        if (serviceCatalog.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponseBuilder.build(HttpStatus.NOT_FOUND, "Service not found"));
        }

        List<Feedback> feedbacks = feedbackRepository.findByServiceId(serviceCatalog.get());
        return ResponseEntity.ok(feedbacks);
    }

    void updateRatings(Feedback feedback) {
        ServiceCatalog serviceCatalog = serviceCatalogRepository.findById(feedback.getServiceId().getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found"));
        com.FindMyService.model.Provider provider = providerRepository.findById(serviceCatalog.getProviderId().getProviderId())
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        int totalServiceReviews = serviceCatalog.getTotalRatings();
        int totalProviderReviews = provider.getTotalRatings();

        int newTotalServiceReviews = totalServiceReviews + 1;
        int newTotalProviderReviews = totalProviderReviews + 1;

        BigDecimal currentProviderRating = provider.getAvgRating() != null ? provider.getAvgRating() : BigDecimal.ZERO;
        BigDecimal updatedProviderRating = currentProviderRating
                .multiply(BigDecimal.valueOf(totalProviderReviews))
                .add(BigDecimal.valueOf(feedback.getRating()))
                .divide(BigDecimal.valueOf(newTotalProviderReviews), 1, java.math.RoundingMode.HALF_UP);

        BigDecimal currentServiceRating = serviceCatalog.getAvgRating() != null ? serviceCatalog.getAvgRating() : BigDecimal.ZERO;
        BigDecimal updatedServiceRating = currentServiceRating
                .multiply(BigDecimal.valueOf(totalServiceReviews))
                .add(BigDecimal.valueOf(feedback.getRating()))
                .divide(BigDecimal.valueOf(newTotalServiceReviews), 1, java.math.RoundingMode.HALF_UP);

        provider.setAvgRating(updatedProviderRating);
        provider.setTotalRatings(newTotalProviderReviews);

        serviceCatalog.setAvgRating(updatedServiceRating);
        serviceCatalog.setTotalRatings(newTotalServiceReviews);

        providerRepository.save(provider);
        serviceCatalogRepository.save(serviceCatalog);
    }
}
