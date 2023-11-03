package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "BorrowerProfileStatus", name = "Статус заявки профиля заемщика")
public enum BorrowerProfileStatus implements EnumWithValue<String> {
    DATA_NO_ENTERED("DATA_NO_ENTERED", "Внесены не все данные"),
    DATA_ENTERED("DATA_ENTERED", "Документы не подписаны"),
    DATA_UPDATED("DATA_UPDATED", "Документы не подписаны"),
    DOCS_SIGNED("DOCS_SIGNED", "Документы подписаны");


    private final String value;
    private final String name;
}

