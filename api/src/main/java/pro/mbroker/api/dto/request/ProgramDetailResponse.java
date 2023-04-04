package pro.mbroker.api.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.api.enums.RegionType;

import java.util.List;

@Getter
@Setter
@ToString
public class ProgramDetailResponse {
    private List<CreditPurposeType> creditPurposeType;

    private List<RealEstateType> realEstateType;

    private List<RegionType> include;

    private List<RegionType> exclude;
}
