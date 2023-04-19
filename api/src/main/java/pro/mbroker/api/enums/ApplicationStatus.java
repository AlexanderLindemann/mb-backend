package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "ApplicationStatus", name = "Статус заявки")
public enum ApplicationStatus implements EnumWithValue<String> {
    UPLOADING_DOCUMENTS("UPLOADING_DOCUMENTS", "Загрузка документов", null),
    IN_REVIEW("IN_REVIEW", "На рассмотрении", null),
    APPROVED("APPROVED", "Одобрено", null),
    REJECTED("REJECTED", "Отклонено", null);


    private final String value;
    private final String name;
    private final String description;
}

