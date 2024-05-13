package pro.mbroker.api.dto.response.notification;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.dto.response.PartnerContactResponse;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.NotificationTrigger;
import pro.mbroker.api.enums.RealEstateType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
public class NotificationPartnerLetterResponse {

    private String residentialComplexName;
    private String address;
    private List<RealEstateType> realEstateTypes;
    private CreditPurposeType creditPurposeType;
    private Set<String> bankNames;
    private BigDecimal realEstatePrice;
    private BigDecimal downPayment;
    private BigDecimal creditSize;
    private Integer monthCreditTerm;
    private String lastName;
    private String firstName;
    private String middleName;
    private List<PartnerContactResponse> PartnerContacts;
    private String title;
    private String subject;
    private NotificationTrigger trigger;
}