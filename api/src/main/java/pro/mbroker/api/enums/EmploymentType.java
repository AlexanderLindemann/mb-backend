package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "EmploymentType", name = "Тип работы персоны")
public enum EmploymentType implements EnumWithValue<String> {
    EMPLOYEE("EMPLOYEE", "Наёмный работник"),
    MILITARY("MILITARY", "Военнослужащий"),
    UNEMPLOYED("UNEMPLOYED", "Неработающий"),
    RETIREE("RETIREE", "Пенсионер"),
    UNEMPLOYED_RETIREE("UNEMPLOYED_RETIREE", "Неработающий пенсионер"),
    STUDENT("STUDENT", "Студент"),
    BUSINESS_OWNER("BUSINESS_OWNER", "Собственник бизнеса / Доля в бизнесе"),
    SOLE_PROPRIETOR("SOLE_PROPRIETOR", "ИП"),
    SELF_EMPLOYED("SELF_EMPLOYED", "Самозанятый");

    private final String value;
    private final String name;

}

