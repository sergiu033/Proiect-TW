package org.example.review.mapper;

import org.example.review.dto.ReviewCreateDTO;
import org.example.review.dto.ReviewDTO;
import org.example.review.entity.Review;

import java.util.Optional;

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
