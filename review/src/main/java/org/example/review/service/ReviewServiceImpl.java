package org.example.review.service;

import jakarta.transaction.Transactional;
import org.example.review.dto.ReviewCreateDTO;
import org.example.review.dto.ReviewDTO;
import org.example.review.entity.Review;
import org.example.review.exception.ResourceNotFoundException;
import org.example.review.mapper.ReviewMapper;
import org.example.review.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements IReviewService{

    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public ReviewDTO createReview(ReviewCreateDTO reviewCreateDTO) {
        Review newReview = ReviewMapper.toEntity(reviewCreateDTO);
        reviewRepository.save(newReview);

        return ReviewMapper.toDTO(newReview);
    }

    @Override
    public ReviewDTO fetchReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        return ReviewMapper.toDTO(review);
    }

    @Override
    public boolean updateReview(Long reviewId, ReviewDTO reviewDTO) {
        try {
            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
            review.setRating(reviewDTO.getRating());
            review.setDescription(reviewDTO.getDescription());
            review.setTitle(reviewDTO.getTitle());
            reviewRepository.save(review);
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
        return true;
    }

    @Override
    public List<ReviewDTO> fetchReviewsByUserId(Long userId) {
        List<Review> reviews = reviewRepository.findByUserId(userId);
        return reviews
                .stream()
                .map(ReviewMapper::toDTO)
                .toList();
    }

    @Override
    public List<ReviewDTO> fetchReviewsByBookId(Long bookId) {
        List<Review> reviews = reviewRepository.findByBookId(bookId);
        return reviews
                .stream()
                .map(ReviewMapper::toDTO)
                .toList();
    }

    @Override
    public double fetchAverageRating(Long bookId) {
        List<Review> reviews = reviewRepository.findByBookId(bookId);
        return reviews
                .stream()
                .mapToInt(r -> r.getRating().getValue())
                .sum()
                / (double) reviews.size();
    }
}
