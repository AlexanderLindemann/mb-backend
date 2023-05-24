package pro.mbroker.api.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.dto.request.BorrowerProfileRequest;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.RealEstateType;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class PartnerApplicationResponse {
    private UUID id;

    private CreditPurposeType creditPurposeType;

    private RealEstateType realEstateType;

    private RealEstateResponse realEstate;

    private List<BankApplicationResponse> bankApplications;

    private List<BorrowerProfileRequest> borrowerProfiles;
}
