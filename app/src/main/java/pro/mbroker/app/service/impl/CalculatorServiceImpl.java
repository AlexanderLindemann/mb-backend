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
    private static final int MONTHS_IN_YEAR = 12;
    private static final int PERCENTAGE_MAX = 100;
    private final RealEstateRepository realEstateRepository;
    private final DirectoryService directoryService;

    @Override
    @Transactional
    public PropertyMortgageDTO getCreditOffer(CalculatorRequest request) {
        List<CreditProgram> creditPrograms = filterCreditPrograms(request);
        Map<UUID, BankLoanProgramDto> bankLoanProgramDtoMap = new HashMap<>();
        for (CreditProgram creditProgram : creditPrograms) {
            bankLoanProgramDtoMap.computeIfAbsent(creditProgram.getBank().getId(), bankLoanProgram ->
                    createBankLoanProgramDto(creditProgram)).getLoanProgramCalculationDto().add(createLoanProgramCalculationDto(request, creditProgram));
        }
        return new PropertyMortgageDTO()
                .setRealEstatePrice(request.getRealEstatePrice())
                .setMonthCreditTerm(Optional.ofNullable(request.getCreditTerm())
                        .orElse(0) * MONTHS_IN_YEAR)
                .setDownPayment(request.getDownPayment())
                .setBankLoanProgramDto(new ArrayList<>(bankLoanProgramDtoMap.values()));
    }

    private BankLoanProgramDto createBankLoanProgramDto(CreditProgram creditProgram) {
        return new BankLoanProgramDto()
                .setBankName(creditProgram.getBank().getName());
    }

    private LoanProgramCalculationDto createLoanProgramCalculationDto(CalculatorRequest request, CreditProgram creditProgram) {
        BigDecimal mortgageSum = getMortgageSum(request.getRealEstatePrice(), request.getDownPayment());
        BigDecimal calculateMonthlyPayment = calculateMonthlyPayment(mortgageSum, creditProgram.getBaseRate(), request.getCreditTerm() * MONTHS_IN_YEAR);
        BigDecimal downPayment = request.getDownPayment() != null ? request.getDownPayment() : BigDecimal.ZERO;
        return new LoanProgramCalculationDto()
                .setCreditProgramId(creditProgram.getId())
                .setCreditProgramName(creditProgram.getProgramName())
                .setCalculatedRate(creditProgram.getBaseRate())
                .setMonthlyPayment(calculateMonthlyPayment)
                .setOverpayment(calculateOverpayment(calculateMonthlyPayment, request.getCreditTerm() * MONTHS_IN_YEAR, request.getRealEstatePrice(), downPayment));
    }

    private List<CreditProgram> filterCreditPrograms(CalculatorRequest request) {
        List<CreditProgram> creditPrograms = realEstateRepository.findCreditProgramsWithDetailsAndParametersByRealEstateId(request.getRealEstateId());
        return creditPrograms.stream()
                .filter(creditProgram -> isProgramEligible(request, creditProgram))
                .collect(Collectors.toList());
    }

    private boolean isProgramEligible(CalculatorRequest request, CreditProgram creditProgram) {
        BigDecimal mortgageSum = getMortgageSum(request.getRealEstatePrice(), request.getDownPayment());
        int downPaymentPercentage = calculateDownPaymentPercentage(request.getDownPayment(), request.getRealEstatePrice());
        String creditPurposeType = creditProgram.getCreditProgramDetail().getCreditPurposeType();
        String realEstateType = creditProgram.getCreditProgramDetail().getRealEstateType();
        List<CreditPurposeType> creditPurposeTypes = Converter.convertStringListToEnumList(creditPurposeType, CreditPurposeType.class);
        List<RealEstateType> realEstateTypes = Converter.convertStringListToEnumList(realEstateType, RealEstateType.class);
        int creditTermMonths = Optional.ofNullable(request.getCreditTerm())
                .orElse(0) * MONTHS_IN_YEAR;
        return creditPurposeTypes.contains(request.getCreditPurposeType()) &&
                realEstateTypes.contains(request.getRealEstateType()) &&
                mortgageSum.compareTo(creditProgram.getCreditParameter().getMinMortgageSum()) >= 0 &&
                mortgageSum.compareTo(creditProgram.getCreditParameter().getMaxMortgageSum()) <= 0 &&
                creditProgram.getCreditParameter().getMinCreditTerm() <= creditTermMonths &&
                creditProgram.getCreditParameter().getMaxCreditTerm() >= creditTermMonths &&
                downPaymentPercentage >= (creditProgram.getCreditParameter().getMinDownPayment()) &&
                downPaymentPercentage <= (creditProgram.getCreditParameter().getMaxDownPayment()) &&
                isRegionEligible(request, creditProgram);
    }

    private BigDecimal getMortgageSum(BigDecimal realEstatePrice, BigDecimal downPayment) {
        if (realEstatePrice == null || realEstatePrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return realEstatePrice.subtract(Optional.ofNullable(downPayment)
                .orElse(BigDecimal.ZERO));
    }

    @Override
    public BigDecimal calculateMonthlyPayment(BigDecimal mortgageSum, double annualInterestRate, int loanTermInMonths) {
        BigDecimal monthlyInterestRate = BigDecimal.valueOf(annualInterestRate).divide(BigDecimal.valueOf(1200), 8, RoundingMode.HALF_UP);
        BigDecimal numerator = monthlyInterestRate.add(BigDecimal.ONE).pow(loanTermInMonths).multiply(monthlyInterestRate);
        BigDecimal denominator = monthlyInterestRate.add(BigDecimal.ONE).pow(loanTermInMonths).subtract(BigDecimal.ONE);
        BigDecimal monthlyPayment = mortgageSum.multiply(numerator.divide(denominator, RoundingMode.HALF_UP));
        return monthlyPayment.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateOverpayment(BigDecimal monthlyPayment, int loanTermMonths, BigDecimal realEstatePrice, BigDecimal downPayment) {
        BigDecimal totalPayment = monthlyPayment.multiply(BigDecimal.valueOf(loanTermMonths));
        BigDecimal overpayment = totalPayment.subtract(realEstatePrice).add(downPayment);
        return overpayment.setScale(2, RoundingMode.HALF_UP);
    }

    private int calculateDownPaymentPercentage(BigDecimal downPayment, BigDecimal realEstatePrice) {
        if (realEstatePrice == null || realEstatePrice.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Real estate price must be non-null and non-zero.");
        }
        if (downPayment == null) {
            downPayment = BigDecimal.ZERO;
        }
        BigDecimal downPaymentPercentage = downPayment.divide(realEstatePrice, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(PERCENTAGE_MAX));
        return downPaymentPercentage.intValue();
    }

    private boolean isRegionEligible(CalculatorRequest request, CreditProgram creditProgram) {
        RealEstate realEstate = realEstateRepository.findById(request.getRealEstateId())
                .orElseThrow(() -> new ItemNotFoundException(RealEstate.class, request.getRealEstateId()));
        List<RegionType> creditProgramIncludeRegionTypes = Converter.convertStringListToEnumList(creditProgram.getCreditProgramDetail().getInclude(), RegionType.class);
        List<RegionType> creditProgramExcludeRegionTypes = Converter.convertStringListToEnumList(creditProgram.getCreditProgramDetail().getExclude(), RegionType.class);

        List<EnumDescription> filteredRegion = directoryService.getFilteredRegion(creditProgramIncludeRegionTypes, creditProgramExcludeRegionTypes);
        return filteredRegion.stream()
                .flatMap(enumDescription -> enumDescription.getValues().stream())
                .map(EnumItemDescription::getCode)
                .anyMatch(code -> code.equals(realEstate.getRegion().getValue()));
    }
}