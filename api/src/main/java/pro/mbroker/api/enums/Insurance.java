package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "Insurance", name = "Вид страхования")
public enum Insurance {
    REAL_ESTATE_INSURANCE("realEstateInsurance", "Страхование недвижимости"),
    LIFE_INSURANCE("lifeInsurance", "Страхование жизни и здоровья заемщиков"),
    TITLE_INSURANCE("titleInsurance", "Титульное страхование");

    private final String value;
    private final String name;
}




