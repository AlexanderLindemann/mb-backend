package pro.mbroker.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.smartdeal.common.enums.DictionaryEnum;

@Getter
@RequiredArgsConstructor
@DictionaryEnum(code = "PaymentSource", name = "Источник финансирования")
public enum PaymentSource {
    SAVINGS("Savings", "Личные накопления"),
    MATERNAL_CAPITAL("maternalCapital", "Материнский капитал"),
    MILITARY_MORTGAGE("militaryMortgage", "Средства накопительно-ипотечной системы"),
    SUBSIDIES("subsidies", "Государственные субсидии"),
    APARTMENT_SALE("apartmentSale", "Средства от продажи уже имеющегося жилья"),
    RELATIVES_HELP("relativesHelp", "Помощь родственников");

    private final String value;
    private final String name;
}




