package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "BasisOfOwnership", name = "Основание права")
public enum BasisOfOwnership implements EnumWithValue<String> {
    ACQUISITION("ACQUISITION", "Приобретение"),
    INHERITANCE("INHERITANCE", "Наследство");

    private final String value;
    private final String name;
}



