package lab.TW.controller;

import lab.TW.dto.BookDTO;
import lab.TW.enums.Genre;
import lab.TW.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<BookDTO> getBookByIsbn(@PathVariable String isbn) {
        return ResponseEntity.ok(bookService.getBookByIsbn(isbn));
    }

    @PostMapping
    public ResponseEntity<BookDTO> createBook(@RequestBody BookDTO bookDTO) {
        validateBookDTO(bookDTO);
        return new ResponseEntity<>(bookService.createBook(bookDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{isbn}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable String isbn, @RequestBody BookDTO bookDTO) {
        validateBookDTO(bookDTO);
        return ResponseEntity.ok(bookService.updateBook(isbn, bookDTO));
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<Void> deleteBook(@PathVariable String isbn) {
        bookService.deleteBook(isbn);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{isbn}/restrict")
    public ResponseEntity<BookDTO> restrictBook(@PathVariable String isbn) {
        return ResponseEntity.ok(bookService.restrictBook(isbn));
    }

    @PatchMapping("/{isbn}/unrestrict")
    public ResponseEntity<BookDTO> unrestrictBook(@PathVariable String isbn) {
        return ResponseEntity.ok(bookService.unrestrictBook(isbn));
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookDTO>> searchBooksByTitle(@RequestParam String title) {
        return ResponseEntity.ok(bookService.searchBooksByTitle(title));
    }

    @GetMapping("/genres")
    public ResponseEntity<List<BookDTO>> getBooksByGenres(@RequestParam Set<Genre> genres) {
        return ResponseEntity.ok(bookService.getBooksByGenres(genres));
    }

    private void validateBookDTO(BookDTO bookDTO) {
        if (bookDTO == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        if (bookDTO.getIsbn() == null || bookDTO.getIsbn().trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN is required");
        }
        if (bookDTO.getTitle() == null || bookDTO.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (bookDTO.getGenres() == null || bookDTO.getGenres().isEmpty()) {
            throw new IllegalArgumentException("At least one genre must be specified");
        }
    }
}
