package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pro.mbroker.api.dto.request.BankApplicationRequest;
import pro.mbroker.api.dto.response.BankApplicationResponse;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.app.entity.BankApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(uses = BorrowerProfileMapper.class)
public interface BankApplicationMapper {

    @Mapping(source = "bankApplication.creditProgram.baseRate", target = "interestRate")
    @Mapping(target = "mortgageSum", ignore = true)
    @Mapping(target = "coBorrowers", ignore = true)
    @Mapping(source = "creditProgram.id", target = "creditProgramId")
    @Mapping(source = "creditProgram.programName", target = "programName")
    BankApplicationResponse toBankApplicationResponse(BankApplication bankApplication);

    List<BankApplicationResponse> toBorrowerApplicationDtoList(List<BankApplication> bankApplications);

    @Mapping(source = "dto.creditProgramId", target = "creditProgram.id")
    @Mapping(target = "partnerApplication", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bankApplicationStatus", ignore = true)
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

    default BankApplication updateBankApplicationFromRequest(BankApplication existing, BankApplicationRequest request) {
        if (existing == null) {
            existing = new BankApplication();
        }
        existing.setBankApplicationStatus(BankApplicationStatus.READY_TO_SENDING);
        existing.setMonthlyPayment(request.getMonthlyPayment());
        existing.setRealEstatePrice(request.getRealEstatePrice());
        existing.setDownPayment(request.getDownPayment());
        existing.setMonthCreditTerm(request.getMonthCreditTerm());
        existing.setOverpayment(request.getOverpayment());
        return existing;
    }

    default List<BankApplication> updateBankApplicationsFromRequests(List<BankApplication> existingList, List<BankApplicationRequest> requestList) {
        Map<UUID, BankApplication> existingMap = existingList.stream()
                .collect(Collectors.toMap(BankApplication::getId, Function.identity()));
        List<BankApplication> resultList = new ArrayList<>();
        for (BankApplicationRequest request : requestList) {
            BankApplication existing = existingMap.get(request.getId());
            if (existing == null) {
                existing = new BankApplication();
            }
            updateBankApplicationFromRequest(existing, request);
            resultList.add(existing);
        }
        return resultList;
    }

}
