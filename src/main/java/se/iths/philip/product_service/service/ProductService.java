package se.iths.philip.product_service.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import se.iths.philip.product_service.dto.ProductRequestDTO;
import se.iths.philip.product_service.dto.ProductResponseDTO;
import se.iths.philip.product_service.dto.OrderItemRequest;
import se.iths.philip.product_service.exception.InsufficientStockException;
import se.iths.philip.product_service.exception.ProductNotFoundException;
import se.iths.philip.product_service.model.Product;
import se.iths.philip.product_service.repository.ProductRepository;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }
    // Metod för att skapa en ny produkt baserat på ProductRequestDTO
    public ProductResponseDTO createProduct(ProductRequestDTO dto) {

        Product product = new Product();

        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setStock(dto.stock());
        product.setCategory(dto.category());
        product.setImageUrl(dto.imageUrl());

        Product savedProduct = repository.save(product);

        return mapToResponseDto(savedProduct);
    }
    // Metod för att uppdatera en befintlig produkt baserat på dess ID och ProductRequestDTO
    public List<ProductResponseDTO> getAllProducts() {

        return repository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }
    // Metod för att hämta en produkt baserat på dess ID
    public ProductResponseDTO getProductById(Long id) {

        Product product = repository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product with id " + id + " not found"
                        ));

        return mapToResponseDto(product);
    }

    // Metod för att ta bort en produkt baserat på dess ID
    public void deleteProduct(Long id) {

        if (!repository.existsById(id)) {
            throw new ProductNotFoundException(
                    "Product with id " + id + " not found");
        }

        repository.deleteById(id);
    }
    // Metod för att uppdatera en befintlig produkt baserat på dess ID och ProductRequestDTO
    //transactional används för att säkerställa att alla ändringar i databasen sker som en enhetlig operation
    @Transactional
    public List<ProductResponseDTO> decreaseStock(
            List<OrderItemRequest> requests
    ) {
        List<Product> products = requests.stream()
                .map(request -> repository.findById(request.productId())
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product with id "
                                        + request.productId()
                                        + " not found"
                                )))
                .toList();
        // Kontrollera om det finns tillräckligt med lager för varje produkt
        for (int i = 0; i < requests.size(); i++) {
            Product product = products.get(i);
            OrderItemRequest request = requests.get(i);

            if (product.getStock() < request.quantity()) {
                throw new InsufficientStockException("Insufficient stock: "
                + product.getName());
            }
        }

        // Minska lager för varje produkt
        for (int i = 0; i < requests.size(); i++) {

            Product product = products.get(i);
            OrderItemRequest request = requests.get(i);

            product.setStock(product.getStock() - request.quantity());
        }
        // Spara ändringarna i databasen
        return products.stream()
                .map(this::mapToResponseDto)
                .toList();
    }
        // Metod för att mappa en Product-entitet till en ProductResponseDTO
    private ProductResponseDTO mapToResponseDto(Product product) {

        return new ProductResponseDTO
                (product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getCategory(),
                product.getImageUrl());
    }
}
