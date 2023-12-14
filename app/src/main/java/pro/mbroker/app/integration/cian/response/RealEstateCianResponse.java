package pro.mbroker.app.integration.cian.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import pro.mbroker.app.integration.cian.CiansRealEstate;

import java.util.List;

@Getter
@Setter
public class RealEstateCianResponse {
    @JsonProperty("newbuildings")
    List<CiansRealEstate> newBuildings;
}
