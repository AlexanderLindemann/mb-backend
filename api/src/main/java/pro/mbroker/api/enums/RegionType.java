package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "Region", name = "Регион")
public enum RegionType implements EnumWithValue<String> {
    ADYGEA("ADYGEA", "Республика Адыгея (Адыгея)", null),
    ALTAY("ALTAY", "Алтайский край", null),
    ALTAI_REPUBLIC("ALTAI_REPUBLIC", "Республика Алтай", null),
    AMUR("AMUR", "Амурская область", null),
    ARKHANGELSK("ARKHANGELSK", "Архангельская область", null),
    ASTRAKHAN("ASTRAKHAN", "Астраханская область", null),
    BASHKORTOSTAN("BASHKORTOSTAN", "Республика Башкортостан", null),
    BELGOROD("BELGOROD", "Белгородская область", null),
    BRYANSK("BRYANSK", "Брянская область", null),
    BURYATIA("BURYATIA", "Республика Бурятия", null),
    CHELYABINSK("CHELYABINSK", "Челябинская область", null),
    CHECHNYA("CHECHNYA", "Чеченская Республика", null),
    CHUKOTKA("CHUKOTKA", "Чукотский автономный округ", null),
    CHUVASHIA("CHUVASHIA", "Чувашская Республика – Чувашия", null),
    DAGESTAN("DAGESTAN", "Республика Дагестан", null),
    INGUSHETIA("INGUSHETIA", "Республика Ингушетия", null),
    IRKUTSK("IRKUTSK", "Иркутская область", null),
    IVANOVO("IVANOVO", "Ивановская область", null),
    KABARDINO_BALKARIA("KABARDINO_BALKARIA", "Кабардино-Балкарская Республика", null),
    KALININGRAD("KALININGRAD", "Калининградская область", null),
    KALMYKIA("KALMYKIA", "Республика Калмыкия", null),
    KALUGA("KALUGA", "Калужская область", null),
    KAMCHATKA("KAMCHATKA", "Камчатский край", null),
    KARACHAY_CHERKESSIA("KARACHAY_CHERKESSIA", "Карачаево-Черкесская Республика", null),
    KARELIA("KARELIA", "Республика Карелия", null),
    KEMEROVO("KEMEROVO", "Кемеровская область", null),
    KHABAROVSK("KHABAROVSK", "Хабаровский край", null),
    KHAKASSIA("KHAKASSIA", "Республика Хакасия", null),
    KHANTY_MANSI("KHANTY_MANSI", "Ханты-Мансийский автономный округ — Югра", null),
    KIROV("KIROV", "Кировская область", null),
    KOMI("KOMI", "Республика Коми", null),
    KOSTROMA("KOSTROMA", "Костромская область", null),
    KRASNODAR("KRASNODAR", "Краснодарский край", null),
    KRASNOYARSK("KRASNOYARSK", "Красноярский край", null),
    KURSK("KURSK", "Курская область", null),
    LENINGRAD("LENINGRAD", "Ленинградская область", null),
    LIPECK("LIPECK", "Липецкая область", null),
    MAGADAN("MAGADAN", "Магаданская область", null),
    MARI_EL("MARI_EL", "Республика Марий Эл", null),
    MOSCOW("MOSCOW", "Москва", null),
    MOSCOW_REGION("MOSCOW_REGION", "Московская область", null),
    MORDOVIA("MORDOVIA", "Республика Мордовия", null),
    MURMANSK("MURMANSK", "Мурманская область", null),
    NIZHNY_NOVGOROD("NIZHNY_NOVGOROD", "Нижегородская область", null),
    NORTH_OSSETIA_ALANIA("NORTH_OSSETIA_ALANIA", "Республика Северная Осетия – Алания", null),
    NOVOSIBIRSK("NOVOSIBIRSK", "Новосибирская область", null),
    OMSK("OMSK", "Омская область", null),
    ORENBURG("ORENBURG", "Оренбургская область", null),
    OREL("OREL", "Орловская область", null),
    PENZA("PENZA", "Пензенская область", null),
    PERM("PERM", "Пермский край", null),
    PRIMORSKY("PRIMORSKY", "Приморский край", null),
    PSKOV("PSKOV", "Псковская область", null),
    ROSTOV("ROSTOV", "Ростовская область", null),
    RYAZAN("RYAZAN", "Рязанская область", null),
    SAKHA("SAKHA", "Республика Саха (Якутия)", null),
    SAKHALIN("SAKHALIN", "Сахалинская область", null),
    SAMARA("SAMARA", "Самарская область", null),
    SARATOV("SARATOV", "Саратовская область", null),
    SMOLENSK("SMOLENSK", "Смоленская область", null),
    STAVROPOL("STAVROPOL", "Ставропольский край", null),
    SVERDLOVSK("SVERDLOVSK", "Свердловская область", null),
    SAINT_PETERSBURG("SAINT_PETERSBURG", "Санкт-Петербург", null),
    TAMBOV("TAMBOV", "Тамбовская область", null),
    TATARSTAN("TATARSTAN", "Республика Татарстан (Татарстан)", null),
    TVER("TVER", "Тверская область", null),
    TOMSK("TOMSK", "Томская область", null),
    TULA("TULA", "Тульская область", null),
    TYUMEN("TYUMEN", "Тюменская область", null),
    UDMURTIA("UDMURTIA", "Удмуртская Республика", null),
    ULAN_UDE("ULAN_UDE", "Республика Бурятия (Улан-Удэ)", null),
    ULYANOVSK("ULYANOVSK", "Ульяновская область", null),
    KHANTY_MANSIYSK("KHANTY_MANSIYSK", "Ханты-Мансийский автономный округ — Югра", null),
    CHEBOKSARY("CHEBOKSARY", "Чувашская Республика (Чебоксары)", null),
    CHERKESSK("CHERKESSK", "Карачаево-Черкесская Республика (Черкесск)", null),
    CHITA("CHITA", "Забайкальский край (Чита)", null),
    YAROSLAVL("YAROSLAVL", "Ярославская область", null),
    SEVASTOPOL("SEVASTOPOL", "Севастополь", null),
    JEWISH_AUTONOMOUS_REGION("JEWISH_AUTONOMOUS_REGION", "Еврейская автономная область", null),
    CHUVASH_REPUBLIC("CHUVASH_REPUBLIC", "Чувашская Республика", null),
    CRIMEA("CRIMEA", "Республика Крым", null),
    NENETS_AUTONOMOUS_OKRUG("NENETS_AUTONOMOUS_OKRUG", "Ненецкий автономный округ", null),
    KOMI_PERMYAK("KOMI_PERMYAK", "Коми-Пермяцкий автономный округ", null),
    YAMALO_NENETS("YAMALO_NENETS", "Ямало-Ненецкий автономный округ", null),
    ZABAIKALSKY("ZABAIKALSKY", "Забайкальский край", null),
    REPUBLIC_OF_CRIMEA("REPUBLIC_OF_CRIMEA", "Республика Крым", null),
    VOLOGDA("VOLOGDA", "Вологодская область", null),
    YAMAL_NENETS("YAMAL_NENETS", "Ямало-Ненецкий автономный округ", null),
    TUVA("TUVA", "Республика Тыва", null),
    VLADIMIR("VLADIMIR", "Владимирская область", null),
    VOLGOGRAD("VOLGOGRAD", "Волгоградская область", null),
    VORONEZH("VORONEZH", "Воронежская область", null),
    NORTH_OSSETIA("NORTH_OSSETIA", "Республика Северная Осетия-Алания", null);


    private final String value;
    private final String name;
    private final String description;

}
