package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "MaritalStatus", name = "Семейное положение")
public enum MaritalStatus implements EnumWithValue<String> {
    MARRIED("MARRIED", "В браке"),
    CIVIL_MARRIAGE("CIVIL_MARRIAGE", "В гражданском браке"),
    SINGLE("SINGLE", "Холост / Не замужем"),
    DIVORCED("DIVORCED", "В разводе"),
    WIDOWER("WIDOWER", "Вдовец / Вдова");

    private final String value;
    private final String name;
}




