package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmploymentType implements DirectoryEnumMarker {
    EMPLOYEE("EMPLOYEE", "Наёмный работник", null),
    MILITARY("MILITARY", "Военнослужащий", null),
    UNEMPLOYED("UNEMPLOYED", "Неработающий", null),
    RETIREE("RETIREE", "Пенсионер", null),
    UNEMPLOYED_RETIREE("UNEMPLOYED_RETIREE", "Неработающий пенсионер", null),
    STUDENT("STUDENT", "Студент", null),
    BUSINESS_OWNER("BUSINESS_OWNER", "Собственник бизнеса / Доля в бизнесе", null),
    SOLE_PROPRIETOR("SOLE_PROPRIETOR", "ИП", null),
    SELF_EMPLOYED("SELF_EMPLOYED", "Самозанятый", null);

    private final String code;
    private final String name;
    private final String description;

}

