package pro.mbroker.api.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.PartnerType;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.api.enums.RegionType;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class PartnerResponse {
    private UUID id;

    private String partnerName;

    private PartnerType partnerType;

    private List<RealEstateType> realEstateType;

    private String residentialComplexName;

    private RegionType region;

    private String address;

    private List<CreditPurposeType> creditPurposeType;

    private BankResponse bank;

    private List<CreditProgramResponse> bankCreditProgram;
}
