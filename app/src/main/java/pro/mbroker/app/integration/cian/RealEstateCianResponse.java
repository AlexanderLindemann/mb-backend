package pro.mbroker.app.integration.cian;

import lombok.Getter;
import lombok.Setter;
import pro.mbroker.app.integration.cian.response.BuilderDto;
import pro.mbroker.app.integration.cian.response.RegionDto;

import java.util.List;

@Getter
@Setter
public class RealEstateCianResponse {
    private String name;
    private String fullAddress;
    private Integer id; //cianId
    private RegionDto region;
    private List<BuilderDto> builders;
}
