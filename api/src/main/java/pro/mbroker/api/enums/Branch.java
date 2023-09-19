package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "Branch", name = "Отрасль организации")
public enum Branch {
    RESCUE("RESCUE", "Аварийно-спасательные службы (МЧС)"),
    CAR("CAR", "Автодилеры / Автосервисы"),
    FINANCE("FINANCE", "Банки / Страхование / Финансы / Лизинг"),
    ARMED_FORCES("ARMED_FORCES", "Вооруженные силы (служба)"),
    PUBLIC("PUBLIC", "Государственное управление"),
    DESIGN("DESIGN", "Дизайн / Проектирование"),
    MINING("MINING", "Добывающая промышленность"),
    COMMUNAL("COMMUNAL", "Жилищно-коммунальное хозяйство (ЖКХ)"),
    MEDICINE("MEDICINE", "Здравоохранение"),
    GAMBLING("GAMBLING", "Игорный бизнес"),
    IT("IT", "Информационные технологии (IT)"),
    CONSULTING("CONSULTING", "Консалтинг / Аудит"),
    ART("ART", "Культура / Искусство / Шоу-бизнес"),
    FORESTRY("FORESTRY", "Лесное хозяйство"),
    PAWNSHOP("PAWNSHOP", "Ломбард"),
    ENGINEERING("ENGINEERING", "Машиностроение"),
    METALLURGY("METALLURGY", "Металлургия"),
    SCIENCE("SCIENCE", "Наука / Образование"),
    SECURITY("SECURITY", "Охранное / Детективное агентство"),
    PUBLISHING("PUBLISHING", "Печать / Издательское дело"),
    FOOD("FOOD", "Пищевая / Лёгкая промышленность"),
    HR("HR", "Подбор / Управление персоналом (HR)"),
    POLITICS("POLITICS", "Политические / Общественные организации"),
    ELECTRONIC("ELECTRONIC", "Приборостроение / Радиоэлектроника"),
    LAW("LAW", "Правоохранительная / Судебная система"),
    WEAPON("WEAPON", "Производство вооружения (ОПК)"),
    PR("PR", "Реклама / Маркетинг / PR"),
    REALTOR("REALTOR", "Риэлторская деятельность / Арендодатель"),
    SPORT("SPORT", "Салоны красоты / Спорт / Фитнес"),
    AGRICULTURE("AGRICULTURE", "Сельское хозяйство"),
    MASSMEDIA("MASSMEDIA", "СМИ"),
    SOCIAL("SOCIAL", "Социальное обеспечение"),
    BUILDING("BUILDING", "Строительство / Производство стройматериалов"),
    REPAIR("REPAIR", "Ремонтные / Бытовые / Коммунальные услуги"),
    CATERING("CATERING", "Рестораны / Кафе / Кейтеринг"),
    TAX("TAX", "Таможня / Налоговая полиция"),
    TELECOM("TELECOM", "Телекоммуникации / Связь"),
    ENERGY("ENERGY", "Топливно-энергетический комплекс (ТЭК)"),
    TRADE("TRADE", "Торговля оптовая / розничная"),
    TOURISM("TOURISM", "Туризм / Гостиничный бизнес / Развлечения"),
    CHEMICAL("CHEMICAL", "Фармацевтическая / Химическая индустрия"),
    JEWELRY("JEWELRY", "Ювелирное дело"),
    LEGAL("LEGAL", "Юридические услуги"),
    OTHER("OTHER", "Другое");

    private final String value;
    private final String name;
}



