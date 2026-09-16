package pl.adam.onlineshop.exception;

import java.io.IOException;
import java.nio.file.Path;

public class InvoiceFileException extends RuntimeException {
    public InvoiceFileException(Path invoicePath, IOException cause) {
        super("Could not save invoice file: " + invoicePath, cause);
    }
}
