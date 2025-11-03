package org.example.review.entity;

import jakarta.persistence.*;
import org.example.review.enums.Rating;

@Entity
@Table(name = "Review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="review_id")
    private Long reviewId;

    @Column(name="user_id")
    private Long userId;

    @Column(name="book_id")
    private Long bookId;

    @Enumerated(EnumType.STRING)
    @Column(name="rating")
    private Rating rating;

    @Column(name="title")
    private String title;

    @Column(name="description")
    private String description;

    public Review(Long userId, Long bookId, Rating rating, String title, String description) {
        this.userId = userId;
        this.bookId = bookId;
        this.rating = rating;
        this.title = title;
        this.description = description;
    }

    public Review() {
    }

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
