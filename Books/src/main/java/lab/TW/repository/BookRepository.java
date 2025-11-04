package lab.TW.repository;

import lab.TW.entity.Book;
import lab.TW.enums.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {
    List<Book> findByTitleContainingIgnoreCase(String title);

    @Query("SELECT DISTINCT b FROM lab.TW.entity.Book b JOIN b.genres g WHERE g IN :genres")
    List<Book> findByGenresIn(Set<Genre> genres);
}
