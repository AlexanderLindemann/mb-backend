package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "TotalWorkExperience", name = "Общий стаж работы")
public enum TotalWorkExperience implements EnumWithValue<String> {
    FROM_1_TO_3("FROM_1_TO_3", "от 1 до 3 мес"),
    FROM_3_TO_6("FROM_3_TO_6", "от 3 до 6 мес"),
    FROM_6_TO_12("FROM_6_TO_12", "от 6 до 12 мес"),
    OVER_12("OVER_12", "более года");

    private final String value;
    private final String name;
}



