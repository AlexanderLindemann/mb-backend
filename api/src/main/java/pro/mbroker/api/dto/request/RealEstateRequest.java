package pro.mbroker.api.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.RegionType;

import java.util.UUID;

@Getter
@Setter
@ToString
public class RealEstateRequest {
    private UUID id;

    private String residentialComplexName;

    private RegionType region;

    private String address;

    private Integer cianId;

    private Boolean active;

}
