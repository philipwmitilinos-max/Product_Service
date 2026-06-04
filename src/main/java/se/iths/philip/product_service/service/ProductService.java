package se.iths.philip.product_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.iths.philip.product_service.dto.ProductRequestDTO;
import se.iths.philip.product_service.dto.ProductResponseDTO;
import se.iths.philip.product_service.dto.OrderItemRequest;
import se.iths.philip.product_service.exception.InsufficientStockException;
import se.iths.philip.product_service.exception.ProductNotFoundException;
import se.iths.philip.product_service.model.Product;
import se.iths.philip.product_service.model.VatClass;
import se.iths.philip.product_service.repository.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    public ProductResponseDTO createProduct(ProductRequestDTO dto) {

        Product product = new Product();

        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setStock(dto.stock());
        product.setVatClass(dto.vatClass());

        Product savedProduct = repository.save(product);

        return mapToResponseDto(savedProduct);
    }

    public List<ProductResponseDTO> getAllProducts() {

        return repository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    public ProductResponseDTO getProductById(Long id) {

        Product product = repository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product with id " + id + " not found"
                        ));

        return mapToResponseDto(product);
    }

    public void deleteProduct(Long id) {

        if (!repository.existsById(id)) {
            throw new ProductNotFoundException(
                    "Product with id " + id + " not found");
        }

        repository.deleteById(id);
    }

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

        for (int i = 0; i < requests.size(); i++) {
            Product product = products.get(i);
            OrderItemRequest request = requests.get(i);

            if (product.getStock() < request.quantity()) {
                throw new InsufficientStockException("Insufficient stock: "
                + product.getName());
            }
        }

        for (int i = 0; i < requests.size(); i++) {

            Product product = products.get(i);
            OrderItemRequest request = requests.get(i);

            product.setStock(product.getStock() - request.quantity());
        }

        return products.stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    private ProductResponseDTO mapToResponseDto(Product product) {

        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getVatClass());
    }
}
