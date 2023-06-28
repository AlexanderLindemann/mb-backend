package pro.mbroker.app.mapper;

import org.mapstruct.*;
import pro.mbroker.api.dto.request.BankApplicationRequest;
import pro.mbroker.api.dto.request.BankApplicationUpdateRequest;
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
    @Mapping(target = "creditTerm", ignore = true)
    @Mapping(source = "creditProgram.programName", target = "creditProgramName")
    @Mapping(source = "bankApplicationStatus", target = "status")
    BankApplicationResponse toBankApplicationResponse(BankApplication bankApplication);

    List<BankApplicationResponse> toBorrowerApplicationDtoList(List<BankApplication> bankApplications);

    @Mapping(source = "dto.creditProgramId", target = "creditProgram.id")
    @Mapping(target = "partnerApplication", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bankApplicationStatus", ignore = true)
    @Mapping(source = "dto.monthlyPayment", target = "monthlyPayment")
    @Mapping(source = "dto.downPayment", target = "downPayment")
    @Mapping(source = "dto.creditTerm", target = "monthCreditTerm")
    @Mapping(source = "dto.overpayment", target = "overpayment")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "mainBorrower", ignore = true)
    @Mapping(target = "applicationNumber", ignore = true)
    BankApplication toBankApplication(BankApplicationRequest dto);

    @Mapping(source = "dto.creditProgramId", target = "creditProgram.id")
    @Mapping(target = "partnerApplication", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bankApplicationStatus", ignore = true)
    @Mapping(source = "dto.monthlyPayment", target = "monthlyPayment")
    @Mapping(source = "dto.downPayment", target = "downPayment")
    @Mapping(source = "dto.creditTerm", target = "monthCreditTerm")
    @Mapping(source = "dto.overpayment", target = "overpayment")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "mainBorrower", ignore = true)
    @Mapping(target = "applicationNumber", ignore = true)
    BankApplication toBankApplication(BankApplicationUpdateRequest dto);

    @Named("toBankApplicationList")
    @Mapping(target = "id", ignore = true)
    List<BankApplication> toBankApplicationList(List<BankApplicationRequest> dtos);

    @AfterMapping
    default void calculateCreditTermInYears(@MappingTarget BankApplicationResponse response, BankApplication bankApplication) {
        if (bankApplication.getMonthCreditTerm() != null) {
            response.setCreditTerm(bankApplication.getMonthCreditTerm() / 12);
        }
    }

    default BankApplication updateBankApplicationFromRequest(BankApplication existing, BankApplicationRequest request) {
        if (existing == null) {
            existing = new BankApplication();
        }
        if (!existing.getBankApplicationStatus().equals(BankApplicationStatus.SENT_TO_BANK)) {
            existing.setBankApplicationStatus(BankApplicationStatus.DATA_NO_ENTERED);
        }
        existing.setMonthlyPayment(request.getMonthlyPayment());
        existing.setRealEstatePrice(request.getRealEstatePrice());
        existing.setDownPayment(request.getDownPayment());
        existing.setMonthCreditTerm(request.getCreditTerm() * 12);
        existing.setOverpayment(request.getOverpayment());
        existing.setActive(true);
        return existing;
    }

    default BankApplication updateBankApplicationFromRequest(BankApplication existing, BankApplicationUpdateRequest request) {
        if (existing == null) {
            existing = new BankApplication();
        }
        existing.setBankApplicationStatus(BankApplicationStatus.DATA_NO_ENTERED);
        existing.setMonthlyPayment(request.getMonthlyPayment());
        existing.setRealEstatePrice(request.getRealEstatePrice());
        existing.setDownPayment(request.getDownPayment());
        existing.setMonthCreditTerm(request.getCreditTerm() * 12);
        existing.setOverpayment(request.getOverpayment());
        existing.setActive(true);
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
