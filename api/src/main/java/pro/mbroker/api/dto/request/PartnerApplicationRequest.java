package pro.mbroker.api.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.RealEstateType;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class PartnerApplicationRequest {

    private String borrowerFullName;

    private CreditPurposeType creditPurposeType;

    private RealEstateType realEstateType;

    private UUID realEstateId;

    private List<BankApplicationRequest> bankApplications;
}
