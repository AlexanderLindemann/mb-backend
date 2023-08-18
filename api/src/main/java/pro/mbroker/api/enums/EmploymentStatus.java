package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "EmploymentStatus", name = "Статус занятости")
public enum EmploymentStatus implements EnumWithValue<String> {
    EMPLOYEE("EMPLOYEE", "Наёмный работник"),
    MILITARY("MILITARY", "Военнослужащий"),
    UNEMPLOYED("UNEMPLOYED", "Неработающий"),
    PENSIONER("PENSIONER", "Пенсионер"),
    BUSINESS_OWNER("BUSINESS_OWNER", "Владелец бизнеса"),
    ENTREPRENEUR("ENTREPRENEUR", "ИП"),
    SELF_EMPLOYED("SELF_EMPLOYED", "Самозанятый");

    private final String value;
    private final String name;
}


