package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import pro.mbroker.api.dto.BorrowerVehicleDto;
import pro.mbroker.app.entity.BorrowerVehicle;

import java.util.Optional;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface BorrowerVehicleMapper {

    default void updateBorrowerVehicleFromDto(BorrowerVehicleDto dto, @MappingTarget BorrowerVehicle borrowerVehicle) {
        if (dto == null || borrowerVehicle == null) {
            return;
        }

        Optional.ofNullable(dto.getModel()).ifPresent(borrowerVehicle::setModel);
        Optional.ofNullable(dto.getYearOfManufacture()).ifPresent(borrowerVehicle::setYearOfManufacture);
        Optional.ofNullable(dto.getPrice()).ifPresent(borrowerVehicle::setPrice);
        Optional.ofNullable(dto.getIsCollateral()).ifPresent(borrowerVehicle::setIsCollateral);
        Optional.ofNullable(dto.getBasisOfOwnership()).ifPresent(borrowerVehicle::setBasisOfOwnership);
    }
}

