package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "ApplicationStatus", name = "Статус заявки")
public enum ApplicationStatus implements EnumWithValue<String> {
    UPLOADING_DOCUMENTS("UPLOADING_DOCUMENTS", "Загрузка документов"),
    IN_REVIEW("IN_REVIEW", "На рассмотрении"),
    APPROVED("APPROVED", "Одобрено"),
    REJECTED("REJECTED", "Отклонено");


    private final String value;
    private final String name;
}

