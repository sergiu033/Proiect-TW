package lab.TW.exception;

public class BookRestrictedException extends RuntimeException {
    public BookRestrictedException(String isbn) {
        super("Book with ISBN " + isbn + " is restricted and cannot be modified");
    }
}
