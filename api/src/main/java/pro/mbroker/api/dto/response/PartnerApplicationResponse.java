package pro.mbroker.api.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.dto.BankWithBankApplicationDto;
import pro.mbroker.api.dto.MortgageCalculationDto;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.Insurance;
import pro.mbroker.api.enums.PartnerApplicationStatus;
import pro.mbroker.api.enums.PaymentSource;
import pro.mbroker.api.enums.RealEstateType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class PartnerApplicationResponse {
    private UUID id;

    private BigDecimal maternalCapitalAmount;

    private BigDecimal subsidyAmount;

    private List<PaymentSource> paymentSource;

    private List<Insurance> insurances;

    private CreditPurposeType creditPurposeType;

    private List<RealEstateType> realEstateTypes;

    private RealEstateResponse realEstate;

    private MortgageCalculationDto mortgageCalculation;

    private PartnerApplicationStatus status;

    private List<BankWithBankApplicationDto> bankWithBankApplicationDto;

    private List<BorrowerProfileResponse> borrowerProfiles;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
