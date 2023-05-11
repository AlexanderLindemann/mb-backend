package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pro.mbroker.api.dto.request.BorrowerApplicationRequest;
import pro.mbroker.api.dto.response.BorrowerApplicationResponse;
import pro.mbroker.app.entity.BorrowerApplication;

import java.util.List;

@Mapper
public interface BorrowerApplicationMapper {

    @Mapping(source = "borrowerApplication.creditProgram.bank.name", target = "bankName")
    @Mapping(source = "borrowerApplication.creditProgram.programName", target = "creditProgramName")
    @Mapping(source = "borrowerApplication.creditProgram.baseRate", target = "interestRate")
    BorrowerApplicationResponse toBorrowerApplicationDto(BorrowerApplication borrowerApplication);

    List<BorrowerApplicationResponse> toBorrowerApplicationDtoList(List<BorrowerApplication> borrowerApplications);

    @Mapping(source = "dto.creditProgramId", target = "creditProgram.id")
    @Mapping(target = "partnerApplication", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "applicationStatus", ignore = true)
    @Mapping(source = "dto.monthlyPayment", target = "monthlyPayment")
    @Mapping(source = "dto.downPayment", target = "downPayment")
    @Mapping(source = "dto.monthCreditTerm", target = "monthCreditTerm")
    @Mapping(source = "dto.overpayment", target = "overpayment")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    BorrowerApplication toBorrowerApplication(BorrowerApplicationRequest dto);

    @Named("toBorrowerApplicationList")
    @Mapping(target = "id", ignore = true)
    List<BorrowerApplication> toBorrowerApplicationList(List<BorrowerApplicationRequest> dtos);
}
