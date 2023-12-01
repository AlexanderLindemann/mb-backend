package pro.mbroker.api.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.api.enums.RegionType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
@SuppressWarnings("PMD")

public class NotificationBankLetterResponse implements Serializable {

    private static final long serialVersionUID = -6639215001970950771L;

    @JsonIgnore
    private UUID id;
    @JsonIgnore
    private UUID borrowerId;
    private UUID partnerApplicationId;
    private Integer applicationNumber;
    private Set<Long> attachmentIds;
    private String partnerName;
    private RegionType regionType;
    private String residentialComplexName;
    private String address;
    private List<RealEstateType> realEstateTypes;
    private String realEstateTypeName;
    private CreditPurposeType creditPurposeType;
    private String programName;
    private BigDecimal realEstatePrice;
    private BigDecimal downPayment;
    private BigDecimal creditSize;
    private Integer monthCreditTerm;
    private String lastName;
    private String firstName;
    private String middleName;
    private List<String> emails;
    private String creditPurposeTypeName;
    private BorrowerResponse borrowerResponse;
    private UUID bankId;
    private String bankName;
    private String message = "";
    private String tokenBearer = "";


    public NotificationBankLetterResponse() {}

    //TODO надо вынести это в наш конвертер Converter (util)
    public String convertCreditPurposeTypeToString(List<CreditPurposeType> creditPurposeTypes) {
        StringBuilder stringBuilder = new StringBuilder();
        creditPurposeTypes.forEach(el -> stringBuilder.append(el.getName()).append(", "));
        return stringBuilder.toString();
    }
}
