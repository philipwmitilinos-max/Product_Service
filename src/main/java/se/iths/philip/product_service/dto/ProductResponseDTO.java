package se.iths.philip.product_service.dto;

import java.math.BigDecimal;

public record ProductResponseDTO(
        Long id,
        String name,
        String description,
        String category,
        BigDecimal price,
        int stock) {
}
