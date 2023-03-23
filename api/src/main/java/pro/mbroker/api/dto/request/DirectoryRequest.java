package pro.mbroker.api.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class DirectoryRequest {
    private List<String> employmentTypeCodes;
    private List<String> realEstateTypeCodes;
    private List<String> creditPurposeTypeCodes;

}
