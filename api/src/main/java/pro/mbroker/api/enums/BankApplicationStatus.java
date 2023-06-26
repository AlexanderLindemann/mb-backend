package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "BankApplicationStatus", name = "Статус заявки банка")
public enum BankApplicationStatus implements EnumWithValue<String> {
    DATA_NO_ENTERED("DATA_NO_ENTERED", "Внесены не все данные"),
    READY_TO_SENDING("READY_TO_SENDING", "Готово к отправке"),
    SENT_TO_BANK("SENT_TO_BANK", "Отправлено в банк"),
    REFINEMENT("REFINEMENT", "На доработке"),
    APPLICATION_APPROVED("APPLICATION_APPROVED", "На доработке"),
    REJECTED("REJECTED", "Отказано"),
    EXPIRED("EXPIRED", "Просрочено"),
    CREDIT_APPROVED("CREDIT_APPROVED", "Кредит выдан"),
    SENDING_TO_BANK("SENDING_TO_BANK", "Отправляется в банк"),
    SENDING_ERROR("SENDING_ERROR", "Ошибка при отправке");


    private final String value;
    private final String name;
}

