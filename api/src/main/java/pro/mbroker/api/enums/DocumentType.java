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
    BORROWER_SNILS("BORROWER_SNILS", "СНИЛС заёмщика"),
    CERTIFIED_COPY_TK("CERTIFIED_COPY_TK", "Заверенная копия ТК или трудового договора"),
    INCOME_CERTIFICATE("INCOME_CERTIFICATE", "Справка о доходах"),
    FOREIGN_PASSPORT_OR_ID_CARD("FOREIGN_PASSPORT_OR_ID_CARD", "Паспорт иностранного гражданина / Карточка ID"),
    MILITARY_ID("MILITARY_ID", "Военный билет"),
    PENSION_CERTIFICATE("PENSION_CERTIFICATE", "Пенсионное удостоверение"),
    EGRUL_EGRIP_EXTRACT("EGRUL_EGRIP_EXTRACT", "Выписка из ЕГРЮЛ / ЕГРИП"),
    OTHER_DOCUMENTS("OTHER_DOCUMENTS", "Прочие документы");

    private final String value;
    private final String name;
}
