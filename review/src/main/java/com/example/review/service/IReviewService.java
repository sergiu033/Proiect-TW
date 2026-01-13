package com.example.review.service;

import com.example.review.dto.ReviewCreateDTO;
import com.example.review.dto.ReviewDTO;

import java.util.List;

public interface IReviewService {
    ReviewDTO createReview(ReviewCreateDTO reviewCreateDTO);
    ReviewDTO fetchReview(Long reviewId);
    boolean updateReview(Long  reviewId, ReviewDTO reviewDTO);
    boolean deleteReview(Long reviewId);
    List<ReviewDTO> fetchReviewsByUserId(Long userId);
    List<ReviewDTO> fetchReviewsByBookId(Long reviewerId);
    double fetchAverageRating(Long bookId);
}
