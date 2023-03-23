package pro.mbroker.api.enums;

public enum RealEstateType {
    APARTMENT("Квартира"),
    APARTMENT_COMPLEX("Апартаменты"),
    ROOM_SHARE("Доля в квартире"),
    ROOM("Комната"),
    HOUSE_WITH_LAND("Дом с землёй"),
    TOWNHOUSE("Таунхаус"),
    COMMERCIAL("Коммерческая недвижимость");

    private final String name;

    RealEstateType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

