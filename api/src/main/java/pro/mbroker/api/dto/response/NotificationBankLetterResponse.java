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
    private RealEstateType realEstateType;
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
    private String bankName;

    public NotificationBankLetterResponse(UUID id,
                                          UUID partnerApplicationId,
                                          Integer applicationNumber,
                                          UUID borrowerId,
                                          String partnerName,
                                          RegionType regionType,
                                          String residentialComplexName,
                                          String address,
                                          RealEstateType realEstateType,
                                          CreditPurposeType creditPurposeType,
                                          String programName,
                                          BigDecimal realEstatePrice,
                                          BigDecimal downPayment,
                                          Integer monthCreditTerm,
                                          String lastName,
                                          String firstName,
                                          String middleName,
                                          String bankName) {
        this.id = id;
        this.partnerApplicationId = partnerApplicationId;
        this.applicationNumber = applicationNumber;
        this.borrowerId = borrowerId;
        this.partnerName = partnerName;
        this.regionType = regionType;
        this.residentialComplexName = residentialComplexName;
        this.address = address;
        this.realEstateType = realEstateType;
        this.realEstateTypeName = realEstateType.getName();
        this.creditPurposeType = creditPurposeType;
        this.programName = programName;
        this.realEstatePrice = realEstatePrice;
        this.downPayment = downPayment;
        this.creditSize = realEstatePrice.subtract(downPayment);
        this.monthCreditTerm = monthCreditTerm;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.bankName = bankName;
    }

    public String convertCreditPurposeTypeToString(List<CreditPurposeType> creditPurposeTypes) {
        StringBuilder stringBuilder = new StringBuilder();
        creditPurposeTypes.forEach(el -> stringBuilder.append(el.getName()).append(", "));
        return stringBuilder.toString();
    }
}
