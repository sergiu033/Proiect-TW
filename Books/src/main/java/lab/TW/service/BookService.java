package lab.TW.service;

import lab.TW.dto.BookDTO;
import lab.TW.entity.Book;
import lab.TW.enums.Genre;
import lab.TW.exception.BookNotFoundException;
import lab.TW.exception.BookRestrictedException;
import lab.TW.mapper.BookMapper;
import lab.TW.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDTO)
                .collect(Collectors.toList());
    }

    public BookDTO getBookByIsbn(String isbn) {
        return bookRepository.findById(isbn)
                .map(bookMapper::toDTO)
                .orElseThrow(() -> new BookNotFoundException(isbn));
    }

    public BookDTO createBook(BookDTO bookDTO) {
        Book book = bookMapper.toEntity(bookDTO);
        return bookMapper.toDTO(bookRepository.save(book));
    }

    @Transactional
    public BookDTO updateBook(String isbn, BookDTO bookDTO) {
        Book existingBook = bookRepository.findById(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));

        if (existingBook.isRestricted()) {
            throw new BookRestrictedException(isbn);
        }

        // Set the ISBN to ensure it's not changed
        bookDTO.setIsbn(isbn);

        // Update the entity using the mapper
        bookMapper.updateBookFromDTO(bookDTO, existingBook);

        // Save and return the updated book
        Book savedBook = bookRepository.save(existingBook);
        return bookMapper.toDTO(savedBook);
    }

    public void deleteBook(String isbn) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));

        if (book.isRestricted()) {
            throw new BookRestrictedException(isbn);
        }

        bookRepository.delete(book);
        bookRepository.flush();
    }

    public BookDTO restrictBook(String isbn) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));
        book.setRestricted(true);
        return bookMapper.toDTO(bookRepository.save(book));
    }

    public BookDTO unrestrictBook(String isbn) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));
        book.setRestricted(false);
        return bookMapper.toDTO(bookRepository.save(book));
    }

    public List<BookDTO> searchBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(bookMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<BookDTO> getBooksByGenres(Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            throw new IllegalArgumentException("At least one genre must be specified");
        }
        return bookRepository.findByGenresIn(genres).stream()
                .filter(book -> book.getGenres().containsAll(genres))
                .map(bookMapper::toDTO)
                .collect(Collectors.toList());
    }
}
