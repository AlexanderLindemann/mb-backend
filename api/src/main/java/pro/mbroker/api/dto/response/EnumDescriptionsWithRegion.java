package pro.mbroker.api.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class EnumDescriptionsWithRegion {
    List<EnumDescription> enumDescription = new ArrayList<>();
    List<EnumRegion> enumRegions = new ArrayList<>();
}
