package pro.mbroker.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.BasisOfOwnership;
import pro.mbroker.api.enums.RealEstateType;

@Getter
@Setter
@ToString
public class BorrowerRealEstateDto {
    private RealEstateType type;

    private BasisOfOwnership basisOfOwnership;

    private Integer share;

    private Float area;

    private Integer price;

    private String address;

    private Boolean isCollateral;

}
