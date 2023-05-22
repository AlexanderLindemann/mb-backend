package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pro.mbroker.api.dto.request.BankApplicationRequest;
import pro.mbroker.api.dto.response.BankApplicationResponse;
import pro.mbroker.app.entity.BankApplication;

import java.util.List;

@Mapper(uses = BorrowerProfileMapper.class)
public interface BankApplicationMapper {

    @Mapping(source = "bankApplication.creditProgram.baseRate", target = "interestRate")
    @Mapping(target = "mortgageSum", ignore = true)
    @Mapping(target = "coBorrowers", ignore = true)
    @Mapping(source = "creditProgram.id", target = "creditProgramId")
    BankApplicationResponse toBankApplicationResponse(BankApplication bankApplication);

    List<BankApplicationResponse> toBorrowerApplicationDtoList(List<BankApplication> bankApplications);

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
    @Mapping(target = "mainBorrower", ignore = true)
    BankApplication toBankApplication(BankApplicationRequest dto);

    @Named("toBankApplicationList")
    @Mapping(target = "id", ignore = true)
    List<BankApplication> toBankApplicationList(List<BankApplicationRequest> dtos);
}
