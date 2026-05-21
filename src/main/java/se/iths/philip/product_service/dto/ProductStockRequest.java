package se.iths.philip.product_service.dto;

import jakarta.validation.constraints.Positive;

public record ProductStockRequest(

        Long productId,

        @Positive
        int quantity
) {
}
