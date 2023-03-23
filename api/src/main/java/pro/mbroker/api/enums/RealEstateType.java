package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RealEstateType implements DirectoryEnumMarker {
    APARTMENT("APARTMENT", "Квартира", null),
    APARTMENT_COMPLEX("APARTMENT_COMPLEX", "Апартаменты", null),
    ROOM_SHARE("ROOM_SHARE", "Доля в квартире", null),
    ROOM("ROOM", "Комната", null),
    HOUSE_WITH_LAND("HOUSE_WITH_LAND", "Дом с землёй", null),
    TOWNHOUSE("TOWNHOUSE", "Таунхаус", null),
    COMMERCIAL("COMMERCIAL", "Коммерческая недвижимость", null);

    private final String code;
    private final String name;
    private final String description;
}

