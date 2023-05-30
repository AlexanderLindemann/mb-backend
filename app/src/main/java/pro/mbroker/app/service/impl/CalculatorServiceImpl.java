package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
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
import pro.mbroker.app.repository.BankRepository;
import pro.mbroker.app.repository.RealEstateRepository;
import pro.mbroker.app.service.CalculatorService;
import pro.mbroker.app.service.DirectoryService;
import pro.mbroker.app.util.Converter;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
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
    private final AttachmentServiceImpl attachmentService;
    private final BankRepository bankRepository;

    @Override
    @Transactional
    public PropertyMortgageDTO getCreditOffer(CalculatorRequest request) {
        List<CreditProgram> creditPrograms = filterCreditPrograms(request);
        Map<UUID, BankLoanProgramDto> bankLoanProgramDtoMap = new HashMap<>();
        for (CreditProgram creditProgram : creditPrograms) {
            bankLoanProgramDtoMap.computeIfAbsent(creditProgram.getBank().getId(),
                    bankLoanProgram ->
                            createBankLoanProgramDto(creditProgram)).getLoanProgramCalculationDto().add(createLoanProgramCalculationDto(request, creditProgram));
        }
        return new PropertyMortgageDTO()
                .setRealEstatePrice(request.getRealEstatePrice())
                .setMonthCreditTerm(Optional.ofNullable(request.getCreditTerm())
                        .orElse(0) * MONTHS_IN_YEAR)
                .setDownPayment(request.getDownPayment())
                .setBankLoanProgramDto(new ArrayList<>(bankLoanProgramDtoMap.values()));
    }

    @Override
    public BigDecimal getMortgageSum(BigDecimal realEstatePrice, BigDecimal downPayment) {
        if (realEstatePrice == null || realEstatePrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return realEstatePrice.subtract(Optional.ofNullable(downPayment)
                .orElse(BigDecimal.ZERO));
    }

    private BankLoanProgramDto createBankLoanProgramDto(CreditProgram creditProgram) {
        if (creditProgram == null || creditProgram.getBank() == null) {
            log.error("Credit program or its bank is null");
            throw new IllegalArgumentException("Credit program and its bank cannot be null");
        }
        BankLoanProgramDto bankLoanProgramDtoBuilder = new BankLoanProgramDto()
                .setBankName(creditProgram.getBank().getName());
        Long logoId = bankRepository.findExternalStorageIdByBankName(creditProgram.getBank().getName());
        if (Objects.nonNull(logoId)) {
            bankLoanProgramDtoBuilder.setLogo(generateBase64FromLogo(logoId));
        }
        return bankLoanProgramDtoBuilder;
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
        List<CreditProgram> creditPrograms = realEstateRepository
                .findCreditProgramsWithDetailsAndParametersByRealEstateId(request.getRealEstateId(),
                                                                          LocalDateTime.now());
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
                isRegionEligible(request, creditProgram) &&
                isMaternalCapital(request, creditProgram);
    }

    @Override
    public BigDecimal calculateMonthlyPayment(BigDecimal mortgageSum, double annualInterestRate, int loanTermInMonths) {
        BigDecimal monthlyInterestRate = BigDecimal.valueOf(annualInterestRate).divide(BigDecimal.valueOf(1200), 8, RoundingMode.HALF_EVEN);
        BigDecimal base = monthlyInterestRate.add(BigDecimal.ONE).pow(loanTermInMonths);
        BigDecimal numerator = base.multiply(monthlyInterestRate);
        BigDecimal denominator = base.subtract(BigDecimal.ONE);
        BigDecimal monthlyPayment = mortgageSum.multiply(numerator.divide(denominator, 8, RoundingMode.HALF_EVEN));
        return monthlyPayment.setScale(2, RoundingMode.HALF_EVEN);
    }


    @Override
    public BigDecimal calculateOverpayment(BigDecimal monthlyPayment, int loanTermMonths, BigDecimal realEstatePrice, BigDecimal downPayment) {
        if (monthlyPayment == null || realEstatePrice == null || downPayment == null) {
            throw new IllegalArgumentException("Invalid input: all arguments must be non-null");
        }
        BigDecimal totalPayment = monthlyPayment.multiply(BigDecimal.valueOf(loanTermMonths));
        BigDecimal overpayment = totalPayment.subtract(realEstatePrice).add(downPayment);
        return overpayment.setScale(2, RoundingMode.HALF_EVEN);
    }


    private int calculateDownPaymentPercentage(BigDecimal downPayment, BigDecimal realEstatePrice) {
        if (realEstatePrice == null || realEstatePrice.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Real estate price must be non-null and non-zero.");
        }
        BigDecimal actualDownPayment = Optional.ofNullable(downPayment).orElse(BigDecimal.ZERO);
        BigDecimal downPaymentPercentage = actualDownPayment.divide(realEstatePrice, 4, RoundingMode.HALF_EVEN).multiply(new BigDecimal(PERCENTAGE_MAX));
        if (downPaymentPercentage.compareTo(new BigDecimal(Integer.MAX_VALUE)) > 0) {
            throw new ArithmeticException("Down payment percentage exceeds maximum int value");
        }
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

    private boolean isMaternalCapital(CalculatorRequest request, CreditProgram creditProgram) {
        return !request.getIsMaternalCapital() || creditProgram.getCreditParameter().getIsMaternalCapital().equals(request.getIsMaternalCapital());
    }

    private String generateBase64FromLogo(Long logoId) {
        try {
            MultipartFile logo = attachmentService.download(logoId);
            byte[] logoBytes = logo.getBytes();
            return Base64.getEncoder().encodeToString(logoBytes);
        } catch (IOException e) {
            log.error("Ошибка при обработке логотипа: {}", e.getMessage());
        }
        return null;
    }
}
