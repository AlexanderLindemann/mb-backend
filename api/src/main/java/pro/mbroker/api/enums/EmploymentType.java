package pro.mbroker.api.enums;

public enum EmploymentType {
    EMPLOYEE("Наёмный работник"),
    MILITARY("Военнослужащий"),
    UNEMPLOYED("Неработающий"),
    RETIREE("Пенсионер"),
    UNEMPLOYED_RETIREE("Неработающий пенсионер"),
    STUDENT("Студент"),
    BUSINESS_OWNER("Собственник бизнеса / Доля в бизнесе"),
    SOLE_PROPRIETOR("ИП"),
    SELF_EMPLOYED("Самозанятый");

    private final String name;

    EmploymentType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

