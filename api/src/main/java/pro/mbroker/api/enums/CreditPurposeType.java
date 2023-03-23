package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CreditPurposeType implements DirectoryEnumMarker {
    PURCHASE_UNDER_CONSTRUCTION("PURCHASE_UNDER_CONSTRUCTION", "Покупка строящегося жилья", null),
    PURCHASE_READY_HOUSE("PURCHASE_READY_HOUSE", "Покупка готового жилья", null),
    REFINANCING("REFINANCING", "Рефинансирование", null);

    private final String code;
    private final String name;
    private final String description;
}

