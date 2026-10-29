package se.iths.philip.product_service.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // Exception handler för ProductNotFoundException
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<String> handleProductNotFound(
            ProductNotFoundException ex) {
        // Returnerar en ResponseEntity med status NOT_FOUND och meddelandet från undantaget
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    // Exception handler för InsufficientStockException
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<String> handleInsufficientStock(
            InsufficientStockException ex) {
        // Returnerar en ResponseEntity med status BAD_REQUEST och meddelandet från undantaget
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    // Exception handler för ConstraintViolationException
    public String handleConstraintViolation(
            ConstraintViolationException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleInvalidEnum(
            HttpMessageNotReadableException ex) {
        // Returnerar en ResponseEntity med status BAD_REQUEST och ett meddelande om ogiltig VAT-klass
        return ResponseEntity
                .badRequest()
                .body("Invalid VAT class");
    }
}
