package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "MarriageContract", name = "Брачный контракт")
public enum MarriageContract implements EnumWithValue<String> {
    YES("YES", "Да"),
    NO("NO", "Нет");

    private final String value;
    private final String name;
}




