package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.PartnerApplicationController;
import pro.mbroker.api.dto.request.PartnerApplicationRequest;
import pro.mbroker.api.dto.response.BorrowerApplicationResponse;
import pro.mbroker.api.dto.response.PartnerApplicationResponse;
import pro.mbroker.app.entity.BorrowerApplication;
import pro.mbroker.app.entity.PartnerApplication;
import pro.mbroker.app.mapper.PartnerApplicationMapper;
import pro.mbroker.app.service.CalculatorService;
import pro.mbroker.app.service.PartnerApplicationService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class PartnerApplicationControllerImpl implements PartnerApplicationController {
    private final PartnerApplicationService partnerApplicationService;
    private final PartnerApplicationMapper partnerApplicationMapper;
    private final CalculatorService calculatorService;

    @Override
    public List<PartnerApplicationResponse> getAllPartnerApplication(int page, int size, String sortBy, String sortOrder) {
        return partnerApplicationService.getAllPartnerApplication(page, size, sortBy, sortOrder)
                .stream()
                .map(this::getCalculateMortgage)
                .collect(Collectors.toList());
    }

    @Override
    public PartnerApplicationResponse createPartnerApplication(PartnerApplicationRequest request) {
        PartnerApplication partnerApplication = partnerApplicationService.createPartnerApplication(request);
        return getCalculateMortgage(partnerApplication);
    }

    @Override
    public PartnerApplicationResponse updatePartnerApplication(UUID partnerApplicationId, PartnerApplicationRequest request) {
        PartnerApplication partnerApplication = partnerApplicationService.updatePartnerApplication(partnerApplicationId, request);
        return getCalculateMortgage(partnerApplication);
    }

    @Override
    @PreAuthorize("hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_ADMIN_ACCESS)")
    public void deletePartnerApplication(UUID partnerApplicationId) {
        partnerApplicationService.deletePartnerApplication(partnerApplicationId);
    }

    private PartnerApplicationResponse getCalculateMortgage(PartnerApplication partnerApplication) {
        PartnerApplicationResponse response = partnerApplicationMapper.toPartnerApplicationResponse(partnerApplication);
        List<BorrowerApplication> borrowerApplications = partnerApplication.getBorrowerApplications();
        IntStream.range(0, borrowerApplications.size())
                .forEach(i -> {
                    BorrowerApplication borrowerApplication = borrowerApplications.get(i);
                    BorrowerApplicationResponse borrowerApplicationResponse = response.getBorrowerApplications().get(i);
                    BigDecimal mortgageSum = calculatorService.getMortgageSum(borrowerApplication.getRealEstatePrice(), borrowerApplication.getDownPayment());
                    borrowerApplicationResponse.setMortgageSum(mortgageSum);
                });
        return response;
    }
}
