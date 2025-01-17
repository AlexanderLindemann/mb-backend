package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pro.mbroker.api.dto.request.PartnerApplicationRequest;
import pro.mbroker.api.dto.response.PartnerApplicationResponse;
import pro.mbroker.app.entity.PartnerApplication;

import java.util.List;

@Mapper(config = ProgramMapperConfig.class, uses = {RealEstateMapper.class, BankApplicationMapper.class, MortgageCalculationMapper.class, BorrowerProfileMapper.class, PartnerContactMapper.class})
public interface PartnerApplicationMapper {

    @Mapping(target = "bankWithBankApplicationDto", ignore = true)
    @Mapping(target = "paymentSource", ignore = true)
    @Mapping(target = "realEstateTypes", ignore = true)
    @Mapping(target = "insurances", ignore = true)
    @Mapping(source = "partnerApplicationStatus", target = "status")
    @Mapping(source = "partner.id", target = "partnerId")
    @Mapping(source = "partner.name", target = "partnerName")
    @Mapping(source = "partner.partnerContacts", target = "contacts")
    @Mapping(source = "partner.smartDealOrganizationId", target = "smartDealOrganizationId")
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
    @Mapping(target = "paymentSource", ignore = true)
    @Mapping(target = "realEstateTypes", ignore = true)
    @Mapping(target = "insurances", ignore = true)
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
    @Mapping(target = "paymentSource", ignore = true)
    @Mapping(target = "realEstateTypes", ignore = true)
    @Mapping(target = "insurances", ignore = true)
    void updatePartnerApplicationFromRequest(PartnerApplicationRequest request, @MappingTarget PartnerApplication partnerApplication);
}