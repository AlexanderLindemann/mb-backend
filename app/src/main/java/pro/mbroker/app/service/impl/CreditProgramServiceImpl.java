package pro.mbroker.app.service.impl;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import pro.mbroker.app.mapper.CreditProgramMapper;
import pro.mbroker.app.repository.CreditProgramRepository;
import pro.mbroker.app.repository.specification.CreditProgramSpecification;
import pro.mbroker.app.service.BankService;
import pro.mbroker.app.service.CianLoadingFilesService;
import pro.mbroker.app.service.CreditProgramService;
import pro.mbroker.app.service.RegionService;
import pro.mbroker.app.util.CreditProgramConverter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditProgramServiceImpl implements CreditProgramService {
    private final CreditParameterMapper creditParameterMapper;
    private final CreditProgramRepository creditProgramRepository;
    private final CreditProgramMapper creditProgramMapper;
    private final CreditProgramDetailMapper creditProgramDetailMapper;
    private final BankService bankService;
    private final RegionService regionService;
    private final CianLoadingFilesService cianLoadingFilesService;
    @Value("${cian.credit-program.loading_credit_program.scheduled.enabled}")
    private boolean loadingCreditProgramEnabled;

    @Value("${cian.credit-program.program_path}")
    String programPath;

    String cianProgramsTable = "cian_programs";

    @Value("${cian.credit-program.bank_future_rules_path}")
    String bankFutureRulesPath;

    String cianBankFutureTable = "cian_bank_future_rules";

    @Value("${cian.credit-program.additional_rate_rules_path}")
    String additionalRateRulesPath;

    String cianAdditionalRateRulesTable = "cian_additional_rate_rules";

    @Value("${spring.datasource.url}")
    String jdbcUrl;
    @Value("${spring.datasource.username}")
    String username;
    @Value("${spring.datasource.password}")
    String password;

    @Value("${cian.credit-program.make-inactive}")
    Boolean makeInactive;
    AtomicInteger counterNewPrograms = new AtomicInteger(0);
    AtomicInteger counterUpdatedPrograms = new AtomicInteger(0);

    @Override
    @Transactional
    public CreditProgram createCreditParameter(BankProgramRequest request,
                                               CreditProgramDetail creditProgramDetail,
                                               Integer sdId) {
        request.resetEarliestTime();
        request.resetLatestTime();
        CreditProgram creditProgram = creditProgramMapper.toProgramMapper(request)
                .setBank(bankService.getBankById(request.getBankId()))
                .setCreditProgramDetail(creditProgramDetail)
                .setCreditParameter(creditParameterMapper
                        .toCreditParameterMapper(request.getCreditParameter()));
        creditProgram.setCreatedBy(sdId);
        creditProgram.setUpdatedBy(sdId);
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
    public CreditProgram updateProgram(UUID creditProgramId,
                                       BankProgramRequest updateProgramRequest,
                                       CreditProgramDetail updateCreditProgramDetail,
                                       Integer sdId) {
        CreditProgram creditProgram = creditProgramRepository.findById(creditProgramId)
                .orElseThrow(() -> new ItemNotFoundException(CreditProgram.class, creditProgramId));
        CreditParameter creditParameter = creditProgram.getCreditParameter();
        CreditParameter updateCreditParameter = creditParameterMapper.toCreditParameterMapper(updateProgramRequest.getCreditParameter());
        if (!creditParameter.equals(updateCreditParameter)) {
            creditParameterMapper.updateCreditParameter(updateProgramRequest.getCreditParameter(), creditParameter);
        }
        creditProgramMapper.updateProgramFromRequest(updateProgramRequest, creditProgram);
        creditProgram.setCreditParameter(creditParameter);
        if (!creditProgram.getBank().getId().equals(updateProgramRequest.getBankId())) {
            creditProgram.setBank(bankService.getBankById(updateProgramRequest.getBankId()));
        }
        CreditProgramDetail creditProgramDetailCurrent = creditProgram.getCreditProgramDetail();
        creditProgramDetailMapper.updateProgramDetail(updateCreditProgramDetail, creditProgramDetailCurrent);
        creditProgram.setUpdatedBy(sdId);
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
            creditProgramMapper.updateProgramFromRequest(updateProgramRequest, creditProgram);
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
    public void deleteCreditProgram(UUID creditProgramId, Integer sdId) {
        CreditProgram creditProgram = getProgram(creditProgramId);
        creditProgram.setActive(false);
        creditProgram.setUpdatedBy(sdId);
        creditProgramRepository.save(creditProgram);
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

    @Async
    @Override
    public void createCreditProgramsFromCian() {
        log.info("Начали автоматическое создание кредитных программ");
        if (makeInactive) {
            updateInactiveProgramStatus();
        }
        loadProgramFromCianProgramsTable();
        log.info("Загрузили кредитные программы успешно");
    }

    @Async
    @Override
    @Scheduled(fixedRateString = "${cian.credit-program.loading_credit_program.scheduled.interval}")
    public void loadAllFilesFromCian() {
        if (loadingCreditProgramEnabled) {
            Integer programs = loadCreditProgramFromCian();
            Integer future = loadBankFutureRulesFromCian();
            Integer rate = loadAdditionalRateRulesFromCian();
            if (programs != 0 && future != 0 && rate != 0) {
                createCreditProgramsFromCian();
            } else {
                log.info("Нет новых кредитных программ для загрузки");
            }
        }
    }

    private void updateInactiveProgramStatus() {
        log.info("Помечаем не активные программы");
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            String sqlQuery = "update credit_program set is_active = false " +
                    "where cian_id IS NOT NULL;";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Double getSalaryClientInterestRate(Integer bankId, String mortgageType, String benefitProgram) {
        Double rate = 0.0;
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            String mortgageTypeParam = "%" + mortgageType + "%";
            String benefitProgramParam = "%" + benefitProgram + "%";
            String sqlQuery = "SELECT rate " +
                    "FROM cian_additional_rate_rules " +
                    "WHERE bank_id = ? " +
                    "  AND is_active " +
                    "  AND ARRAY(SELECT unnest(region_id::integer[])) && ARRAY(SELECT unnest(region_id::integer[]) FROM cian_programs) " +
                    "  AND ARRAY(SELECT unnest(real_estate_type::TEXT[])) && " +
                    "      ARRAY(SELECT unnest(cian_programs.real_estate_type::TEXT[]) FROM cian_programs) " +
                    "  AND ARRAY(SELECT unnest(object_type::TEXT[])) && " +
                    "      ARRAY(SELECT unnest(cian_programs.object_type::TEXT[]) FROM cian_programs) " +
                    "  AND mortgage_type LIKE ? " +
                    "  AND benefit_program LIKE ? " +
                    "  AND condition = 'зарплатный банк = текущий банк' " +
                    "  AND rate < 0";


            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
                preparedStatement.setInt(1, bankId);
                preparedStatement.setString(2, mortgageTypeParam);
                preparedStatement.setString(3, benefitProgramParam);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        rate = resultSet.getDouble("rate");
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Не удалось получить salaryRate: {}");
            e.printStackTrace();
        }
        return rate;
    }

    private void cleanCianTable(String tableName) {
        log.info("Запуск очистки таблицы {}", tableName);
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            String sqlQuery = "TRUNCATE TABLE " + tableName;
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
                preparedStatement.executeUpdate();
            }
            log.info("Таблица {} очищена", tableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getFullDescription(Integer bankId, String mortgageType, String benefitProgram) {
        StringBuilder builder = new StringBuilder();

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            String mortgageTypeParam = "%" + mortgageType + "%";
            String benefitProgramParam = "%" + benefitProgram + "%";
            String sqlQuery = "SELECT DISTINCT feature_description " +
                    "FROM cian_bank_future_rules cbrf " +
                    "WHERE bank_id = ? " +
                    "AND is_active " +
                    "AND ARRAY(SELECT unnest(cbrf.region_id::integer[])) && ARRAY(SELECT unnest(region_id::integer[]) FROM cian_programs) " +
                    "AND ARRAY(SELECT unnest(cbrf.real_estate_type::TEXT[])) && ARRAY(SELECT unnest(cian_programs.real_estate_type::TEXT[]) FROM cian_programs) " +
                    "AND ARRAY(SELECT unnest(cbrf.object_type::TEXT[])) && ARRAY(SELECT unnest(cian_programs.object_type::TEXT[]) FROM cian_programs) " +
                    "AND mortgage_type LIKE ? " +
                    "AND benefit_program LIKE ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
                preparedStatement.setInt(1, bankId);
                preparedStatement.setString(2, mortgageTypeParam);
                preparedStatement.setString(3, benefitProgramParam);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        builder.append(resultSet.getString("feature_description")).append(" ");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }

    private void loadProgramFromCianProgramsTable() {
        log.info("Подгрузить все программы из cian programs");

       /* String sqlQuery = "SELECT distinct bank_id, region_id, mortgage_type, real_estate_type, object_type, benefit_program, base_rate, " +
                "       loan_term_min, loan_term_max, loan_amount_min, loan_amount_max, down_payment_rate_min " +
                "FROM cian_programs " +
                "WHERE is_active " +
                "  AND ARRAY(SELECT unnest(region_id::integer[])) && ARRAY(SELECT unnest(region_id::integer[]) FROM cian_programs) " +
                "  AND ARRAY(SELECT unnest(real_estate_type::TEXT[])) && " +
                "      ARRAY(SELECT unnest(cian_programs.real_estate_type::TEXT[]) FROM cian_programs) " +
                "  AND ARRAY(SELECT unnest(object_type::TEXT[])) && " +
                "      ARRAY(SELECT unnest(cian_programs.object_type::TEXT[]) FROM cian_programs) " +
                "  AND ARRAY(SELECT unnest(income_confirmation::TEXT[])) && " +
                "      ARRAY(SELECT unnest(cian_programs.income_confirmation::TEXT[]) FROM cian_programs) " +
                "and income_confirmation like '%confirmation%'" +
                "UNION ALL " +
                "SELECT distinct bank_id, region_id, mortgage_type, real_estate_type, object_type, benefit_program, base_rate, " +
                "                loan_term_min, loan_term_max, loan_amount_min, loan_amount_max, down_payment_rate_min " +
                "FROM cian_programs " +
                "WHERE is_active " +
                "  AND ARRAY(SELECT unnest(real_estate_type::TEXT[])) && " +
                "      ARRAY(SELECT unnest(cian_programs.real_estate_type::TEXT[]) FROM cian_programs) " +
                "  AND ARRAY(SELECT unnest(object_type::TEXT[])) && " +
                "      ARRAY(SELECT unnest(cian_programs.object_type::TEXT[]) FROM cian_programs) " +
                "  AND ARRAY(SELECT unnest(income_confirmation::TEXT[])) && " +
                "      ARRAY(SELECT unnest(cian_programs.income_confirmation::TEXT[]) FROM cian_programs) " +
                "  and income_confirmation like '%confirmation%' and region_group is not null";
*/

        String sqlQuery = "SELECT bank_id, region_id, mortgage_type, real_estate_type, object_type, benefit_program, base_rate, " +
                "       loan_term_min, loan_term_max, loan_amount_min, loan_amount_max, down_payment_rate_min " +
                "FROM cian_programs ";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                BankProgramRequest bankProgramRequest = mappingToBankProgramRequest(resultSet);
                bankProgramRequest.setCianId(new Random().nextInt(6) + 1);//todo добавить поле что загружено из циана
                if (bankProgramRequest != null) {
                    CreditProgramDetail creditProgramDetail =
                            CreditProgramConverter.convertCreditDetailToStringFormat(bankProgramRequest);

                    if (creditProgramDetail != null) {
                        Integer bankId = resultSet.getInt("bank_id");
                        String mortgageType = resultSet.getString("mortgage_type");
                        String benefitProgram = resultSet.getString("benefit_program");

                        if (bankProgramRequest.getActive() != null && bankProgramRequest.getActive()) {
                            bankProgramRequest.setFullDescription(getFullDescription(bankId, mortgageType, benefitProgram));
                            bankProgramRequest.setSalaryClientInterestRate(getSalaryClientInterestRate(bankId, mortgageType, benefitProgram));
                        }

                        List<CreditProgram> existList = creditProgramExist(bankProgramRequest);
                        if (existList.isEmpty()) {
                            createCreditParameter(bankProgramRequest, creditProgramDetail, 007);
                            counterNewPrograms.incrementAndGet();
                        } else {
                            updateProgram(existList.get(0).getId(), bankProgramRequest, creditProgramDetail, 007);
                            counterUpdatedPrograms.incrementAndGet();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        log.info("Загружено новых кредитных програм {}. Обновлено {} программ ", counterNewPrograms.get(), counterUpdatedPrograms.get());
    }

    private List<CreditProgram> creditProgramExist(BankProgramRequest request) {

        return creditProgramRepository.findCreditProgram(
                request.getProgramName(),
                request.getFullDescription(),
                request.getBankId(),
                request.getBaseRate(),
                request.getSalaryClientInterestRate(),
                RegionType.getRegionTypesString(request.getInclude()),
                CreditPurposeType.getCreditPurposeTypeString(request.getCreditPurposeType()),
                request.getCreditProgramType(),
                RealEstateType.getRealEstateTypeString(request.getRealEstateType()),
                request.getCreditParameter().getMinDownPayment(),
                request.getCreditParameter().getMaxDownPayment());
    }

    private Integer loadDataFromCian(String tableName, String filePath) {
        AtomicInteger counter = new AtomicInteger();
        String fileName = Paths.get(filePath).getFileName().toString();
        try {
            if (!isFileAlreadyLoaded(tableName, fileName)) {
                cleanCianTable(tableName);

                log.info("Начали парсить файл: {}", filePath);
                try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
                     InputStream is = getInputStreamFromPath(filePath);
                     InputStreamReader inputStreamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
                     CSVReader reader = new CSVReader(inputStreamReader)) {

                    String[] header = reader.readNext();
                    String insertQuery = generateInsertQuery(tableName, header);

                    try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                        String[] nextLine;
                        while ((nextLine = reader.readNext()) != null) {
                            boolean continueProcessing = processLine(preparedStatement, header, nextLine);
                            if (continueProcessing) {
                                preparedStatement.executeUpdate();
                                counter.incrementAndGet();
                            }
                        }
                    }
                    cianLoadingFilesService.insertLoadedFileRecord(tableName, fileName, counter.get());
                    log.info("Загрузка в {} успешна, {} записей", tableName, counter);
                }
            }
        } catch (SQLException | IOException | CsvValidationException e) {
            log.error("Не удалось загрузить все записи", e);
            throw new RuntimeException(e);
        }

        return counter.get();
    }

    private boolean isFileAlreadyLoaded(String tableName, String fileName) {
        return cianLoadingFilesService.isFileAlreadyLoaded(tableName, fileName);
    }

    private boolean processLine(PreparedStatement preparedStatement, String[] header, String[] nextLine)
            throws SQLException {
        for (int i = 0; i < nextLine.length; i++) {
            if ("is_active".equals(header[i]) && !Boolean.parseBoolean(nextLine[i])) {
                return false;
            }
            if ("created_at".equals(header[i]) || "updated_at".equals(header[i])) {
                preparedStatement.setObject(i + 1, LocalDateTime.now());
            } else if ("shadow_id".equals(header[i])) {
                preparedStatement.setObject(i + 1, 1);
            } else {
                preparedStatement.setObject(i + 1, convertToCorrectType(header[i], nextLine[i]));
            }
        }
        return true;
    }

    @Override
    public Integer loadCreditProgramFromCian() {
        return loadDataFromCian(cianProgramsTable, programPath);
    }

    @Override
    public Integer loadBankFutureRulesFromCian() {
        return loadDataFromCian(cianBankFutureTable, bankFutureRulesPath);
    }

    @Override
    public Integer loadAdditionalRateRulesFromCian() {
        return loadDataFromCian(cianAdditionalRateRulesTable, additionalRateRulesPath);
    }

    private static Object convertToCorrectType(String columnName, String value) {
        if (value.isEmpty()) {
            return null;
        }
        if ("created_at".equals(columnName) || "updated_at".equals(columnName)) {
            LocalDateTime dateTime = LocalDateTime.now();
            return dateTime;

        }

        switch (columnName) {
            case "id":
                return Long.parseLong(value);
            case "bank_id":
                return Long.parseLong(value);
            case "region_id":
                return convertStringArrayToString(value);
            case "loan_term_min":
                return Long.parseLong(value);
            case "loan_term_max":
                return Long.parseLong(value);
            case "loan_amount_min":
                return Long.parseLong(value);
            case "loan_amount_max":
                return Long.parseLong(value);
            case "shadow_id":
                return 1;
            case "base_rate":
            case "down_payment_rate_min":
                return Double.parseDouble(value);
            case "down_payment_rate_max":
                return Double.parseDouble(value);
            case "is_active":
                return Boolean.parseBoolean(value);
            case "feature_description":
            case "mortgage_type":
                return convertStringArrayToString(value);
            case "real_estate_type":
                return convertStringArrayToString(value);
            case "object_type":
                return convertStringArrayToString(value);
            case "benefit_program":
                return convertStringArrayToString(value);
            case "employment_type":
                return value;
            case "min_age":
            case "max_age":
            case "min_finally_age":
                return java.lang.Integer.parseInt(value);
            case "max_finally_age":
                return java.lang.Integer.parseInt(value);
            case "condition":
                return value;
            case "rate":
                return Double.parseDouble(value);
            default:
                return value;
        }
    }

    private static String convertStringArrayToString(String stringValue) {
        return stringValue;
    }

    private static String generateInsertQuery(String tableName, String[] columns) {
        StringBuilder query = new StringBuilder("INSERT INTO " + tableName + " (");
        for (String column : columns) {
            query.append(column).append(", ");
        }
        query.delete(query.length() - 2, query.length()); // Удалить последнюю запятую и пробел
        query.append(") VALUES (");
        for (int i = 0; i < columns.length; i++) {
            query.append("?, ");
        }
        query.delete(query.length() - 2, query.length());
        query.append(")");
        return query.toString();
    }

    private BankProgramRequest mappingToBankProgramRequest(ResultSet cian) {
        BankProgramRequest bankProgramRequest = new BankProgramRequest();
        try {
            Integer bankId = cian.getInt("bank_id");
            Bank bank = bankService.findBankByCianId(cian.getInt("bank_id"));
            if (bank != null) {
                CreditProgramType creditProgramType = parseCreditProgramTypeFromInput(cian.getString("benefit_program"));

                bankProgramRequest = new BankProgramRequest()
                        .setBankId(bank.getId())
                        .setProgramName(creditProgramType.getName())
                        .setProgramStartDate(LocalDateTime.now())
                        .setProgramEndDate(LocalDateTime.now().plusDays(30 * 12 * 3)) // 3 года
                        .setDescription("")
                        .setCreditPurposeType(parseCreditPurposeTypeFromInput(cian.getString("real_estate_type"),
                                cian.getString("mortgage_type"))) //ошибки тут нет у циана это называется RealEstateType
                        .setRealEstateType(parseRealEstateTypesFromInput(cian.getString("object_type")))
                        .setCreditProgramType(creditProgramType)
                        .setInclude(parseRegionTypeIncludeFromInput(cian.getString("region_id")))
                        .setExclude(Collections.emptyList())
                        .setCreditParameter(new CreditParameterResponse()
                                .setMinMortgageSum(new BigDecimal(cian.getLong("loan_amount_min")))
                                .setMaxMortgageSum(new BigDecimal(cian.getLong("loan_amount_max")))
                                .setMinCreditTerm(cian.getInt("loan_term_min"))
                                .setMaxCreditTerm(cian.getInt("loan_term_max"))
                                .setMinDownPayment(cian.getBigDecimal("down_payment_rate_min"))
                                .setMaxDownPayment(new BigDecimal("99.99"))
                                .setIsMaternalCapital(true)
                        )
                        .setBaseRate(cian.getDouble("base_rate"))
                        .setActive(true);
            } else {
                log.info("Банк с id {} не было найден", bankId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return bankProgramRequest;
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
        Set<CreditPurposeType> types = new HashSet<>();

        if (input != null) {
            Pattern pattern = Pattern.compile("\\{([^}]+)\\}");
            Matcher matcher = pattern.matcher(input);

            if (matcher.find()) {
                String innerContent = matcher.group(1);
                String[] items = innerContent.split(",");
                var list = Arrays.asList(items);

                types = list.stream()
                        .map(CreditPurposeType::getCreditPurposeTypeByCian)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
            }
        }

        if (mortgageType != null) {
            CreditPurposeType mortgagePurposeType = CreditPurposeType.getCreditPurposeTypeByCian(mortgageType);
            if (mortgagePurposeType != null) {
                types.add(mortgagePurposeType);
            }
        }

        return new ArrayList<>(types);
    }

    private static CreditProgramType parseCreditProgramTypeFromInput(String input) {
        return CreditProgramType.getCreditProgramTypeByCian(input);
    }

    private List<RegionType> parseRegionTypeIncludeFromInput(String input) {
        List<RegionType> types;
        List<Integer> regionTypeIds = new ArrayList<>();

        Pattern pattern = Pattern.compile("\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            String innerContent = matcher.group(1);
            String[] items = innerContent.split(",");

            regionTypeIds = Arrays.stream(items)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }

        types = regionService.getRegionTypesByCianIdIn(regionTypeIds);
        return types;
    }

    private InputStream getInputStreamFromPath(String path) {
        ClassPathResource classPathResource = new ClassPathResource(path);
        try {
            return classPathResource.getInputStream();
        } catch (IOException e) {
            log.error("Error reading {}", path, e);
            throw new RuntimeException("Error reading " + path, e);
        }
    }
}
