package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "CreditProgramType", name = "Тип кредитной программы")
public enum CreditProgramType implements EnumWithValue<String> {
    STANDARD("STANDARD", "Стандартная"),
    FAMILY("FAMILY", "Семейная"),
    MILITARY("MILITARY", "Военная"),
    PREFERENTIAL("PREFERENTIAL", "Льготная ипотека"),
    IT("IT", "IT-ипотека");

    private final String value;
    private final String name;

    private static final Map<String, CreditProgramType> BY_VALUE = new HashMap<>();
    static {
        for (CreditProgramType type : values()) {
            BY_VALUE.put(type.value, type);
        }
    }
    public static CreditProgramType fromString(String value) {
        return BY_VALUE.getOrDefault(value, STANDARD);
    }
}
