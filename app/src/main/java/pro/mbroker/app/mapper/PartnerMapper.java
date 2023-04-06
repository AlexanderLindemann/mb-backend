package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pro.mbroker.api.dto.request.PartnerRequest;
import pro.mbroker.api.dto.response.PartnerResponse;
import pro.mbroker.app.entity.Partner;

@Mapper
public interface PartnerMapper {
    @Mapping(target = "bankCreditProgram", ignore = true)
    @Mapping(target = "creditPurposeType", ignore = true)
    @Mapping(target = "realEstateType", ignore = true)
    @Mapping(target = "bank", ignore = true)
    PartnerResponse toPartnerResponseMapper(Partner partner);

    @Mapping(target = "bank", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creditPrograms", ignore = true)
    @Mapping(target = "realEstateType", ignore = true)
    @Mapping(target = "creditPurposeType", ignore = true)
    Partner toPartnerMapper(PartnerRequest request);
}