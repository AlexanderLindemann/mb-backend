package pro.mbroker.api.enums;

public enum CreditPurposeType {
    PURCHASE_UNDER_CONSTRUCTION("Покупка строящегося жилья"),
    PURCHASE_READY_HOUSE("Покупка готового жилья"),
    REFINANCING("Рефинансирование");

    private final String name;

    CreditPurposeType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
