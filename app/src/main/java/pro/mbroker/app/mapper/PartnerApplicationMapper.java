package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pro.mbroker.api.dto.request.PartnerApplicationRequest;
import pro.mbroker.api.dto.response.PartnerApplicationResponse;
import pro.mbroker.app.entity.PartnerApplication;

import java.util.List;

@Mapper(config = ProgramMapperConfig.class, uses = {RealEstateMapper.class, BorrowerApplicationMapper.class})
public interface PartnerApplicationMapper {

    PartnerApplicationResponse toPartnerApplicationResponse(PartnerApplication partnerApplication);

    List<PartnerApplicationResponse> toPartnerApplicationResponseList(List<PartnerApplication> partnerApplications);

    @Mapping(target = "partner", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "realEstate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "borrowerApplications", source = "borrowerApplications", qualifiedByName = "toBorrowerApplicationList")
    PartnerApplication toPartnerApplication(PartnerApplicationRequest request);

    @Mapping(target = "id", ignore = true)
    void updatePartnerApplication(PartnerApplication update, @MappingTarget PartnerApplication exist);
}