package se.iths.philip.product_service.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VatClass {

    VAT_25(25),
    VAT_12(12),
    VAT_6(6);

    private final int percentage;
}
