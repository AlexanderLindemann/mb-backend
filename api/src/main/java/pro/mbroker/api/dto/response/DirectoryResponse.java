package pro.mbroker.api.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.EnumDescription;

@Getter
@Setter
@Builder
@ToString
public class DirectoryResponse {
    private EnumDescription employmentTypes;
    private EnumDescription realEstateTypes;
    private EnumDescription creditPurposeTypes;

}
