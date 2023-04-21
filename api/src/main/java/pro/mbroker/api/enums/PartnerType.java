package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "PartnerType", name = "Тип партнера")
public enum PartnerType implements EnumWithValue<String> {
    DEVELOPER("DEVELOPER", "Застройщик"),
    REAL_ESTATE_AGENCY("REAL_ESTATE_AGENCY", "Агентство недвижимости"),
    PRIVATE_BROKER("PRIVATE_BROKER", "Частный брокер");

    private final String value;
    private final String name;
}
