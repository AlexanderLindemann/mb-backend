package pro.mbroker.app.service.impl;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
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
import pro.mbroker.app.mapper.ProgramMapper;
import pro.mbroker.app.repository.CreditProgramRepository;
import pro.mbroker.app.repository.specification.CreditProgramSpecification;
import pro.mbroker.app.service.BankService;
import pro.mbroker.app.service.CreditProgramService;
import pro.mbroker.app.service.RegionService;
import pro.mbroker.app.util.CreditProgramConverter;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    private final ProgramMapper programMapper;
    private final CreditProgramDetailMapper creditProgramDetailMapper;
    private final BankService bankService;
    private final RegionService regionService;
    @Value("${program_path}")
    String programPath;

    @Value("${bank_future_rules_path}")
    String bankFutureRulesPath;

    @Value("${additional_rate_rules_path}")
    String additionalRateRulesPath;

    @Value("${spring.datasource.url}")
    String jdbcUrl;
    @Value("${spring.datasource.username}")
    String username;
    @Value("${spring.datasource.password}")
    String password;

    AtomicInteger counterPrograms = new AtomicInteger(0);


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

    @Async
    @Override
    public void createCreditProgramsFromCian() {
        log.info("Начали автоматическое создание кредитных программ");
        updateInactiveProgramStatus();
        loadProgramFromCianProgramsTable();
        log.info("Загрузили кредитные программы успешно");
    }

    @Async
    @Override
    //@Scheduled(fixedRate = 60 * 60 * 1000)  //
    public void loadAllFilesFromCian() {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Запускаем каждый метод в отдельном потоке
        executor.submit(this::loadCreditProgramFromCian);
        executor.submit(this::loadBankFutureRulesFromCian);
        executor.submit(this::loadAdditionalRateRulesFromCian);

        executor.shutdown();
    }

    private void updateInactiveProgramStatus() {
        log.info("Помечаем не активные программы");
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            String sqlQuery = "update credit_program set is_active = false " +
                    "where cian_id in (select id from cian_programs where is_active = false);";
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

        String sqlQuery = "SELECT distinct bank_id, region_id, region_group, mortgage_type, real_estate_type, object_type, benefit_program, base_rate, " +
                "       loan_term_min, loan_term_max, loan_amount_min, loan_amount_max, down_payment_rate_min, created_at " +
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
                "SELECT distinct bank_id, region_id, region_group, mortgage_type, real_estate_type, object_type, benefit_program, base_rate, " +
                "                loan_term_min, loan_term_max, loan_amount_min, loan_amount_max, down_payment_rate_min, created_at " +
                "FROM cian_programs " +
                "WHERE is_active " +
                "  AND ARRAY(SELECT unnest(real_estate_type::TEXT[])) && " +
                "      ARRAY(SELECT unnest(cian_programs.real_estate_type::TEXT[]) FROM cian_programs) " +
                "  AND ARRAY(SELECT unnest(object_type::TEXT[])) && " +
                "      ARRAY(SELECT unnest(cian_programs.object_type::TEXT[]) FROM cian_programs) " +
                "  AND ARRAY(SELECT unnest(income_confirmation::TEXT[])) && " +
                "      ARRAY(SELECT unnest(cian_programs.income_confirmation::TEXT[]) FROM cian_programs) " +
                "  and income_confirmation like '%confirmation%' and region_group is not null";


        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password); PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                BankProgramRequest bankProgramRequest = mappingToBankProgramRequest(resultSet);
                CreditProgramDetail creditProgramDetail = null;
                if (bankProgramRequest != null) {
                    creditProgramDetail = CreditProgramConverter.convertCreditDetailToStringFormat(bankProgramRequest);
                }
                if (creditProgramDetail != null) {
                    Integer bankId = resultSet.getInt("bank_id");
                    String mortgageType = resultSet.getString("mortgage_type");
                    String benefitProgram = resultSet.getString("benefit_program");

                    if (bankProgramRequest.getActive()) {
                        bankProgramRequest.setFullDescription(getFullDescription(bankId, mortgageType, benefitProgram));
                        bankProgramRequest.setSalaryClientInterestRate(getSalaryClientInterestRate(bankId, mortgageType, benefitProgram));
                    }

                    createCreditParameter(bankProgramRequest, creditProgramDetail);
                    counterPrograms.incrementAndGet();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    private BankProgramRequest executeQueryAndGetResults(Connection connection, String sqlQuery) throws SQLException {
        BankProgramRequest bankProgramRequests = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                BankProgramRequest bankProgramRequest = mappingToBankProgramRequest(resultSet);
                CreditProgramDetail creditProgramDetail = null;
                if (bankProgramRequest != null) {
                    creditProgramDetail = CreditProgramConverter.convertCreditDetailToStringFormat(bankProgramRequest);
                }
                if (creditProgramDetail != null) {
                    Integer bankId = resultSet.getInt("bank_id");
                    String mortgageType = resultSet.getString("mortgage_type");
                    String benefitProgram = resultSet.getString("benefit_program");

                    if (bankProgramRequest.getActive()) {
                        bankProgramRequest.setFullDescription(getFullDescription(bankId, mortgageType, benefitProgram));
                        bankProgramRequest.setSalaryClientInterestRate(getSalaryClientInterestRate(bankId, mortgageType, benefitProgram));
                    }

                    createCreditParameter(bankProgramRequest, creditProgramDetail);
                    counterPrograms.incrementAndGet();
                }
            }
        }
        return bankProgramRequests;
    }

    @Override
    @Async
    public void loadCreditProgramFromCian() {
        try {
            cleanCianTable("cian_programs");
            String fullPath = System.getProperty("user.dir") + programPath;
            FileReader filereader = new FileReader(fullPath);
            AtomicInteger counter = new AtomicInteger();
            log.info("Начали парсить файл: {}", programPath);
            try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
                 CSVReader reader = new CSVReader(filereader)) {

                String tableName = "cian_programs";
                String[] header = reader.readNext();
                String insertQuery = generateInsertQuery(tableName, header);

                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                    String[] nextLine;
                    while ((nextLine = reader.readNext()) != null) {
                        for (int i = 0; i < nextLine.length; i++) {
                            preparedStatement.setObject(i + 1, convertToCorrectType(header[i], nextLine[i]));
                        }

                        preparedStatement.executeUpdate();
                        counter.getAndIncrement();
                    }
                }

                log.info("Загрузка успешно в cian_programs успешно {} записей ", counter);
            }
        } catch (SQLException | IOException e) {
            log.error("Не удалось загрузить все записи");
            e.printStackTrace();
        } catch (CsvValidationException ex) {
            log.error("Не удалось загрузить все записи");
            throw new RuntimeException(ex);
        }
    }

    @Override
    @Async
    public void loadBankFutureRulesFromCian() {
        try {
            cleanCianTable("cian_bank_future_rules");
            String fullPath = System.getProperty("user.dir") + bankFutureRulesPath;
            FileReader filereader = new FileReader(fullPath);
            String table = "cian_bank_future_rules";
            AtomicInteger counter = new AtomicInteger(0);
            log.info("Начали парсить файл: " + fullPath);

            try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
                 CSVReader reader = new CSVReader(filereader)) {

                String[] header = reader.readNext();
                String insertQuery = generateInsertQuery(table, header);

                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                    String[] nextLine;
                    boolean continueProcessing = true;
                    while (continueProcessing && (nextLine = reader.readNext()) != null) {
                        for (int i = 0; i < nextLine.length; i++) {
                            if ("is_active".equals(header[i]) && !Boolean.parseBoolean(nextLine[i])) {
                                continueProcessing = false;
                                break;
                            }
                            preparedStatement.setObject(i + 1, convertToCorrectType(header[i], nextLine[i]));
                        }

                        if (continueProcessing) {
                            preparedStatement.executeUpdate();
                            counter.incrementAndGet();
                        } else {
                            continueProcessing = true;
                        }
                    }
                }
            }
            log.info("Загрузили в cian_bank_future_rules успешно {} записей", counter);
        } catch (SQLException | IOException e) {
            log.error("Загрузка файла завершилась ошибкой" + e.getMessage());
        } catch (CsvValidationException ex) {
            log.error("Загрузка файла завершилась ошибкой" + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Override
    @Async
    public void loadAdditionalRateRulesFromCian() {
        try {
            cleanCianTable("cian_additional_rate_rules");
            String fullPath = System.getProperty("user.dir") + additionalRateRulesPath;
            String table = "cian_additional_rate_rules";
            AtomicInteger counter = new AtomicInteger(0);

            log.info("Начали парсить файл: " + fullPath);

            FileReader filereader = new FileReader(fullPath);

            try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
                 CSVReader reader = new CSVReader(filereader)) {

                String[] header = reader.readNext();
                String insertQuery = generateInsertQuery(table, header);

                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                    String[] nextLine;
                    boolean continueProcessing = true;
                    while (continueProcessing && (nextLine = reader.readNext()) != null) {
                        for (int i = 0; i < nextLine.length; i++) {
                            if ("is_active".equals(header[i]) && !Boolean.parseBoolean(nextLine[i])) {
                                continueProcessing = false;
                                break;
                            }
                            preparedStatement.setObject(i + 1, convertToCorrectType(header[i], nextLine[i]));
                        }

                        if (continueProcessing) {
                            preparedStatement.executeUpdate();
                            counter.incrementAndGet();
                        } else {
                            continueProcessing = true;
                        }
                    }
                }

                log.info("Загрузили в cian_additional_rate_rules успешно {} записей", counter);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Object convertToCorrectType(String columnName, String value) {
        if (value.isEmpty()) {
            return null;
        }
        if ("created_at".equals(columnName) || "updated_at".equals(columnName)) {
            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS Z");
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.now();
            try {
                dateTime = LocalDateTime.parse(value, formatter1);
            } catch (DateTimeParseException e1) {
                try {
                    dateTime = LocalDateTime.parse(value, formatter2);
                } catch (DateTimeParseException e2) {

                }
            }

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
            case "loan_term_max":
            case "loan_amount_min":
            case "loan_amount_max":
            case "shadow_id":
                return Long.parseLong(value);
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
                return Integer.parseInt(value);
            case "max_finally_age":
                return Integer.parseInt(value);
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
        Bank bank;
        try {
            bank = bankService.findBankByCianId(cian.getInt("bank_id"));
            LocalDateTime createdAt = cian.getTimestamp("created_at").toLocalDateTime();
            CreditProgramType creditProgramType = parseCreditProgramTypeFromInput(cian.getString("mortgage_type"));

            bankProgramRequest = new BankProgramRequest()
                    .setBankId(bank.getId())
                    .setProgramName(creditProgramType.getName())
                    .setProgramStartDate(createdAt)
                    .setProgramEndDate(createdAt.plusDays(30 * 12 * 3)) // 3 года
                    .setDescription("")
                    .setCreditPurposeType(parseCreditPurposeTypeFromInput(cian.getString("real_estate_type"),
                            cian.getString("mortgage_type"))) //ошибки тут нет у циана это называется RealEstateType
                    .setRealEstateType(parseRealEstateTypesFromInput(cian.getString("object_type")))
                    .setCreditProgramType(creditProgramType)
                    .setInclude(parseRegionTypeIncludeFromInput(cian.getString("region_id"), cian.getString("region_group")))
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (bank == null) {
            return null;
        } //else todo создать автоматически банк. Но это не просто.
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

    private List<RegionType> parseRegionTypeIncludeFromInput(String input, String regionGroupName) {
        List<RegionType> types;
        List<Integer> regionTypeIds = new ArrayList<>();

        if (input.equals("{}")) {
            regionTypeIds = regionService.getRegionIdsByGroupName(regionGroupName);
        } else {
            Pattern pattern = Pattern.compile("\\{([^}]+)\\}");
            Matcher matcher = pattern.matcher(input);

            if (matcher.find()) {
                String innerContent = matcher.group(1);
                String[] items = innerContent.split(",");

                regionTypeIds = Arrays.stream(items)
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
            }
        }
        types = regionService.getRegionTypesByCianIdIn(regionTypeIds);
        return types;
    }
}

