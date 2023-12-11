package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "RealEstateType", name = "Тип недвижимости")
public enum RealEstateType implements EnumWithValue<String> {
    APARTMENT("APARTMENT", "Квартира"),
    APARTMENT_COMPLEX("APARTMENT_COMPLEX", "Апартаменты"),
    ROOM_SHARE("ROOM_SHARE", "Доля в квартире"),
    ROOM("ROOM", "Комната"),
    HOUSE_WITH_LAND("HOUSE_WITH_LAND", "Дом с землёй"),
    TOWNHOUSE("TOWNHOUSE", "Таунхаус"),
    PARKING_PLACE("PARKING_PLACE", "Машиноместо"),
    STORAGE_ROOM("STORAGE_ROOM", "Кладовка"),
    COMMERCIAL("COMMERCIAL", "Коммерческая недвижимость");

    private final String value;
    private final String name;

    public static List<RealEstateType> getAll() {
        return Arrays.asList(RealEstateType.values());
    }
    public static List<String> getAllNames() {
        return Arrays.stream(RealEstateType.values())
                .map(RealEstateType::getName)
                .collect(Collectors.toList());
    }
}

