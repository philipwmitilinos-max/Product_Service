package se.iths.philip.product_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import se.iths.philip.product_service.dto.OrderItemRequest;
import se.iths.philip.product_service.dto.ProductRequestDTO;
import se.iths.philip.product_service.dto.ProductResponseDTO;
import se.iths.philip.product_service.exception.ProductNotFoundException;
import se.iths.philip.product_service.model.Product;
import se.iths.philip.product_service.model.VatClass;
import se.iths.philip.product_service.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    private Product product;
    private ProductRequestDTO requestDTO;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        product = new Product();

        product.setId(1L);
        product.setName("Keyboard");
        product.setDescription("Mechanical keyboard");
        product.setPrice(BigDecimal.valueOf(1000));
        product.setStock(10);
        product.setVatClass(VatClass.VAT_25);

        requestDTO = new ProductRequestDTO(
                "Keyboard",
                "Mechanical keyboard",
                BigDecimal.valueOf(1000),
                10,
                VatClass.VAT_25
        );
    }

    @Test
    void createProductShouldReturnProductDTO() {

        when(repository.save(any(Product.class)))
                .thenReturn(product);

        ProductResponseDTO result = service.createProduct(requestDTO);

        assertEquals("Keyboard", result.name());

        verify(repository).save(any(Product.class));
    }

    @Test
    void getAllProductsShouldReturnListOfProducts() {
        when(repository.findAll())
                .thenReturn(List.of(product));

        List<ProductResponseDTO> result =
                service.getAllProducts();

        assertEquals(1, result.size());

        assertEquals("Keyboard",
                result.get(0).name());
    }

    @Test
    void getProductByIdShouldReturnProduct() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(product));

        ProductResponseDTO result = service.getProductById(1L);

        assertEquals("Keyboard", result.name());
    }

    @Test
    void getProductByIdShouldThrowException() {

        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> service.getProductById(99L)
        );
    }

    @Test
    void deleteProductShouldDeleteProduct() {

        when(repository.existsById(1L))
                .thenReturn(true);

        service.deleteProduct(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void deleteProductShouldThrowException() {

        when(repository.existsById(99L))
                .thenReturn(false);

        assertThrows(
                ProductNotFoundException.class,
                () -> service.deleteProduct(99L));
    }

    @Test
    void decreaseStockShouldDecreaseStock() {

        OrderItemRequest request = new OrderItemRequest(1L, 2);

        when(repository.findById(1L))
                .thenReturn(Optional.of(product));

        List<ProductResponseDTO> result =
                service.decreaseStock(List.of(request));

        assertEquals(8, result.get(0).stock());
    }

    @Test
    void decreaseStockShouldThrowProductNotFoundException() {

        OrderItemRequest request = new OrderItemRequest(99L, 2);

        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> service.decreaseStock(List.of(request))
        );
    }
}