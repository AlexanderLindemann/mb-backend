package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.entity.PartnerApplication;
import pro.mbroker.app.service.BankApplicationService;
import pro.mbroker.app.service.BorrowerProfileService;
import pro.mbroker.app.service.DocxFieldHandler;
import pro.mbroker.app.service.FormService;
import pro.mbroker.app.service.PartnerApplicationService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FormServiceImpl implements FormService {


    private final BorrowerProfileService borrowerProfileService;
    private final PartnerApplicationService partnerApplicationService;
    private final DocxFieldHandler docxFieldHandler;


    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ByteArrayResource> generateFormFile(UUID partnerApplicationId, UUID borrowerProfileId) {

        PartnerApplication partnerApplication = partnerApplicationService.getPartnerApplication(partnerApplicationId);
        BorrowerProfile borrowerProfile = borrowerProfileService.findByIdWithRealEstateVehicleAndEmployer(borrowerProfileId);
        ClassPathResource classPathResource = new ClassPathResource("form.docx");

        byte[] file;
        try (InputStream inputStream = classPathResource.getInputStream()) {
            file = inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Error reading form.docx", e);
        }
        Map<String, String> replacements = docxFieldHandler.replaceFieldValue(file, partnerApplication, borrowerProfile);
        try (InputStream inputStream = new ByteArrayInputStream(file)) {
            XWPFDocument document = new XWPFDocument(inputStream);
            replaceTextInDocx(document, replacements);
            ByteArrayOutputStream docxOutputStream = new ByteArrayOutputStream();
            document.write(docxOutputStream);
            ByteArrayResource resource = new ByteArrayResource(docxOutputStream.toByteArray());
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=example.docx");
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void replaceTextInDocx(XWPFDocument document, Map<String, String> replacements) {
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            replaceTextInParagraph(paragraph, replacements);
        }
        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        replaceTextInParagraph(paragraph, replacements);
                    }
                }
            }
        }
    }

    private void replaceTextInParagraph(XWPFParagraph paragraph, Map<String, String> replacements) {
        StringBuilder accumulatedParagraphText = new StringBuilder();
        for (XWPFRun run : paragraph.getRuns()) {
            String runText = run.getText(0);
            if (runText != null) {
                accumulatedParagraphText.append(runText);
            }
        }
        String originalText = accumulatedParagraphText.toString();
        for (int i = paragraph.getRuns().size() - 1; i >= 0; i--) {
            paragraph.removeRun(i);
        }
        int lastPosition = 0;
        List<ReplacementPosition> replacementPositions = new ArrayList<>();
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            if (entry.getValue() != null) {
                String keyWithQuotes = "«" + entry.getKey() + "»";
                int startPos = originalText.indexOf(keyWithQuotes, lastPosition);
                while (startPos != -1) {
                    replacementPositions.add(new ReplacementPosition(startPos, startPos + keyWithQuotes.length(), entry.getValue()));
                    startPos = originalText.indexOf(keyWithQuotes, startPos + keyWithQuotes.length());
                }
            }
        }
        Collections.sort(replacementPositions);
        for (ReplacementPosition position : replacementPositions) {
            if (position.start > lastPosition) {
                XWPFRun runBefore = paragraph.createRun();
                runBefore.setText(originalText.substring(lastPosition, position.start));
            }
            XWPFRun boldRun = paragraph.createRun();
            boldRun.setText(position.replacement);
            boldRun.setBold(true);
            lastPosition = position.end;
        }
        if (lastPosition < originalText.length()) {
            XWPFRun runAfter = paragraph.createRun();
            runAfter.setText(originalText.substring(lastPosition));
        }
    }

    public static class ReplacementPosition implements Comparable<ReplacementPosition> {
        int start;
        int end;
        String replacement;

        public ReplacementPosition(int start, int end, String replacement) {
            this.start = start;
            this.end = end;
            this.replacement = replacement;
        }

        @Override
        public int compareTo(ReplacementPosition o) {
            return Integer.compare(this.start, o.start);
        }
    }
}
