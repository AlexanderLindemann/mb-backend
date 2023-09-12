package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.app.entity.BankApplication;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.service.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("PMD")
public class FormServiceImpl implements FormService {


    private final BankApplicationService bankApplicationService;
    private final BorrowerProfileService borrowerProfileService;
    private final DocxFieldHandler docxFieldHandler;


    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ByteArrayResource> generateFormFile(UUID bankApplicationId, UUID borrowerProfileId, MultipartFile file) {
        BankApplication bankApplication = bankApplicationService.getBankApplicationById(bankApplicationId);
        BorrowerProfile borrowerProfile = borrowerProfileService.findByIdWithRealEstateVehicleAndEmployer(borrowerProfileId);
        Map<String, String> replacements = docxFieldHandler.replaceFieldValue(file, bankApplication, borrowerProfile);
        try (InputStream inputStream = new ByteArrayInputStream(file.getBytes())) {
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
