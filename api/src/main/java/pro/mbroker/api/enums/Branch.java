package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "Branch", name = "Отрасль организации")
public enum Branch {
    AGRICULTURE("AGRICULTURE", "Сельское хозяйство"),
    AUTOMOTIVE("AUTOMOTIVE", "Автомобильная промышленность"),
    BANKING("BANKING", "Банковская деятельность"),
    CONSTRUCTION("CONSTRUCTION", "Строительство"),
    CONSULTING("CONSULTING", "Консалтинг"),
    CONSUMER_GOODS("CONSUMER_GOODS", "Потребительские товары"),
    EDUCATION("EDUCATION", "Образование"),
    ENERGY("ENERGY", "Энергетика"),
    ENTERTAINMENT("ENTERTAINMENT", "Развлечения"),
    FINANCE("FINANCE", "Финансы"),
    HEALTHCARE("HEALTHCARE", "Здравоохранение"),
    HOSPITALITY("HOSPITALITY", "Гостеприимство"),
    INSURANCE("INSURANCE", "Страхование"),
    IT("IT", "Информационные технологии"),
    LEGAL("LEGAL", "Юриспруденция"),
    MANUFACTURING("MANUFACTURING", "Производство"),
    MEDIA("MEDIA", "СМИ"),
    PHARMACEUTICAL("PHARMACEUTICAL", "Фармацевтика"),
    REAL_ESTATE("REAL_ESTATE", "Недвижимость"),
    RETAIL("RETAIL", "Розничная торговля"),
    TELECOMMUNICATIONS("TELECOMMUNICATIONS", "Телекоммуникации"),
    TRANSPORTATION("TRANSPORTATION", "Транспорт"),
    TRAVEL("TRAVEL", "Туризм");

    private final String value;
    private final String name;
}


