package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "DocumentType", name = "Тип документа")
public enum DocumentType implements EnumWithValue<String> {
    BORROWER_PASSPORT("BORROWER_PASSPORT", "Паспорт заемщика"),
    APPLICATION_FORM("APPLICATION_FORM", "Анкета"),
    DATA_PROCESSING_AGREEMENT("DATA_PROCESSING_AGREEMENT", "Согласие на обработку персональных данных"),
    BORROWER_SNILS("BORROWER_SNILS", "СНИЛС заёмщика");

    private final String value;
    private final String name;
}
