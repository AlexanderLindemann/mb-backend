package pro.mbroker.api.dto.request;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.PartnerType;
import pro.mbroker.api.enums.RealEstateType;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class PartnerRequest {
    private Integer smartDealOrganizationId;
    @NonNull
    private String name;
    @NonNull
    private PartnerType type;
    @NonNull
    private List<RealEstateType> realEstateType;
    @NonNull
    private List<CreditPurposeType> creditPurposeType;

    private List<RealEstateRequest> realEstateRequest;
    @NonNull
    private List<UUID> bankCreditProgram;

    private Integer cianId;
}
