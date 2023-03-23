package pro.mbroker.api.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.EmploymentType;
import pro.mbroker.api.enums.RealEstateType;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class DirectoryResponse {
    private List<EmploymentType> employmentTypes;
    private List<RealEstateType> realEstateTypes;
    private List<CreditPurposeType> creditPurposeTypes;

}
