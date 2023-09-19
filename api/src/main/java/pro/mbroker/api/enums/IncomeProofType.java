package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IncomeProofType {
    _2NDFL("2NDFL", "2-НДФЛ"),
    BANK_REFERENCE("bankReference", "Справка банка"),
    FULL_PACKAGE("fullPackage", "Полный пакет (бизнес)"),
    NO_CONFIRMATION("noConfirmation", "Без подтверждения");

    private final String value;
    private final String name;
}




