package org.example.review.controller;

import org.example.review.dto.ReviewCreateDTO;
import org.example.review.dto.ReviewDTO;
import org.example.review.service.IReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/")
public class ReviewController {

    @Autowired
    public IReviewService reviewService;

    @PostMapping("/reviews")
    public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewCreateDTO reviewCreateDTO) {
        ReviewDTO createdReview = reviewService.createReview(reviewCreateDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdReview);
    }

    @GetMapping("/reviews")
    public ResponseEntity<ReviewDTO> fetchReview(@RequestParam("id") Long id) {
        ReviewDTO fetchedReview = reviewService.fetchReview(id);
        return  ResponseEntity
                .status(HttpStatus.OK)
                .body(fetchedReview);
    }

    @PutMapping("/reviews")
    public ResponseEntity<ReviewDTO> updateReview(@RequestParam("id") Long id, @RequestBody ReviewDTO reviewDTO) {
        boolean isUpdated = reviewService.updateReview(id,  reviewDTO);
        if (isUpdated) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(reviewDTO);
        }
        else {
            return ResponseEntity
                    .status(HttpStatus.NOT_MODIFIED)
                    .body(null);
        }
    }

    @DeleteMapping("/reviews")
    public ResponseEntity<String>  deleteReview(@RequestParam("id") Long id) {
        boolean isDeleted = reviewService.deleteReview(id);
        if (isDeleted) {
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body("Deleted Successfully");
        }
        else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Review with id " + id + " not found");
        }
    }

    @GetMapping("/book")
    public ResponseEntity<List<ReviewDTO>> getAllReviewsByBook(@RequestParam("bookId") Long bookId) {
        List<ReviewDTO> listOfReviews = reviewService.fetchReviewsByBookId(bookId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(listOfReviews);
    }

    @GetMapping("/user")
    public ResponseEntity<List<ReviewDTO>> getAllReviewsByUser(@RequestParam("userId") Long userId) {
        List<ReviewDTO> listOfReviews = reviewService.fetchReviewsByUserId(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(listOfReviews);
    }

    @GetMapping("/book/average_rating")
    public ResponseEntity<Double> getAverageRating(@RequestParam("bookId") Long bookId) {
        double averageRating = reviewService.fetchAverageRating(bookId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(averageRating);
    }

}
