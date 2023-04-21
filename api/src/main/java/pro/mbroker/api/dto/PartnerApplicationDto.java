package pro.mbroker.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.dto.response.RealEstateResponse;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.RealEstateType;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class PartnerApplicationDto {
    private UUID id;

    private String borrowerFullName;

    private CreditPurposeType creditPurposeType;

    private RealEstateType realEstateType;

    private RealEstateResponse realEstate;

    private List<BorrowerApplicationDto> borrowerApplications;
}
