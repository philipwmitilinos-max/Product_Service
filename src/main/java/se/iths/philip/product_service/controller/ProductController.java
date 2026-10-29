package se.iths.philip.product_service.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import se.iths.philip.product_service.dto.ProductRequestDTO;
import se.iths.philip.product_service.dto.ProductResponseDTO;
import se.iths.philip.product_service.dto.OrderItemRequest;
import se.iths.philip.product_service.service.ProductService;

import java.util.List;


@RestController
@RequestMapping("/products")
// Validera inkommande request body med hjälp av @Validated
@Validated
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    // Endpoint för att skapa en ny produkt med hjälp av ProductRequestDTO som request body
    public ProductResponseDTO createProduct(
            @Valid @RequestBody ProductRequestDTO dto) {

        return service.createProduct(dto);
    }

    @GetMapping
    // Endpoint för att hämta alla produkter
    public List<ProductResponseDTO> getAllProducts() {

        return service.getAllProducts();
    }

    @GetMapping("/{id}")
    // Endpoint för att hämta en produkt efter ID
    public ProductResponseDTO getProductById(
            @PathVariable Long id) {

        return service.getProductById(id);
    }

    @DeleteMapping("/{id}")
    // Endpoint för att ta bort en produkt efter ID
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        service.deleteProduct(id);
    }

    @PostMapping("/stock/decrease")
    // Endpoint för att minska lagersaldo för en lista av produkter baserat på OrderItemRequest
    public List<ProductResponseDTO> decreaseStock(
            @RequestBody List<@Valid OrderItemRequest> requests) {
        return service.decreaseStock(requests);
    }
}
