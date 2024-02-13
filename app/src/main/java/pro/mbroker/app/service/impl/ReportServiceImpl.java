package pro.mbroker.app.service.impl;


import com.google.common.net.HttpHeaders;
import liquibase.util.csv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.mbroker.app.entity.BankApplication;
import pro.mbroker.app.entity.BaseEntity;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.entity.PartnerApplication;
import pro.mbroker.app.exception.ReportGenerationException;
import pro.mbroker.app.service.CalculatorService;
import pro.mbroker.app.service.ReportService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final CalculatorService calculatorService;

    @Override
    public void generateCsvReport(HttpServletResponse httpServletResponse, List<PartnerApplication> partnerApplications) {
        log.info("Starting generation of CSV report for {} applications", partnerApplications.size());
        httpServletResponse.setContentType("text/csv; charset=UTF-8");
        httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.csv");
        try (Writer writer = new OutputStreamWriter(httpServletResponse.getOutputStream(), StandardCharsets.UTF_8);
             CSVWriter csvWriter = new CSVWriter(writer, ';',
                     CSVWriter.NO_QUOTE_CHARACTER,
                     CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                     CSVWriter.DEFAULT_LINE_END)) {
            writer.write('\ufeff');
            writeCsvHeader(csvWriter);
            writeCsvBody(csvWriter, partnerApplications);
            csvWriter.flush();
            log.info("Successfully generated CSV report for {} applications", partnerApplications.size());
        } catch (IOException e) {
            log.error("Error occurred while generating CSV report", e);
            throw new ReportGenerationException(e);
        }
    }

    private void writeCsvHeader(CSVWriter csvWriter) {
        log.debug("Writing CSV header");
        String[] headerRecord = {
                "Application Number", "Bank Application ID", "Developer Application ID", "Bank ID", "Bank Name",
                "Developer ID", "Developer Name", "Real Estate ID", "Real Estate Name", "Main Borrower's Full Name",
                "Bank Application Status", "Credit Program ID", "Credit Program Name",
                "Property Cost", "Credit Amount", "Developer Application Creation Date",
                "Last Modification Date of Bank Application"
        };
        csvWriter.writeNext(headerRecord);
    }

    private void writeCsvBody(CSVWriter csvWriter, List<PartnerApplication> partnerApplications) {
        log.debug("Writing CSV body");
        for (PartnerApplication response : partnerApplications) {
            String[] baseRecordPart = {
                    "",                                                                 // Application Number
                    "",                                                                 // BankAppResponse ID
                    response.getId().toString(),                                        // Developer Application ID
                    "",                                                                 // Bank ID
                    "",                                                                 // Bank Name
                    response.getPartner().getId().toString(),                           // Developer ID
                    response.getPartner().getName(),                                    // Developer Name
                    response.getRealEstate().getId().toString(),                        // Real Estate ID
                    response.getRealEstate().getResidentialComplexName(),               // Real Estate Name
                    getFullName(response.getBorrowerProfiles()),                        // Main Borrower's Full Name
                    "",                                                                 // Bank Application Status
                    "",                                                                 // Credit Program ID
                    "",                                                                 // Credit Program Name
                    response.getMortgageCalculation().getRealEstatePrice().toString(),  // Property Cost
                    getCreditAmount(response),                                          // Credit Amount
                    "",                                                                 // CreatedAt
                    ""                                                                  // UpdatedAt
            };
            List<BankApplication> bankApplications = response.getBankApplications().stream()
                    .filter(BaseEntity::isActive).collect(Collectors.toList());
            if (bankApplications.isEmpty()) {
                csvWriter.writeNext(baseRecordPart);
            } else {
                for (BankApplication bankApplication : bankApplications) {
                    baseRecordPart[0] = bankApplication.getApplicationNumber().toString();
                    baseRecordPart[1] = bankApplication.getId().toString();
                    baseRecordPart[3] = bankApplication.getCreditProgram().getBank().getId().toString();
                    baseRecordPart[4] = bankApplication.getCreditProgram().getBank().getName();
                    baseRecordPart[9] = getFullName(List.of(bankApplication.getMainBorrower()));
                    baseRecordPart[10] = bankApplication.getBankApplicationStatus().toString();
                    baseRecordPart[11] = bankApplication.getCreditProgram().getId().toString();
                    baseRecordPart[12] = bankApplication.getCreditProgram().getProgramName();
                    baseRecordPart[15] = bankApplication.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    baseRecordPart[16] = bankApplication.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    csvWriter.writeNext(baseRecordPart);
                }
            }
        }
    }

    private String getFullName(List<BorrowerProfile> profiles) {
        return profiles.stream()
                .findFirst()
                .map(bp -> String.format("%s %s %s", bp.getFirstName(), bp.getLastName(),
                        Objects.nonNull(bp.getMiddleName())
                                ? bp.getMiddleName()
                                : "")).orElse("");
    }

    private String getCreditAmount(PartnerApplication partnerApplication) {
        return calculatorService.getMortgageSum(
                partnerApplication.getMortgageCalculation()
                        .getRealEstatePrice(),
                partnerApplication.getMortgageCalculation()
                        .getDownPayment()).toString();
    }
}