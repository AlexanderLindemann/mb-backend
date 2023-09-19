package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "FamilyRelation", name = "Степень родства с основным заёмщиком")
public enum FamilyRelation implements EnumWithValue<String> {
    HUSBAND_WIFE("HUSBAND_WIFE", "Супруг / Супруга"),
    FATHER_MOTHER("FATHER_MOTHER", "Отец / Мать"),
    SON_DAUGHTER("SON_DAUGHTER", "Сын / Дочь"),
    GRANDMOTHER_GRANDFATHER("GRANDMOTHER_GRANDFATHER", "Бабушка / Дедушка"),
    GRANDSON_GRANDDAUGHTER("GRANDSON_GRANDDAUGHTER", "Внук / Внучка"),
    BROTHER_SISTER("BROTHER_SISTER", "Брат / Сестра"),
    STEPSON_STEPDAUGHTER("STEPSON_STEPDAUGHTER", "Пасынок / Падчерица"),
    STEPFATHER_STEPMOTHER("STEPFATHER_STEPMOTHER", "Отчим / Мачеха"),
    ADOPTIVE_ADOPTED("ADOPTIVE_ADOPTED", "Усыновитель / Усыновленный");

    private final String value;
    private final String name;
}



