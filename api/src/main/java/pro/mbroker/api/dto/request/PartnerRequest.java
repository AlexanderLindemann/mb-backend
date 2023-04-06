package pro.mbroker.api.dto.request;

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
public class PartnerRequest {
    private String partnerName;

    private PartnerType partnerType;

    private List<RealEstateType> realEstateTypes;

    private String residentialComplexName;

    private RegionType region;

    private String address;

    private List<CreditPurposeType> creditPurposeType;

    private UUID bankId;

    private List<UUID> bankCreditProgram;

}
