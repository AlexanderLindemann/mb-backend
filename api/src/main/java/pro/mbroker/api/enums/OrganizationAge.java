package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "OrganizationAge", name = "Возраст организации")
public enum OrganizationAge implements EnumWithValue<String> {
    LESS_THAN_1("lessThan1", "Менее 1 года"),
    FROM_1_TO_5("from1to5", "от 1 до 5 лет"),
    OVER_5("over5", "Более 5 лет");

    private final String value;
    private final String name;
}

