package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "Gender", name = "Пол")
public enum Gender implements EnumWithValue<String> {
    MALE("MALE", "Мужской"),
    FEMALE("FEMALE", "Женский");
    private final String value;
    private final String name;
}



