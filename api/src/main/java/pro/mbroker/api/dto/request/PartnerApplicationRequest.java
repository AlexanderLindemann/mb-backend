package pro.mbroker.api.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.dto.MortgageCalculationDto;
import pro.mbroker.api.enums.CreditProgramType;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.Insurance;
import pro.mbroker.api.enums.PaymentSource;
import pro.mbroker.api.enums.RealEstateType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class PartnerApplicationRequest {

    private BigDecimal maternalCapitalAmount;

    private BigDecimal subsidyAmount;

    private List<PaymentSource> paymentSource;

    private Insurance insurance;

    private CreditPurposeType creditPurposeType;

    private CreditProgramType creditProgramType;

    private RealEstateType realEstateType;

    private UUID realEstateId;

    private List<BankApplicationRequest> bankApplications;

    private BorrowerProfileRequest mainBorrower;

    private MortgageCalculationDto mortgageCalculation;
}
