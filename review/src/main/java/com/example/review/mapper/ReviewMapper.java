package com.example.review.mapper;

import com.example.review.dto.ReviewCreateDTO;
import com.example.review.dto.ReviewDTO;
import com.example.review.entity.Review;

public class ReviewMapper {
    public static Review toEntity(ReviewCreateDTO reviewCreateDTO) {
        return new Review(
                reviewCreateDTO.getUserId(),
                reviewCreateDTO.getBookId(),
                reviewCreateDTO.getRating(),
                reviewCreateDTO.getTitle(),
                reviewCreateDTO.getDescription()
        );
    }

    public static ReviewDTO toDTO(Review review) {
        return new ReviewDTO(
                review.getTitle(),
                review.getDescription(),
                review.getRating()
        );
    }
}
