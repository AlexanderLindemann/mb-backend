package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "PartnerApplicationStatus", name = "Статус заявки партнера")
public enum PartnerApplicationStatus implements EnumWithValue<String> {
    UPLOADING_DOCUMENTS("UPLOADING_DOCUMENTS", "Загрузка документов"),
    IN_REVIEW("SENDING_PREPERATION", "Отправка заявки в банки"),
    APPROVED("CREDIT_APPROVED", "Кредит выдан"),
    REJECTED("EXPIRED", "Заявка просрочена");


    private final String value;
    private final String name;
}

