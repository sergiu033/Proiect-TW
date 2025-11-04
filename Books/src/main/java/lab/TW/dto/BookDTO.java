package lab.TW.dto;

import lab.TW.enums.Genre;
import java.util.HashSet;
import java.util.Set;

public class BookDTO {
    private String isbn;
    private String title;
    private String description;
    private String cover;
    private boolean restricted;
    private Set<Genre> genres = new HashSet<>();

    public BookDTO() {
    }

    public BookDTO(String isbn, String title, String description, String cover, Set<Genre> genres) {
        this.isbn = isbn;
        this.title = title;
        this.description = description;
        this.cover = cover;
        setGenres(genres);
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
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

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public boolean isRestricted() {
        return restricted;
    }

    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres.clear();
        if (genres != null) {
            this.genres.addAll(genres);
        }
    }
}
