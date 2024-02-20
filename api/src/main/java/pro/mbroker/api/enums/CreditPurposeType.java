package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private static final Map<String, CreditPurposeType> objectTypeMap = initializeMap();

    private static Map<String, CreditPurposeType> initializeMap() {
        Map<String, CreditPurposeType> map = new HashMap<>();
        map.put("newBuilding", PURCHASE_UNDER_CONSTRUCTION);
        map.put("secondary", PURCHASE_READY_HOUSE);
        map.put("refinancing", REFINANCING);

        return map;
    }

    public static List<CreditPurposeType> getAll() {
        return Arrays.asList(CreditPurposeType.values());
    }
    public static List<String> getAllNames() {
        return Arrays.stream(CreditPurposeType.values())
                .map(CreditPurposeType::getName)
                .collect(Collectors.toList());
    }

    public static CreditPurposeType getCreditPurposeTypeByCian(String objectType) {
        return objectTypeMap.get(objectType);
    }

    public static String getCreditPurposeTypeString (List<CreditPurposeType> creditPurposeTypes) {
        return creditPurposeTypes.stream()
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }
}

