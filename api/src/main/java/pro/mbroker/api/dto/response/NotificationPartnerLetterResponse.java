package pro.mbroker.api.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.RealEstateType;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@ToString
public class NotificationPartnerLetterResponse {

    private String residentialComplexName;
    private String address;
    private List<RealEstateType> realEstateTypes;
    private CreditPurposeType creditPurposeType;
    private List<String> bankNames;
    private BigDecimal realEstatePrice;
    private BigDecimal downPayment;
    private BigDecimal creditSize;
    private Integer monthCreditTerm;
    private String lastName;
    private String firstName;
    private String middleName;
    private List<String> emails;
}