package pro.mbroker.api.dto.response;

import lombok.Getter;
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
public class PartnerResponse {

    private UUID id;

    private Integer smartDealOrganizationId;

    private String name;

    private PartnerType type;

    private List<RealEstateType> realEstateType;

    private List<CreditPurposeType> creditPurposeType;

    private List<RealEstateResponse> realEstates;

    private List<CreditProgramResponse> bankCreditProgram;

    private List<PartnerContactResponse> contacts;

    private Integer cianId;
}
