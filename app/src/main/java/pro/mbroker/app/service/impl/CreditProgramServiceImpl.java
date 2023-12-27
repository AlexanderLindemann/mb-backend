package pro.mbroker.app.service.impl;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.dto.LoanDto;
import pro.mbroker.api.dto.request.BankProgramRequest;
import pro.mbroker.api.dto.request.CreditParameterResponse;
import pro.mbroker.api.enums.CreditProgramType;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.api.enums.RegionType;
import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.entity.CreditParameter;
import pro.mbroker.app.entity.CreditProgram;
import pro.mbroker.app.entity.CreditProgramDetail;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.mapper.CreditParameterMapper;
import pro.mbroker.app.mapper.CreditProgramDetailMapper;
import pro.mbroker.app.mapper.ProgramMapper;
import pro.mbroker.app.repository.CreditProgramRepository;
import pro.mbroker.app.repository.specification.CreditProgramSpecification;
import pro.mbroker.app.service.BankService;
import pro.mbroker.app.service.CreditProgramService;
import pro.mbroker.app.service.RegionService;
import pro.mbroker.app.util.CreditProgramConverter;

import java.io.FileReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditProgramServiceImpl implements CreditProgramService {
    private final CreditParameterMapper creditParameterMapper;
    private final CreditProgramRepository creditProgramRepository;
    private final ProgramMapper programMapper;
    private final CreditProgramDetailMapper creditProgramDetailMapper;
    private final BankService bankService;
    private final RegionService regionService;
    @Value("${program_path}")
    String filePath;


    @Override
    @Transactional
    public CreditProgram createCreditParameter(BankProgramRequest createCreditParameter, CreditProgramDetail creditProgramDetail) {
        createCreditParameter.resetEarliestTime();
        createCreditParameter.resetLatestTime();
        CreditProgram creditProgram = programMapper.toProgramMapper(createCreditParameter)
                .setBank(bankService.getBankById(createCreditParameter.getBankId()))
                .setCreditProgramDetail(creditProgramDetail)
                .setCreditParameter(creditParameterMapper.toCreditParameterMapper(createCreditParameter.getCreditParameter()));
        return creditProgramRepository.save(creditProgram);
    }

    @Override
    @Transactional(readOnly = true)
    public CreditProgram getProgramByCreditProgramId(UUID creditProgramId) {
        return getIsActiveCreditProgram(creditProgramId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CreditProgram> getProgramByCreditProgramIds(List<UUID> creditProgramIds) {
        return getPrograms(creditProgramIds);
    }

    @Override
    @Transactional
    public CreditProgram updateProgram(UUID creditProgramId, BankProgramRequest updateProgramRequest, CreditProgramDetail updateCreditProgramDetail) {
        CreditProgram creditProgram = creditProgramRepository.findById(creditProgramId)
                .orElseThrow(() -> new ItemNotFoundException(CreditProgram.class, creditProgramId));
        CreditParameter creditParameter = creditProgram.getCreditParameter();
        CreditParameter updateCreditParameter = creditParameterMapper.toCreditParameterMapper(updateProgramRequest.getCreditParameter());
        if (!creditParameter.equals(updateCreditParameter)) {
            creditParameterMapper.updateCreditParameter(updateProgramRequest.getCreditParameter(), creditParameter);
        }
        programMapper.updateProgramFromRequest(updateProgramRequest, creditProgram);
        creditProgram.setCreditParameter(creditParameter);
        if (!creditProgram.getBank().getId().equals(updateProgramRequest.getBankId())) {
            creditProgram.setBank(bankService.getBankById(updateProgramRequest.getBankId()));
        }
        CreditProgramDetail creditProgramDetailCurrent = creditProgram.getCreditProgramDetail();
        creditProgramDetailMapper.updateProgramDetail(updateCreditProgramDetail, creditProgramDetailCurrent);
        return creditProgramRepository.save(creditProgram);
    }

    @Override
    @Transactional
    public CreditProgram updateProgramFromCian(Integer cianId, BankProgramRequest updateProgramRequest, CreditProgramDetail updateCreditProgramDetail) {
        List<CreditProgram> programs = creditProgramRepository.findByCianIdWithMaxCreatedAt(cianId);
        Optional<CreditProgram> maxCreatedAtProgram = programs.stream()
                .max(Comparator.comparing(CreditProgram::getCreatedAt));

        CreditProgram creditProgram = maxCreatedAtProgram.orElse(null);

        if (creditProgram != null) {
            CreditParameter creditParameter = creditProgram.getCreditParameter();
            CreditParameter updateCreditParameter = creditParameterMapper.toCreditParameterMapper(updateProgramRequest.getCreditParameter());
            if (!creditParameter.equals(updateCreditParameter)) {
                creditParameterMapper.updateCreditParameter(updateProgramRequest.getCreditParameter(), creditParameter);
            }
            programMapper.updateProgramFromRequest(updateProgramRequest, creditProgram);
            creditProgram.setCreditParameter(creditParameter);
            if (!creditProgram.getBank().getId().equals(updateProgramRequest.getBankId())) {
                creditProgram.setBank(bankService.getBankById(updateProgramRequest.getBankId()));
            }
            CreditProgramDetail creditProgramDetailCurrent = creditProgram.getCreditProgramDetail();
            creditProgramDetailMapper.updateProgramDetail(updateCreditProgramDetail, creditProgramDetailCurrent);

            return creditProgramRepository.save(creditProgram);
        } else return null;

    }

    @Override
    @Transactional(readOnly = true)
    public List<CreditProgram> getProgramsByBankId(UUID bankId) {
        Specification<CreditProgram> specification = CreditProgramSpecification.creditProgramByBankIdAndIsActive(bankId);
        return creditProgramRepository.findAll(specification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CreditProgram> getAllCreditProgram(Pageable pageable) {
        return creditProgramRepository.findAllWithBankBy(pageable);
    }

    @Override
    @Transactional
    public void deleteCreditProgram(UUID creditProgramId) {
        CreditProgram program = getProgram(creditProgramId);
        program.setActive(false);
        creditProgramRepository.save(program);
    }

    private CreditProgram getIsActiveCreditProgram(UUID creditProgramId) {
        Specification<CreditProgram> specification = CreditProgramSpecification.creditProgramByIdAndIsActive(creditProgramId);
        return creditProgramRepository.findOne(specification)
                .orElseThrow(() -> new ItemNotFoundException(CreditProgram.class, creditProgramId));
    }

    private CreditProgram getProgram(UUID creditProgramId) {
        return creditProgramRepository.findById(creditProgramId)
                .orElseThrow(() -> new ItemNotFoundException(CreditProgram.class, creditProgramId));
    }

    private List<CreditProgram> getPrograms(List<UUID> creditProgramIds) {
        return creditProgramRepository.findAllByIdIn(creditProgramIds);
    }

    public List<CreditProgram> getProgramsWithDetail(List<UUID> creditProgramIds) {
        return creditProgramRepository.findByIdInWithCreditProgramDetail(creditProgramIds);
    }

    @Override
    public List<UUID> getAllCreditProgramIds() {
        return creditProgramRepository.findAllCreditProgramIds();
    }

    @Override
    public void loadCreditProgramFromCian() {
        try {
            String fullPath = System.getProperty("user.dir") + filePath;

            FileReader filereader = new FileReader(fullPath);

            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(1)
                    .build();
            List<String[]> allData = csvReader.readAll();
            List<LoanDto> programs = new ArrayList<>();
            Set<Integer> existedCreditProgramWithCianIds = creditProgramRepository.findAllCreditProgramCianIds();

            for (String[] data : allData) {
                LoanDto item = parseDataToLoanDto(data);
                programs.add(item);
            }
            for (LoanDto item : programs) {
                Integer cianId = item.getId();
                BankProgramRequest bankProgramRequest = mappingToBankProgramRequest(item);

                CreditProgramDetail creditProgramDetail = null;
                if (bankProgramRequest != null) {
                    creditProgramDetail = CreditProgramConverter.convertCreditDetailToStringFormat(bankProgramRequest);
                }
                if (creditProgramDetail != null) {
                    if (existedCreditProgramWithCianIds.contains(cianId)) {
                        updateProgramFromCian(cianId, bankProgramRequest, creditProgramDetail);
                    } else {
                        createCreditParameter(bankProgramRequest, creditProgramDetail);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BankProgramRequest mappingToBankProgramRequest(LoanDto cian) {
        Bank bank = bankService.findBankByCianId(cian.getBankId());

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS Z");

        LocalDateTime dateTime = LocalDateTime.parse(cian.getCreatedAt(), inputFormatter);

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        LocalDateTime createdAt = LocalDateTime.parse(dateTime.format(outputFormatter));
        CreditProgramType creditProgramType = parseCreditProgramTypeFromInput(cian.getMortgageType());

        if (bank == null) {
            return null;
        } //else todo создать автоматически банк. Но это не просто.
        return new BankProgramRequest ()
                .setBankId(bank.getId())
                .setCianId(cian.getId())
                .setProgramName(creditProgramType.getName())
                .setProgramStartDate(createdAt)
                .setProgramEndDate(createdAt.plusDays(30)) // поставила 30 дней уточнить где брать протухший день
                .setDescription("")
                .setFullDescription(cian.getFullDescription())
                .setCreditPurposeType(parseCreditPurposeTypeFromInput(cian.getRealEstateType(), cian.getMortgageType())) //ошибки тут нет у циана это называется RealEstateType
                .setRealEstateType(parseRealEstateTypesFromInput(cian.getObjectType()))
                .setCreditProgramType(creditProgramType)
                .setInclude(parseRegionTypeIncludeFromInput(cian.getRegionIds()))
                .setExclude(Collections.emptyList())
                .setCreditParameter(new CreditParameterResponse()
                        .setMinMortgageSum(new BigDecimal(cian.getLoanAmountMin()))
                        .setMaxMortgageSum(new BigDecimal(cian.getLoanAmountMin()))
                        .setMinCreditTerm(cian.getLoanTermMin())
                        .setMaxCreditTerm(cian.getLoanTermMax())
                        .setMinDownPayment(new BigDecimal(cian.getDownPaymentRateMin()))
                        .setMaxDownPayment(new BigDecimal(cian.getDownPaymentRateMax()))
                        .setIsMaternalCapital(true)
                        )
                .setBaseRate(cian.getBaseRate())
                .setSalaryClientInterestRate(cian.getBaseRate());//в выгрузке нет ставки для зп клиентов
    }

    private static LoanDto parseDataToLoanDto(String[] data) {
        return LoanDto.builder()
                .id(Integer.parseInt(data[0]))
                .bankId(Integer.parseInt(data[1]))
                .baseRate(Double.parseDouble(data[2]))
                .regionIds(data[3])
                .regionGroup(data[4])
                .mortgageType(data[5])
                .realEstateType(data[6])
                .objectType(data[7])
                .benefitProgram(data[8])
                .loanTermMin(Integer.parseInt(data[9]))
                .loanTermMax(Integer.parseInt(data[10]))
                .incomeConfirmation(data[11])
                .loanAmountMin(Long.parseLong(data[12]))
                .loanAmountMax(Long.parseLong(data[13]))
                .downPaymentRateMin(data[14])
                .downPaymentRateMax(data[15])
                .hasLifeInsurance(data[16])
                .isNotCitizen(data[17])
                .employmentType(data[18])
                .registrationType(data[19])
                .isActive(data[20])
                .createdAt(data[21])
                .shadowId(Long.parseLong(data[22]))
                .fullDescription(data[36])
                .build();
    }

    private static List<RealEstateType> parseRealEstateTypesFromInput(String input) {
        Pattern pattern = Pattern.compile("\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(input);
        List<RealEstateType> types = new ArrayList<>();

        if (matcher.find()) {
            String innerContent = matcher.group(1);
            String[] items = innerContent.split(",");
            var list = Arrays.asList(items);

            types = list.stream()
                    .map(RealEstateType::getRealEstateTypeByCianObjectType)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        return types;
    }

    private static List<CreditPurposeType> parseCreditPurposeTypeFromInput(String input, String mortgageType) {
        Pattern pattern = Pattern.compile("\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(input);
        List<CreditPurposeType> types = new ArrayList<>();

        if (matcher.find()) {
            String innerContent = matcher.group(1);
            String[] items = innerContent.split(",");
            var list = Arrays.asList(items);

            types = list.stream()
                    .map(CreditPurposeType::getCreditPurposeTypeByCian)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            types.add(CreditPurposeType.getCreditPurposeTypeByCian(mortgageType));
        }

        return types;
    }

    private static CreditProgramType parseCreditProgramTypeFromInput(String input) {
        Pattern pattern = Pattern.compile("\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(input);
        CreditProgramType type = CreditProgramType.STANDARD;

        if (matcher.find()) {
            String innerContent = matcher.group(1);
            String[] items = innerContent.split(",");

            var list = Arrays.asList(items);

            if (!list.isEmpty()) {
                type = CreditProgramType.getCreditProgramTypeByCian(list.get(0));
            }
        }

        return type;
    }

    private List<RegionType> parseRegionTypeIncludeFromInput(String input) {
        Pattern pattern = Pattern.compile("\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(input);
        List<RegionType> types = new ArrayList<>();

        if (matcher.find()) {
            String innerContent = matcher.group(1);
            String[] items = innerContent.split(",");

            List<Integer> integerList = Arrays.stream(items)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            types = regionService.getRegionTypesByCianIdIn(integerList);

        }

        return types;
    }

}
