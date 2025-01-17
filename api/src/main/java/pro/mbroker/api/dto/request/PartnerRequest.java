package pro.mbroker.api.dto.request;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.PartnerType;
import pro.mbroker.api.enums.RealEstateType;

import java.util.List;
import java.util.Set;
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

    private List<RealEstateType> realEstateType;

    private List<CreditPurposeType> creditPurposeType;

    private List<RealEstateRequest> realEstateRequest;

    private Set<UUID> bankCreditProgram;

    private Integer cianId;

    private List<PartnerContactRequest> contacts;
}
