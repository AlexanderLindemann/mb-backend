package pro.mbroker.api.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.RegionType;

@Getter
@Setter
@ToString
public class RealEstateAddressRequest {
    private String residentialComplexName;

    private RegionType region;

    private String address;

}
