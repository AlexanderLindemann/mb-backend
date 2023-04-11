package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pro.mbroker.api.dto.request.RealEstateAddressRequest;
import pro.mbroker.api.dto.response.RealEstateAddressResponse;
import pro.mbroker.app.entity.RealEstateAddress;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface RealEstateAddressMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "partner", ignore = true)
    RealEstateAddress toRealEstateAddressMapper(RealEstateAddressRequest address);

    RealEstateAddressResponse toRealEstateAddressResponseMapper(RealEstateAddress address);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "partner", ignore = true)
    void updateRealEstateAddress(RealEstateAddressRequest realEstateAddressRequest, @MappingTarget RealEstateAddress realEstateAddress);

    default List<RealEstateAddress> toRealEstateAddressList(List<RealEstateAddressRequest> addressRequests) {
        return addressRequests.stream()
                .map(this::toRealEstateAddressMapper)
                .collect(Collectors.toList());
    }

    default List<RealEstateAddressResponse> toRealEstateAddressResponseList(List<RealEstateAddress> realEstateAddresses) {
        return realEstateAddresses.stream()
                .map(this::toRealEstateAddressResponseMapper)
                .collect(Collectors.toList());
    }
}