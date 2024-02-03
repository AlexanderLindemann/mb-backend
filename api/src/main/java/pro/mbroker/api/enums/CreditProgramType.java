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
    IT("IT", "IT-ипотека"),
    IGC("IGC", "ИЖС хозспособом");

    private final String value;
    private final String name;

    private static final Map<String, CreditProgramType> objectTypeMap = initializeMap();

    private static Map<String, CreditProgramType> initializeMap() {
        Map<String, CreditProgramType> map = new HashMap<>();
        map.put("noProgram", STANDARD);
        map.put("family", FAMILY);
        map.put("governmentSupport2020", PREFERENTIAL);
        map.put("it", IT);
        map.put("military", MILITARY);
        map.put("izhsHozsposopom", IGC);

        return map;
    }

    private static final Map<String, CreditProgramType> BY_VALUE = new HashMap<>();

    static {
        for (CreditProgramType type : values()) {
            BY_VALUE.put(type.value, type);
        }
    }

    public static CreditProgramType fromString(String value) {
        return BY_VALUE.getOrDefault(value, STANDARD);
    }

    public static CreditProgramType getCreditProgramTypeByCian(String objectType) {
        return objectTypeMap.get(objectType);
    }
}
