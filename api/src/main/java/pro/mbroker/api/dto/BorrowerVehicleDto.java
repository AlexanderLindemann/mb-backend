package pro.mbroker.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.BasisOfOwnership;

@Getter
@Setter
@ToString
public class BorrowerVehicleDto {
    private String model;

    private BasisOfOwnership basisOfOwnership;

    private Integer yearOfManufacture;

    private Integer price;

    private Boolean isCollateral;

}
