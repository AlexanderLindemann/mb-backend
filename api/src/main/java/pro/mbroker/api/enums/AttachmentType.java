package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "AttachmentType", name = "Тип документа attachment")
public enum AttachmentType implements EnumWithValue<String> {
    SIGNATURE_FORM("SIGNATURE_FORM", "подписанная анкета"),
    BORROWER_DOCUMENT("BORROWER_DOCUMENT", "документ клиента"),
    OTHER("OTHER", "прочий документ");


    private final String value;
    private final String name;
}

