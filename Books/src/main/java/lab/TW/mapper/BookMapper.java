package lab.TW.mapper;

import lab.TW.dto.BookDTO;
import lab.TW.entity.Book;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class BookMapper {

    @Mapping(target = "genres", source = "genres")
    public abstract BookDTO toDTO(Book book);

    @Mapping(target = "genres", source = "genres")
    public abstract Book toEntity(BookDTO bookDTO);

    @Mapping(target = "genres", source = "genres")
    public abstract void updateBookFromDTO(BookDTO bookDTO, @MappingTarget Book book);

    @AfterMapping
    protected void handleGenresAfterMapping(BookDTO bookDTO, @MappingTarget Book book) {
        if (bookDTO.getGenres() != null) {
            book.setGenres(bookDTO.getGenres());
        }
    }
}
