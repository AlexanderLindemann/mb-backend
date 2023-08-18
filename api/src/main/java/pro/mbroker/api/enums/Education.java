package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "Education", name = "Образование")
public enum Education implements EnumWithValue<String> {
    INCOMPLETE_SECONDARY("INCOMPLETE_SECONDARY", "Ниже среднего"),
    AVERAGE("AVERAGE", "Среднее"),
    INCOMPLETE_HIGHER("INCOMPLETE_HIGHER", "Незаконченное высшее"),
    HIGHER("HIGHER", "Высшее"),
    SEVERAL_HIGHER("SEVERAL_HIGHER", "Несколько высших"),
    PHD("PHD", "Учёная степень"),
    RUSSIAN_MBA("RUSSIAN_MBA", "Российское MBA"),
    INTERNATIONAL_MBA("INTERNATIONAL_MBA", "Инностранное MBA");

    private final String value;
    private final String name;
}

