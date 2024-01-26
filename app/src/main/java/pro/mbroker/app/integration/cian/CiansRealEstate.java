package pro.mbroker.app.integration.cian;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import pro.mbroker.app.integration.cian.response.BuilderDto;
import pro.mbroker.app.integration.cian.response.RegionDto;
import pro.mbroker.app.integration.cian.response.SellerDto;

import java.util.List;

@Getter
@Setter
public class CiansRealEstate {
    private String name;
    @JsonProperty("fullAddress")
    private String fullAddress;
    private Integer id; //cianId
    private RegionDto region;
    private List<BuilderDto> builders;
    private List<SellerDto> sellers;
}
