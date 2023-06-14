package pro.mbroker.app.service.impl;


import com.google.common.net.HttpHeaders;
import liquibase.util.csv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.mbroker.api.dto.BankWithBankApplicationDto;
import pro.mbroker.api.dto.response.BankApplicationResponse;
import pro.mbroker.api.dto.response.PartnerApplicationResponse;
import pro.mbroker.app.exception.ReportGenerationException;
import pro.mbroker.app.service.ReportService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    @Override
    public void generateCsvReport(HttpServletResponse httpServletResponse, List<PartnerApplicationResponse> partnerApplicationResponses) {
        log.info("Starting generation of CSV report for {} applications", partnerApplicationResponses.size());
        httpServletResponse.setContentType("text/csv; charset=UTF-8");
        httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.csv");
        try (Writer writer = new OutputStreamWriter(httpServletResponse.getOutputStream(), StandardCharsets.UTF_8);
             CSVWriter csvWriter = new CSVWriter(writer, ';',
                     CSVWriter.NO_QUOTE_CHARACTER,
                     CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                     CSVWriter.DEFAULT_LINE_END)) {
            writeCsvHeader(csvWriter);
            writeCsvBody(csvWriter, partnerApplicationResponses);
            csvWriter.flush();
            log.info("Successfully generated CSV report for {} applications", partnerApplicationResponses.size());
        } catch (IOException e) {
            log.error("Error occurred while generating CSV report", e);
            throw new ReportGenerationException(e);
        }
    }

    private static void writeCsvHeader(CSVWriter csvWriter) {
        log.debug("Writing CSV header");
        String[] headerRecord = {
                "Bank Application ID", "Developer Application ID", "Bank ID", "Bank Name",
                "Developer ID", "Developer Name", "Main Borrower's Full Name",
                "Bank Application Status", "Credit Program ID", "Credit Program Name",
                "Property Cost", "Credit Amount", "Developer Application Creation Date",
                "Last Modification Date of Bank Application"
        };
        csvWriter.writeNext(headerRecord);
    }

    private static void writeCsvBody(CSVWriter csvWriter, List<PartnerApplicationResponse> partnerApplicationResponses) {
        log.debug("Writing CSV body");
        for (PartnerApplicationResponse response : partnerApplicationResponses) {
            for (BankWithBankApplicationDto bankWithBankAppDto : response.getBankWithBankApplicationDto()) {
                for (BankApplicationResponse bankAppResponse : bankWithBankAppDto.getBankApplications()) {
                    String[] record = {
                            bankAppResponse.getId().toString(),
                            response.getId().toString(),
                            bankWithBankAppDto.getBankId().toString(),
                            bankWithBankAppDto.getBankName(),
                            response.getRealEstate().getId().toString(),
                            response.getRealEstate().getResidentialComplexName(),
                            bankAppResponse.getMainBorrower().getFirstName() + " " +
                                    bankAppResponse.getMainBorrower().getLastName() + " " +
                                    bankAppResponse.getMainBorrower().getMiddleName(),
                            Objects.nonNull(bankAppResponse.getStatus()) ? bankAppResponse.getStatus().toString() : "",
                            bankAppResponse.getCreditProgramId().toString(),
                            bankAppResponse.getCreditProgramName(),
                            response.getMortgageCalculation().getRealEstatePrice().toString(),
                            bankAppResponse.getMortgageSum().toString(),
                            bankAppResponse.getCreatedAt().toString(),
                            bankAppResponse.getUpdatedAt().toString()
                    };
                    csvWriter.writeNext(record);
                }
            }
        }
    }
}

