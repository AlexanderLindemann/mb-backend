package pro.mbroker.api.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.RegionType;

import java.util.UUID;

@Getter
@Setter
@ToString
public class RealEstateResponse {

    private UUID id;

    private String residentialComplexName;

    private RegionType region;

    private String address;

    private Long cianId;
}
