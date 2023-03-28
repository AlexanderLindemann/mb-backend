package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "Region", name = "Регион")
public enum RegionType implements EnumWithValue<String> {
    BELGOROD("BELGOROD", "Белгородская область", null),
    BRYANSK("BRYANSK", "Брянская область", null),
    VLADIMIR("VLADIMIR", "Владимирская область", null),
    VOLGOGRAD("VOLGOGRAD", "Волгоградская область", null),
    VORONEZH("VORONEZH", "Воронежская область", null),
    IVANOVO("IVANOVO", "Ивановская область", null),
    IRKUTSK("IRKUTSK", "Иркутская область", null),
    KALININGRAD("KALININGRAD", "Калининградская область", null),
    KALUGA("KALUGA", "Калужская область", null),
    KEMEROVO("KEMEROVO", "Кемеровская область", null),
    KURSK("KURSK", "Курская область", null),
    LIPECK("LIPECK", "Липецкая область", null),
    MAGADAN("MAGADAN", "Магаданская область", null),
    MOSCOW("MOSCOW", "Москва", null),
    MOSCOW_REGION("MOSCOW_REGION", "Московская область", null),
    MURMANSK("MURMANSK", "Мурманская область", null),
    NIZHNY_NOVGOROD("NIZHNY_NOVGOROD", "Нижегородская область", null),
    NOVOSIBIRSK("NOVOSIBIRSK", "Новосибирская область", null),
    OMSK("OMSK", "Омская область", null),
    ORENBURG("ORENBURG", "Оренбургская область", null),
    OREL("OREL", "Орловская область", null),
    PENZA("PENZA", "Пензенская область", null),
    PERM("PERM", "Пермский край", null),
    PSKOV("PSKOV", "Псковская область", null),
    ROSTOV("ROSTOV", "Ростовская область", null),
    RYAZAN("RYAZAN", "Рязанская область", null),
    SAMARA("SAMARA", "Самарская область", null),
    SARATOV("SARATOV", "Саратовская область", null),
    SMOLENSK("SMOLENSK", "Смоленская область", null),
    STAVROPOL("STAVROPOL", "Ставропольский край", null),
    SVERDLOVSK("SVERDLOVSK", "Свердловская область", null),
    TAMBOV("TAMBOV", "Тамбовская область", null),
    TVER("TVER", "Тверская область", null),
    TOMSK("TOMSK", "Томская область", null),
    TULA("TULA", "Тульская область", null),
    TYUMEN("TYUMEN", "Тюменская область", null),
    ULYANOVSK("ULYANOVSK", "Ульяновская область", null),
    CHELYABINSK("CHELYABINSK", "Челябинская область", null),
    ZABAIKALSKY("ZABAIKALSKY", "Забайкальский край", null),
    YAROSLAVL("YAROSLAVL", "Ярославская область", null);

    private final String value;
    private final String name;
    private final String description;


}
