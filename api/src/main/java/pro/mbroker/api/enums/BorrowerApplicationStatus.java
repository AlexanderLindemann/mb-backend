package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "BorrowerApplicationStatus", name = "Статус заявки профиля заемщика")
public enum BorrowerApplicationStatus implements EnumWithValue<String> {
    DATA_NO_ENTERED("DATA_NO_ENTERED", "Внесены не все данные"),
    IN_REVIEW("DATA_ENTERED", "Внесены все данные");


    private final String value;
    private final String name;
}

