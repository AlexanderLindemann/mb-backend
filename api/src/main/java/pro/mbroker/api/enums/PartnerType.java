package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "PartnerType", name = "Тип партнера")
public enum PartnerType implements EnumWithValue<String> {
    DEVELOPER("DEVELOPER", "Застройщик", null),
    REAL_ESTATE_AGENCY("REAL_ESTATE_AGENCY", "Агентство недвижимости", null),
    PRIVATE_BROKER("PRIVATE_BROKER", "Частный брокер", null);

    private final String value;
    private final String name;
    private final String description;
}
