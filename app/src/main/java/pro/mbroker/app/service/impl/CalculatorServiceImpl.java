package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.dto.BankLoanProgramDto;
import pro.mbroker.api.dto.LoanProgramCalculationDto;
import pro.mbroker.api.dto.PropertyMortgageDTO;
import pro.mbroker.api.dto.request.CalculatorRequest;
import pro.mbroker.api.dto.response.EnumDescription;
import pro.mbroker.api.dto.response.EnumItemDescription;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.api.enums.RegionType;
import pro.mbroker.app.entity.CreditProgram;
import pro.mbroker.app.entity.RealEstate;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.repository.RealEstateRepository;
import pro.mbroker.app.service.CalculatorService;
import pro.mbroker.app.service.DirectoryService;
import pro.mbroker.app.util.Converter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CalculatorServiceImpl implements CalculatorService {
    private final RealEstateRepository realEstateRepository;
    private final DirectoryService directoryService;

    @Override
    @Transactional
    public PropertyMortgageDTO getCreditOffer(CalculatorRequest request) {
        List<CreditProgram> creditPrograms = filterCreditPrograms(request);
        Map<UUID, BankLoanProgramDto> bankLoanProgramDtoMap = new HashMap<>();
        for (CreditProgram creditProgram : creditPrograms) {
            LoanProgramCalculationDto loanProgramCalculationDto = createLoanProgramCalculationDto(request, creditProgram);
            bankLoanProgramDtoMap.computeIfAbsent(creditProgram.getBank().getId(), bankLoanProgram ->
                    createBankLoanProgramDto(creditProgram)).getLoanProgramCalculationDto().add(loanProgramCalculationDto);
        }
        return new PropertyMortgageDTO()
                .setMortgageSum(request.getMortgageSum())
                .setCreditTerm(request.getCreditTerm())
                .setDownPayment(request.getDownPayment())
                .setBankLoanProgramDto(new ArrayList<>(bankLoanProgramDtoMap.values()));
    }

    private BankLoanProgramDto createBankLoanProgramDto(CreditProgram creditProgram) {
        return new BankLoanProgramDto()
                .setBankName(creditProgram.getBank().getName())
                .setLoanProgramCalculationDto(new ArrayList<>());
    }

    private LoanProgramCalculationDto createLoanProgramCalculationDto(CalculatorRequest request, CreditProgram creditProgram) {
        BigDecimal calculateMonthlyPayment = calculateMonthlyPayment(request.getMortgageSum(), request.getDownPayment(), creditProgram.getBaseRate(), request.getCreditTerm());
        return new LoanProgramCalculationDto()
                .setCalculatedRate(creditProgram.getBaseRate())
                .setMonthlyPayment(calculateMonthlyPayment)
                .setOverpayment(calculateOverpayment(calculateMonthlyPayment, request.getCreditTerm(), request.getMortgageSum()));
    }

    private List<CreditProgram> filterCreditPrograms(CalculatorRequest request) {
        List<CreditProgram> creditPrograms = realEstateRepository.findCreditProgramsWithDetailsAndParametersByRealEstateId(request.getRealEstateId());
        return creditPrograms.stream()
                .filter(creditProgram -> isProgramEligible(request, creditProgram))
                .collect(Collectors.toList());
    }

    private boolean isProgramEligible(CalculatorRequest request, CreditProgram creditProgram) {
        BigDecimal mortgageSum = request.getMortgageSum().subtract(request.getDownPayment());
        String creditPurposeType = creditProgram.getCreditProgramDetail().getCreditPurposeType();
        String realEstateType = creditProgram.getCreditProgramDetail().getRealEstateType();
        List<CreditPurposeType> creditPurposeTypes = Converter.convertStringListToEnumList(creditPurposeType, CreditPurposeType.class);
        List<RealEstateType> realEstateTypes = Converter.convertStringListToEnumList(realEstateType, RealEstateType.class);
        return creditPurposeTypes.contains(request.getCreditPurposeType()) &&
                realEstateTypes.contains(request.getRealEstateType()) &&
                mortgageSum.compareTo(creditProgram.getCreditParameter().getMaxMortgageSum()) <= 0 &&
                creditProgram.getCreditParameter().getMinCreditTerm() <= request.getCreditTerm() &&
                creditProgram.getCreditParameter().getMaxCreditTerm() >= request.getCreditTerm() &&
                isRegionEligible(request, creditProgram);
    }

    public BigDecimal calculateMonthlyPayment(BigDecimal loanAmount, BigDecimal downPayment, double annualInterestRate, int loanTermInMonths) {
        BigDecimal mortgageSum = loanAmount.subtract(downPayment);
        BigDecimal monthlyInterestRate = BigDecimal.valueOf(annualInterestRate).divide(BigDecimal.valueOf(1200), 8, RoundingMode.HALF_UP);
        BigDecimal numerator = monthlyInterestRate.add(BigDecimal.ONE).pow(loanTermInMonths).multiply(monthlyInterestRate);
        BigDecimal denominator = monthlyInterestRate.add(BigDecimal.ONE).pow(loanTermInMonths).subtract(BigDecimal.ONE);
        BigDecimal monthlyPayment = mortgageSum.multiply(numerator.divide(denominator, RoundingMode.HALF_UP));
        return monthlyPayment.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateOverpayment(BigDecimal monthlyPayment, int loanTermMonths, BigDecimal totalLoanAmount) {
        BigDecimal totalPayment = monthlyPayment.multiply(BigDecimal.valueOf(loanTermMonths));
        BigDecimal overpayment = totalPayment.subtract(totalLoanAmount);
        return overpayment.setScale(2, RoundingMode.HALF_UP);
    }

    private boolean isRegionEligible(CalculatorRequest request, CreditProgram creditProgram) {
        RealEstate realEstate = realEstateRepository.findById(request.getRealEstateId())
                .orElseThrow(() -> new ItemNotFoundException(RealEstate.class, request.getRealEstateId()));
        List<RegionType> creditProgramIncludeRegionTypes = Converter.convertStringListToEnumList(creditProgram.getCreditProgramDetail().getInclude(), RegionType.class);
        List<RegionType> creditProgramExcludeRegionTypes = Converter.convertStringListToEnumList(creditProgram.getCreditProgramDetail().getExclude(), RegionType.class);

        List<EnumDescription> filteredRegion = directoryService.getFilteredRegion(creditProgramIncludeRegionTypes, creditProgramExcludeRegionTypes);
        List<String> regionCodes = filteredRegion.stream()
                .flatMap(enumDescription -> enumDescription.getValues().stream())
                .map(EnumItemDescription::getCode)
                .collect(Collectors.toList());
        return regionCodes.contains(realEstate.getRegion().getValue());
    }
}
