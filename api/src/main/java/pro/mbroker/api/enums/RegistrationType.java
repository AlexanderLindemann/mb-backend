package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "RegistrationType", name = "Регистрация")
public enum RegistrationType implements EnumWithValue<String> {
    PERMANENT("PERMANENT", "Постоянная"),
    TEMPORARY("TEMPORARY", "Временная");

    private final String value;
    private final String name;
}


