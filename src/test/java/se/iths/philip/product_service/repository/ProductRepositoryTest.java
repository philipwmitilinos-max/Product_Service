package se.iths.philip.product_service.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import se.iths.philip.product_service.model.Product;
import se.iths.philip.product_service.model.VatClass;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository repository;

    private Product product;

    @BeforeEach
    void setup() {
        product = new Product();

        product.setName("Keyboard");
        product.setDescription("Mechanical keyboard");
        product.setPrice(BigDecimal.valueOf(1000));
        product.setStock(10);
        product.setVatClass(VatClass.VAT_25);
    }

    @Test
    void shouldSaveProduct() {
        Product savedProduct = repository.save(product);

        assertNotNull(savedProduct.getId());

        assertEquals("Keyboard", savedProduct.getName());
    }

    @Test
    void shouldFindProductById() {
        Product savedProduct = repository.save(product);

        Optional<Product> result = repository.findById(savedProduct.getId());

        assertTrue(result.isPresent());

        assertEquals(savedProduct, result.get());
    }

    @Test
    void shouldFindAllProducts() {

        repository.save(product);

        Product mouse = new Product();

        mouse.setName("Mouse");
        mouse.setDescription("Gaming mouse");
        mouse.setPrice(BigDecimal.valueOf(500));
        mouse.setStock(20);
        mouse.setVatClass(VatClass.VAT_25);

        repository.save(mouse);

        assertEquals(2, repository.findAll().size());
    }

    @Test
    void shouldDeleteProduct() {

        Product savedProduct = repository.save(product);

        repository.deleteById(savedProduct.getId());

        Optional<Product> result = repository.findById(savedProduct.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnTrueWhenProductExists() {

        Product savedProduct = repository.save(product);

        boolean exists = repository.existsById(savedProduct.getId());

        assertTrue(exists);
    }
}