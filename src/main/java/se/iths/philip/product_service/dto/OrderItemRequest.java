package se.iths.philip.product_service.dto;

import jakarta.validation.constraints.Positive;

public record OrderItemRequest(

        Long productId,

        @Positive
        int quantity
) {
}
