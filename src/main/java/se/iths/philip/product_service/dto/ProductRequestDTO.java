package se.iths.philip.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import se.iths.philip.product_service.model.VatClass;

import java.math.BigDecimal;

public record ProductRequestDTO(

        @NotBlank
        String name,

        @NotBlank
        String description,

        @Positive
        BigDecimal price,

        @PositiveOrZero
        int stock,

        @NotNull
        VatClass vatClass
) {
}
