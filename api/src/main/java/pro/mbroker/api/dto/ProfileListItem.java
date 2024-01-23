package pro.mbroker.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class ProfileListItem {

    private String id;
    //номер
    private String number;

    //клиент фио
    private String fio;

    //автор заявки
    private UserReference createdBy;

    //стоимость недвижимости
    private BigDecimal propertyPrice;

    //сумма кредита
    private BigDecimal mortgageSum;

    //процент первоначального взноса
    private BigDecimal firstPay;

    //срок кредита
    private long durationMonths;

    //тип ипотеки
    private String type;
}
