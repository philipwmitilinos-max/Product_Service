package se.iths.philip.product_service.model;

public enum VatClass {

    VAT_25(25),
    VAT_12(12),
    VAT_6(6);

    private final int percentage;

    VatClass(int percentage) {
        this.percentage = percentage;
    }

    public int getPercentage() {
        return percentage;
    }
}
