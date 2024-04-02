package pro.mbroker.api.dto.request;

import lombok.Data;
import pro.mbroker.api.enums.CreditProgramType;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.api.enums.RegionType;

import java.util.Set;
import java.util.UUID;

@Data
public class CreditProgramServiceRequest {
    private int page;
    private int size;
    private String sortBy;
    private String sortOrder;
    private Set<UUID> banks;
    private String name;
    private Set<CreditProgramType> creditProgramTypes;
    private Set<CreditPurposeType> creditPurposeTypes;
    private Set<RealEstateType> realEstateTypes;
    private Set<RegionType> regions;
    private Integer cianId;
}