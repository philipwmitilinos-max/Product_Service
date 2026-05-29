package se.iths.philip.product_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@Validated
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponseDTO createProduct(
            @Valid @RequestBody ProductRequestDTO dto) {

        return service.createProduct(dto);
    }

    @GetMapping
    public List<ProductResponseDTO> getAllProducts() {

        return service.getAllProducts();
    }

    @GetMapping("/{id}")
    public ProductResponseDTO getProductById(
            @PathVariable Long id) {

        return service.getProductById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        service.deleteProduct(id);
    }

    @PostMapping("/stock/decrease")
    public List<ProductResponseDTO> decreaseStock(
            @RequestBody List<@Valid OrderItemRequest> requests) {
        return service.decreaseStock(requests);
    }
}
