package pro.mbroker.api.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.dto.MortgageCalculationDto;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.Insurance;
import pro.mbroker.api.enums.PartnerType;
import pro.mbroker.api.enums.PaymentSource;
import pro.mbroker.api.enums.RealEstateType;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@ToString
public class PartnerApplicationRequest {

    private String realEstateId;

    private String prefixLink;

    private Integer externalCreatorId;

    private PartnerType partnerType;

    private BigDecimal maternalCapitalAmount;

    private BigDecimal subsidyAmount;

    private List<PaymentSource> paymentSource;

    private List<Insurance> insurances;

    private CreditPurposeType creditPurposeType;

    private List<RealEstateType> realEstateTypes;

    private List<BankApplicationRequest> bankApplications;

    private BorrowerProfileRequest mainBorrower;

    private MortgageCalculationDto mortgageCalculation;
}
