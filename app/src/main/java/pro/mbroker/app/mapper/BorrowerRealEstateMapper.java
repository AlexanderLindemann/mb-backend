package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import pro.mbroker.api.dto.BorrowerRealEstateDto;
import pro.mbroker.app.entity.BorrowerRealEstate;

import java.util.Optional;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface BorrowerRealEstateMapper {

    default void updateBorrowerRealEstateFromDto(BorrowerRealEstateDto dto, @MappingTarget BorrowerRealEstate realEstate) {
        if (dto == null || realEstate == null) {
            return;
        }

        Optional.ofNullable(dto.getAddress()).ifPresent(realEstate::setAddress);
        Optional.ofNullable(dto.getArea()).ifPresent(realEstate::setArea);
        Optional.ofNullable(dto.getShare()).ifPresent(realEstate::setShare);
        Optional.ofNullable(dto.getPrice()).ifPresent(realEstate::setPrice);
        Optional.ofNullable(dto.getType()).ifPresent(realEstate::setType);
        Optional.ofNullable(dto.getIsCollateral()).ifPresent(realEstate::setIsCollateral);
        Optional.ofNullable(dto.getBasisOfOwnership()).ifPresent(realEstate::setBasisOfOwnership);
    }
}

