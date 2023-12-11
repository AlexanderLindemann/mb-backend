package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "CreditPurposeType", name = "Цели кредита")
public enum CreditPurposeType implements EnumWithValue<String> {
    PURCHASE_UNDER_CONSTRUCTION("PURCHASE_UNDER_CONSTRUCTION", "Покупка строящегося жилья"),
    PURCHASE_READY_HOUSE("PURCHASE_READY_HOUSE", "Покупка готового жилья"),
    REFINANCING("REFINANCING", "Рефинансирование");

    private final String value;
    private final String name;

    public static List<CreditPurposeType> getAll() {
        return Arrays.asList(CreditPurposeType.values());
    }
    public static List<String> getAllNames() {
        return Arrays.stream(CreditPurposeType.values())
                .map(CreditPurposeType::getName)
                .collect(Collectors.toList());
    }
}

