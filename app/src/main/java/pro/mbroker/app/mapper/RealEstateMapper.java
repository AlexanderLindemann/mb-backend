package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pro.mbroker.api.dto.request.RealEstateRequest;
import pro.mbroker.api.dto.response.RealEstateResponse;
import pro.mbroker.app.entity.RealEstate;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface RealEstateMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "partner", ignore = true)
    RealEstate toRealEstateMapper(RealEstateRequest address);

    RealEstateResponse toRealEstateResponseMapper(RealEstate address);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "partner", ignore = true)
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
}