package com.FindMyService.service;

import com.FindMyService.model.Rating;
import com.FindMyService.repository.RatingRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    public Optional<Rating> getRatingById(String ratingId) {
        return ratingRepository.findById(ratingId);
    }

    public Rating postRating(Rating rating) {
        return ratingRepository.save(rating);
    }
}
