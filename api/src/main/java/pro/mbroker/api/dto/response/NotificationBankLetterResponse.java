package pro.mbroker.api.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.RealEstateType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
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
    private Integer applicationNumber;
    private List<AttachmentInfo> attachmentInfo;
    private String partnerName;
    private String residentialComplexName;
    private String address;
    @JsonIgnore
    private RealEstateType realEstateType;
    private String realEstateTypeName;
    @JsonIgnore
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

    public NotificationBankLetterResponse(UUID id,
                                          Integer applicationNumber,
                                          UUID borrowerId,
                                          String partnerName,
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
                                          String middleName) {
        this.id = id;
        this.applicationNumber = applicationNumber;
        this.borrowerId = borrowerId;
        this.partnerName = partnerName;
        this.residentialComplexName = residentialComplexName;
        this.address = address;
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
        this.creditPurposeTypeName = creditPurposeType.getName();
    }
}
