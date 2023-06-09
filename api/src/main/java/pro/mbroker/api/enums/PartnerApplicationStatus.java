package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "PartnerApplicationStatus", name = "Статус заявки партнера")
public enum PartnerApplicationStatus implements EnumWithValue<String> {
    UPLOADING_DOCS("UPLOADING_DOCS", "Загрузка документов"),
    SENDING_PREPERATION("SENDING_PREPERATION", "Отправка заявки в банки"),
    CREDIT_APPROVED("CREDIT_APPROVED", "Кредит выдан"),
    EXPIRED("EXPIRED", "Заявка просрочена");


    private final String value;
    private final String name;
}

