package org.example.review.dto;

import org.example.review.enums.Rating;

public class ReviewCreateDTO {
    private Long userId;
    private Long bookId;
    private Rating rating;
    private String title;
    private String description;

    public ReviewCreateDTO(Long userId, Long bookId, Rating rating, String title, String description) {
        this.userId = userId;
        this.bookId = bookId;
        this.rating = rating;
        this.title = title;
        this.description = description;
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
