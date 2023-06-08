package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pro.mbroker.api.dto.request.PartnerApplicationRequest;
import pro.mbroker.api.dto.response.PartnerApplicationResponse;
import pro.mbroker.app.entity.PartnerApplication;

import java.util.List;

@Mapper(config = ProgramMapperConfig.class, uses = {RealEstateMapper.class, BankApplicationMapper.class, MortgageCalculationMapper.class})
public interface PartnerApplicationMapper {
    @Mapping(target = "bankWithBankApplicationDto", ignore = true)
    PartnerApplicationResponse toPartnerApplicationResponse(PartnerApplication partnerApplication);

    List<PartnerApplicationResponse> toPartnerApplicationResponseList(List<PartnerApplication> partnerApplications);

    @Mapping(target = "partner", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "realEstate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "mortgageCalculation", ignore = true)
    @Mapping(target = "borrowerProfiles", ignore = true)
    @Mapping(target = "partnerApplicationStatus", ignore = true)
    @Mapping(target = "bankApplications", ignore = true)
    PartnerApplication toPartnerApplication(PartnerApplicationRequest request);

    @Mapping(target = "id", ignore = true)
    void updatePartnerApplication(PartnerApplication update, @MappingTarget PartnerApplication exist);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "partner", ignore = true)
    @Mapping(target = "realEstate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "bankApplications", ignore = true)
    @Mapping(target = "partnerApplicationStatus", ignore = true)
    @Mapping(target = "borrowerProfiles", ignore = true)
    @Mapping(target = "mortgageCalculation", ignore = true)
    void updatePartnerApplicationFromRequest(PartnerApplicationRequest request, @MappingTarget PartnerApplication partnerApplication);

}