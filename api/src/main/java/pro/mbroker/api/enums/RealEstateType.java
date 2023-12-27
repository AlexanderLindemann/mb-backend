package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private static final Map<String, RealEstateType> objectTypeMap = initializeMap();

    private static Map<String, RealEstateType> initializeMap() {
        Map<String, RealEstateType> map = new HashMap<>();
        map.put("flat", APARTMENT);
        map.put("apartments", APARTMENT_COMPLEX);
        map.put("house", HOUSE_WITH_LAND);
        return map;
    }


    public static List<RealEstateType> getAll() {
        return Arrays.asList(RealEstateType.values());
    }

    public static List<String> getAllNames() {
        return Arrays.stream(RealEstateType.values())
                .map(RealEstateType::getName)
                .collect(Collectors.toList());
    }

    public static RealEstateType getRealEstateTypeByCianObjectType(String objectType) {

        return objectTypeMap.get(objectType);
    }
}

