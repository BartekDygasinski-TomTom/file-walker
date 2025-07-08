package pl.bdygasinski.filewalker.exception;

public class EntryNotAccessibleException extends RuntimeException {

    public EntryNotAccessibleException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
