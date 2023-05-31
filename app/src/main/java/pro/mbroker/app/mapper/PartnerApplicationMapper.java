package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import pro.mbroker.api.dto.request.PartnerApplicationRequest;
import pro.mbroker.api.dto.response.BankApplicationResponse;
import pro.mbroker.api.dto.response.BorrowerProfileResponse;
import pro.mbroker.api.dto.response.PartnerApplicationResponse;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.app.entity.BankApplication;
import pro.mbroker.app.entity.PartnerApplication;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mapper(config = ProgramMapperConfig.class, uses = {RealEstateMapper.class, BankApplicationMapper.class, MortgageCalculationMapper.class})
public interface PartnerApplicationMapper {

    @Mapping(source = "partnerApplication.bankApplications", target = "bankApplications", qualifiedByName = "mapBankApplications")
    @Mapping(source = "partnerApplication.mortgageCalculation.isMaternalCapital", target = "mortgageCalculation.isMaternalCapital")
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
    @Mapping(target = "bankApplications", source = "bankApplications", qualifiedByName = "toBankApplicationList")
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
    void updatePartnerApplicationFromRequest(PartnerApplicationRequest request, @MappingTarget PartnerApplication partnerApplication);

    @Named("mapBankApplications")
    default List<BankApplicationResponse> mapBankApplications(List<BankApplication> bankApplications) {
        List<BankApplicationResponse> bankApplicationResponses = new ArrayList<>();
        for (BankApplication bankApplication : bankApplications) {
            BankApplicationResponse bankApplicationResponse = new BankApplicationResponse();
            bankApplicationResponse.setProgramName(bankApplication.getCreditProgram().getProgramName());
            bankApplicationResponses.add(bankApplicationResponse);
        }
        return bankApplicationResponses;
    }

}