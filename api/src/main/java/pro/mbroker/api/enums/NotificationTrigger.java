package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "NotificationTrigger", name = "Триггеры для отправки оповещения контакту")
public enum NotificationTrigger implements EnumWithValue<String> {
    BORROWER_SIGNED_FORM("BORROWER_SIGNED_FORM", "Заёмщики подписали анкету"),
    NEW_MORTGAGE_APPLICATION("NEW_MORTGAGE_APPLICATION", "Новая заявка на ипотеку"),
    UNDERWRITING_STATUS_CHANGED("UNDERWRITING_STATUS_CHANGED", "Изменился статус по андеррайтингу банка");


    private final String value;
    private final String name;
}