package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "NumberOfEmployees", name = "Количество сотрудников")
public enum NumberOfEmployees implements EnumWithValue<String> {
    LESS_THAN_10("lessThan10", "до 10"),
    FROM_10_TO_50("from10to50", "от 10 до 50"),
    FROM_50_TO_100("from50to100", "от 50 до 100"),
    OVER_100("over100", "Больше 100");

    private final String value;
    private final String name;
}


