package com.example.review;

import com.example.review.dto.ReviewCreateDTO;
import com.example.review.dto.ReviewDTO;
import com.example.review.entity.Review;
import com.example.review.enums.Rating;
import com.example.review.exception.ResourceNotFoundException;
import com.example.review.repository.ReviewRepository;
import com.example.review.service.ReviewServiceImpl;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class ReviewApplicationTests {

	@Test
	void contextLoads() {
	}

	@Nested
	@ExtendWith(MockitoExtension.class)
	class ReviewServiceTests {

		@Mock
		private ReviewRepository reviewRepository;

		@InjectMocks
		private ReviewServiceImpl reviewService;

		@Test
		void createReview_ShouldSaveAndReturnDTO() {

			ReviewCreateDTO createDTO = new ReviewCreateDTO(1L, 101L, Rating.FIVE_STARS, "Super carte", "Mi-a placut mult");
			Review savedEntity = new Review(1L, 101L, Rating.FIVE_STARS, "Super carte", "Mi-a placut mult");
			savedEntity.setReviewId(1L);


			when(reviewRepository.save(any(Review.class))).thenReturn(savedEntity);


			ReviewDTO result = reviewService.createReview(createDTO);


			assertNotNull(result);
			assertEquals("Super carte", result.getTitle());
			assertEquals(Rating.FIVE_STARS, result.getRating());
			verify(reviewRepository).save(any(Review.class));
		}

		@Test
		void fetchReview_ShouldReturnDTO_WhenExists() {

			Review review = new Review(1L, 101L, Rating.FOUR_STARS, "Titlu", "Descriere");
			review.setReviewId(5L);

			when(reviewRepository.findById(5L)).thenReturn(Optional.of(review));


			ReviewDTO result = reviewService.fetchReview(5L);


			assertNotNull(result);
			assertEquals("Titlu", result.getTitle());
		}

		@Test
		void fetchReview_ShouldThrowException_WhenNotFound() {

			when(reviewRepository.findById(99L)).thenReturn(Optional.empty());


			assertThrows(ResourceNotFoundException.class, () -> reviewService.fetchReview(99L));
		}

		@Test
		void updateReview_ShouldUpdateAndReturnTrue() {

			Review existingReview = new Review(1L, 101L, Rating.ONE_STAR, "Vechi", "Vechi");
			ReviewDTO updateDTO = new ReviewDTO("Nou", "Descriere Noua", Rating.FIVE_STARS);

			when(reviewRepository.findById(1L)).thenReturn(Optional.of(existingReview));
			when(reviewRepository.save(any(Review.class))).thenReturn(existingReview);


			boolean result = reviewService.updateReview(1L, updateDTO);


			assertTrue(result);
			assertEquals("Nou", existingReview.getTitle()); // Verificăm dacă obiectul a fost modificat
			assertEquals(Rating.FIVE_STARS, existingReview.getRating());
		}

		@Test
		void fetchAverageRating_ShouldCalculateCorrectly() {

			Review r1 = new Review(1L, 100L, Rating.FIVE_STARS, "T1", "D1"); // Valoare 5
			Review r2 = new Review(2L, 100L, Rating.THREE_STARS, "T2", "D2"); // Valoare 3


			when(reviewRepository.findByBookId(100L)).thenReturn(List.of(r1, r2));


			double average = reviewService.fetchAverageRating(100L);


			assertEquals(4.0, average);
		}
	}


	@Nested
	@DataJpaTest
	class ReviewRepositoryTests {

		@Autowired
		private ReviewRepository reviewRepository;

		@Test
		void saveAndFindByUserId_ShouldReturnReviews() {

			Review r1 = new Review(10L, 500L, Rating.FIVE_STARS, "Review 1", "Desc 1");
			Review r2 = new Review(10L, 501L, Rating.FOUR_STARS, "Review 2", "Desc 2");
			Review other = new Review(11L, 500L, Rating.ONE_STAR, "Review Altul", "Desc Altul");

			reviewRepository.save(r1);
			reviewRepository.save(r2);
			reviewRepository.save(other);


			List<Review> userReviews = reviewRepository.findByUserId(10L);


			assertEquals(2, userReviews.size());
			assertTrue(userReviews.stream().anyMatch(r -> r.getTitle().equals("Review 1")));
		}

		@Test
		void findByBookId_ShouldReturnCorrectReviews() {

			Review r1 = new Review(1L, 999L, Rating.FIVE_STARS, "Book Review 1", "D");
			Review r2 = new Review(2L, 999L, Rating.TWO_STARS, "Book Review 2", "D");

			reviewRepository.save(r1);
			reviewRepository.save(r2);


			List<Review> bookReviews = reviewRepository.findByBookId(999L);


			assertEquals(2, bookReviews.size());
		}
	}
}