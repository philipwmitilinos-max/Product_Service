package se.iths.philip.product_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import se.iths.philip.product_service.dto.OrderItemRequest;
import se.iths.philip.product_service.dto.ProductRequestDTO;
import se.iths.philip.product_service.dto.ProductResponseDTO;
import se.iths.philip.product_service.model.VatClass;
import se.iths.philip.product_service.service.ProductService;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService service;

    private ProductRequestDTO requestDTO;
    private ProductResponseDTO responseDTO;
    private OrderItemRequest orderItem;

    @BeforeEach
    void setUp() {

        requestDTO = new ProductRequestDTO(
                "Keyboard",
                "Mechanical keyboard",
                BigDecimal.valueOf(1000),
                10,
                VatClass.VAT_25
        );

        responseDTO = new ProductResponseDTO(
                1L,
                "Keyboard",
                "Mechanical keyboard",
                BigDecimal.valueOf(1000),
                10,
                VatClass.VAT_25
        );

        orderItem = new OrderItemRequest(
                1L,
                2
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProductShouldReturn201() throws Exception {

        when(service.createProduct(any()))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/products")
                        .with(jwt().authorities(() -> "ROLE_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Keyboard"))
                .andExpect(jsonPath("$.vatClass").value("VAT_25"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createProductShouldReturn403ForUser() throws Exception {
        
        mockMvc.perform(post("/products")
                        .with(jwt().authorities(() -> "ROLE_USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createProductShouldReturn401WithoutJwt() throws Exception {

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createProductShouldReturn400WhenIsBlank() throws Exception {

        ProductRequestDTO invalidRequestDTO = new ProductRequestDTO(
                "",
                "Mechanical keyboard",
                BigDecimal.valueOf(1000),
                10,
                VatClass.VAT_25
        );

        mockMvc.perform(post("/products")
                        .with(jwt().authorities(() -> "ROLE_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProductShouldCallService() throws Exception {

        when(service.createProduct(any()))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/products")
                .with(jwt().authorities(() -> "ROLE_ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated());

        verify(service).createProduct(any());
    }

    @Test
    void getAllProductsShouldReturn200() throws Exception {

        List<ProductResponseDTO> product = List.of(responseDTO);

        when(service.getAllProducts())
                .thenReturn(product);

        mockMvc.perform(get("/products")
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Keyboard"));
    }

    @Test
    void deleteProductShouldReturn204() throws Exception {

        mockMvc.perform(delete("/products/1")
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isNoContent());

        verify(service).deleteProduct(1L);
    }

    @Test
    void deleteProductShouldReturn403() throws Exception {

        mockMvc.perform(delete("/products/1")
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void decreaseStockShouldReturn200() throws Exception {

        List<OrderItemRequest> requests = List.of(orderItem);

        List<ProductResponseDTO> response = List.of(responseDTO);

        when(service.decreaseStock(any()))
                .thenReturn(response);

        mockMvc.perform(post("/products/stock/decrease")
                        .with(jwt().authorities(() -> "ROLE_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Keyboard"));

        verify(service).decreaseStock(any());
    }

    @Test
    void decreaseStockShouldReturn400ForNegativeQuantity() throws Exception {

        List<OrderItemRequest> requests = List.of(
                new OrderItemRequest(1L, -1)
        );

        mockMvc.perform(post("/products/stock/decrease")
                        .with(jwt().authorities(() -> "ROLE_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isBadRequest());
    }
}