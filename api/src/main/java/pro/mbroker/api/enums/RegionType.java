package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "Region", name = "Регион")
public enum RegionType implements EnumWithValue<String> {
    ADYGEA("ADYGEA", "Республика Адыгея (Адыгея)"),
    ALTAY("ALTAY", "Алтайский край"),
    ALTAI_REPUBLIC("ALTAI_REPUBLIC", "Республика Алтай"),
    AMUR("AMUR", "Амурская область"),
    ARKHANGELSK("ARKHANGELSK", "Архангельская область"),
    ASTRAKHAN("ASTRAKHAN", "Астраханская область"),
    BASHKORTOSTAN("BASHKORTOSTAN", "Республика Башкортостан"),
    BELGOROD("BELGOROD", "Белгородская область"),
    BRYANSK("BRYANSK", "Брянская область"),
    BURYATIA("BURYATIA", "Республика Бурятия"),
    CHELYABINSK("CHELYABINSK", "Челябинская область"),
    CHECHNYA("CHECHNYA", "Чеченская Республика"),
    CHUKOTKA("CHUKOTKA", "Чукотский автономный округ"),
    DAGESTAN("DAGESTAN", "Республика Дагестан"),
    INGUSHETIA("INGUSHETIA", "Республика Ингушетия"),
    IRKUTSK("IRKUTSK", "Иркутская область"),
    IVANOVO("IVANOVO", "Ивановская область"),
    KABARDINO_BALKARIA("KABARDINO_BALKARIA", "Кабардино-Балкарская Республика"),
    KALININGRAD("KALININGRAD", "Калининградская область"),
    KALMYKIA("KALMYKIA", "Республика Калмыкия"),
    KALUGA("KALUGA", "Калужская область"),
    KAMCHATKA("KAMCHATKA", "Камчатский край"),
    KARACHAY_CHERKESSIA("KARACHAY_CHERKESSIA", "Карачаево-Черкесская Республика"),
    KARELIA("KARELIA", "Республика Карелия"),
    KEMEROVO("KEMEROVO", "Кемеровская область"),
    KHABAROVSK("KHABAROVSK", "Хабаровский край"),
    KHAKASSIA("KHAKASSIA", "Республика Хакасия"),
    KIROV("KIROV", "Кировская область"),
    KOMI("KOMI", "Республика Коми"),
    KOSTROMA("KOSTROMA", "Костромская область"),
    KRASNODAR("KRASNODAR", "Краснодарский край"),
    KRASNOYARSK("KRASNOYARSK", "Красноярский край"),
    KURGANSKAYA_OBL("KURGANSKAYA_OBL", "Курганская область"),
    KURSK("KURSK", "Курская область"),
    LENINGRAD("LENINGRAD", "Ленинградская область"),
    LIPECK("LIPECK", "Липецкая область"),
    MAGADAN("MAGADAN", "Магаданская область"),
    MARI_EL("MARI_EL", "Республика Марий Эл"),
    MOSCOW("MOSCOW", "Москва"),
    MOSCOW_REGION("MOSCOW_REGION", "Московская область"),
    MORDOVIA("MORDOVIA", "Республика Мордовия"),
    MURMANSK("MURMANSK", "Мурманская область"),
    NIZHNY_NOVGOROD("NIZHNY_NOVGOROD", "Нижегородская область"),
    NOVGOROD("NOVGOROD", "Новгородская область"),
    NOVOSIBIRSK("NOVOSIBIRSK", "Новосибирская область"),
    OMSK("OMSK", "Омская область"),
    ORENBURG("ORENBURG", "Оренбургская область"),
    OREL("OREL", "Орловская область"),
    PENZA("PENZA", "Пензенская область"),
    PERM("PERM", "Пермский край"),
    PRIMORSKY("PRIMORSKY", "Приморский край"),
    PSKOV("PSKOV", "Псковская область"),
    ROSTOV("ROSTOV", "Ростовская область"),
    RYAZAN("RYAZAN", "Рязанская область"),
    SAKHA("SAKHA", "Республика Саха (Якутия)"),
    SAKHALIN("SAKHALIN", "Сахалинская область"),
    SAMARA("SAMARA", "Самарская область"),
    SARATOV("SARATOV", "Саратовская область"),
    SMOLENSK("SMOLENSK", "Смоленская область"),
    STAVROPOL("STAVROPOL", "Ставропольский край"),
    SVERDLOVSK("SVERDLOVSK", "Свердловская область"),
    SAINT_PETERSBURG("SAINT_PETERSBURG", "Санкт-Петербург"),
    TAMBOV("TAMBOV", "Тамбовская область"),
    TATARSTAN("TATARSTAN", "Республика Татарстан"),
    TVER("TVER", "Тверская область"),
    TOMSK("TOMSK", "Томская область"),
    TULA("TULA", "Тульская область"),
    TYUMEN("TYUMEN", "Тюменская область"),
    UDMURTIA("UDMURTIA", "Удмуртская Республика"),
    ULYANOVSK("ULYANOVSK", "Ульяновская область"),
    KHANTY_MANSIYSK("KHANTY_MANSIYSK", "Ханты-Мансийский автономный округ — Югра"),
    YAROSLAVL("YAROSLAVL", "Ярославская область"),
    SEVASTOPOL("SEVASTOPOL", "Севастополь"),
    JEWISH_AUTONOMOUS_REGION("JEWISH_AUTONOMOUS_REGION", "Еврейская автономная область"),
    CHUVASH_REPUBLIC("CHUVASH_REPUBLIC", "Чувашская Республика"),
    NENETS_AUTONOMOUS_OKRUG("NENETS_AUTONOMOUS_OKRUG", "Ненецкий автономный округ"),
    KOMI_PERMYAK("KOMI_PERMYAK", "Коми-Пермяцкий автономный округ"),
    YAMALO_NENETS("YAMALO_NENETS", "Ямало-Ненецкий автономный округ"),
    ZABAIKALSKY("ZABAIKALSKY", "Забайкальский край"),
    REPUBLIC_OF_CRIMEA("REPUBLIC_OF_CRIMEA", "Республика Крым"),
    VOLOGDA("VOLOGDA", "Вологодская область"),
    TUVA("TUVA", "Республика Тыва"),
    VLADIMIR("VLADIMIR", "Владимирская область"),
    VOLGOGRAD("VOLGOGRAD", "Волгоградская область"),
    VORONEZH("VORONEZH", "Воронежская область"),
    NORTH_OSSETIA("NORTH_OSSETIA", "Республика Северная Осетия-Алания");


    private final String value;
    private final String name;

    public static RegionType getByName(String name) {
        for (RegionType regionType : RegionType.values()) {
            if (regionType.getName().equalsIgnoreCase(name)) {
                return regionType;
            }
        }
        throw new IllegalArgumentException("No such region for Russian name: " + name);
    }

    public static String getRegionTypesString (List<RegionType> regionTypes) {
        if (!regionTypes.isEmpty()) {
            return regionTypes.stream()
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
        } else {
         log.error("regionTypes is empty");
         return "";
        }
    }
}
