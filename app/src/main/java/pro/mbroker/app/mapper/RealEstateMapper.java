package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pro.mbroker.api.dto.request.RealEstateRequest;
import pro.mbroker.api.dto.response.RealEstateResponse;
import pro.mbroker.app.entity.RealEstate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface RealEstateMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "partner", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", source = "address.active")
    RealEstate toRealEstateMapper(RealEstateRequest address);

    RealEstateResponse toRealEstateResponseMapper(RealEstate address);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "partner", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(mapUpdatedAt())")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", source = "realEstateRequest.active")
    void updateRealEstateAddress(RealEstateRequest realEstateRequest, @MappingTarget RealEstate realEstate);

    @Mapping(target = "partner", ignore = true)
    void updateRealEstateAddressList(List<RealEstateRequest> realEstateRequest, @MappingTarget List<RealEstate> realEstate);

    default List<RealEstate> toRealEstateAddressList(List<RealEstateRequest> addressRequests) {
        return addressRequests.stream()
                .map(this::toRealEstateMapper)
                .collect(Collectors.toList());
    }

    default List<RealEstateResponse> toRealEstateAddressResponseList(List<RealEstate> realEstates) {
        return realEstates.stream()
                .map(this::toRealEstateResponseMapper)
                .collect(Collectors.toList());
    }

    default LocalDateTime mapUpdatedAt() {
        return LocalDateTime.now();
    }
}