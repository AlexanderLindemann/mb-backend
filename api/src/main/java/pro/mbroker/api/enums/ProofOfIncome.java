package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "ProofOfIncome", name = "Вид подтверждения дохода")
public enum ProofOfIncome implements EnumWithValue<String> {
    TWO_NDFL("TWO_NDFL", "2-НДФЛ"),
    BANK_REFERENCE("BANK_REFERENCE", "Справка банка"),
    FULL_PACKAGE("FULL_PACKAGE", "Полный пакет (бизнес)"),
    NO_CONFIRMATION("NO_CONFIRMATION", "Без подтверждения");

    private final String value;
    private final String name;

    public static final List<ProofOfIncome> REQUIRED_INCOME_PROF_TYPES =
            Arrays.asList(TWO_NDFL, BANK_REFERENCE, FULL_PACKAGE);
}



