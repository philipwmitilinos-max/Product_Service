package se.iths.philip.product_service.dto;

import se.iths.philip.product_service.model.VatClass;

import java.math.BigDecimal;

public record ProductResponseDTO(
        Long id,
        String name,
        String description,
        BigDecimal price,
        int stock,
        VatClass vatClass) {
}
