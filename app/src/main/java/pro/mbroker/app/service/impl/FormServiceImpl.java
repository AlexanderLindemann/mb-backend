package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.Units;
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
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.api.enums.BorrowerProfileStatus;
import pro.mbroker.app.entity.Attachment;
import pro.mbroker.app.entity.BankApplication;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.entity.PartnerApplication;
import pro.mbroker.app.repository.BorrowerProfileRepository;
import pro.mbroker.app.service.AttachmentService;
import pro.mbroker.app.service.BankApplicationService;
import pro.mbroker.app.service.BorrowerProfileService;
import pro.mbroker.app.service.DocxFieldHandler;
import pro.mbroker.app.service.FormService;
import pro.mbroker.app.util.CustomMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Component
public class FormServiceImpl implements FormService {

    private final AttachmentService attachmentService;
    private final BorrowerProfileService borrowerProfileService;
    private final BorrowerProfileRepository borrowerProfileRepository;
    private final DocxFieldHandler docxFieldHandler;
    private final BankApplicationService bankApplicationService;
    String filePath = "forms/SDMortgageForm_2023-09-27.docx";

    @Override
    @Transactional
    public ResponseEntity<ByteArrayResource> generateFormFile(UUID borrowerProfileId) {
        BorrowerProfile borrowerProfile = borrowerProfileService.findByIdWithRealEstateVehicleAndEmployer(borrowerProfileId);
        PartnerApplication partnerApplication = borrowerProfile.getPartnerApplication();

        Map<String, String> replacements = docxFieldHandler.replaceFieldValue(
                getFileFromPath(filePath),
                partnerApplication,
                borrowerProfile);
        ByteArrayResource byteAreaResource = matchReplacements(replacements, filePath);
        removeSignatureForm(borrowerProfile);
        borrowerProfileService.updateBorrowerStatus(borrowerProfileId, BorrowerProfileStatus.DATA_ENTERED);
        return processFormResponse(byteAreaResource);
    }


    @Override
    public ResponseEntity<ByteArrayResource> signatureFormFile(UUID borrowerProfileId, byte[] signature) {
        BorrowerProfile borrowerProfile = borrowerProfileService.findByIdWithRealEstateVehicleAndEmployer(borrowerProfileId);
        PartnerApplication partnerApplication = borrowerProfile.getPartnerApplication();
        String encodedImage = Base64.getEncoder().encodeToString(signature);
        Map<String, String> replacements = docxFieldHandler.replaceFieldValue(
                getFileFromPath(filePath),
                partnerApplication, borrowerProfile);
        replacements.put("borrowerSign", encodedImage);

        ByteArrayResource byteAreaResource = matchReplacements(replacements, filePath);
        return processFormResponse(byteAreaResource);
    }

    @SneakyThrows
    @Override
    @Transactional
    public void updateGeneratedForm(UUID borrowerProfileId, byte[] form) {
        BorrowerProfile borrowerProfile = borrowerProfileService.getBorrowerProfile(borrowerProfileId);
        MultipartFile multipartFile = new CustomMultipartFile(
                form,
                borrowerProfile.getLastName() + "_" + borrowerProfile.getFirstName() + ".pdf",
                "application/pdf"
        );
        Attachment upload = attachmentService.upload(multipartFile);
        borrowerProfile.setGeneratedForm(upload);
        borrowerProfileRepository.save(borrowerProfile);
    }

    @Override
    @Transactional
    public void updateSignatureForm(UUID borrowerProfileId, byte[] form) {
        BorrowerProfile borrowerProfile = borrowerProfileService.getBorrowerProfile(borrowerProfileId);
        MultipartFile multipartFile = new CustomMultipartFile(
                form,
                borrowerProfile.getLastName() + "_" + borrowerProfile.getFirstName() + ".pdf",
                "application/pdf"
        );
        Attachment upload = attachmentService.upload(multipartFile);
        borrowerProfile.setSignedForm(upload);
        borrowerProfile.setBorrowerProfileStatus(BorrowerProfileStatus.DOCS_SIGNED);
        borrowerProfileRepository.save(borrowerProfile);
        BankApplication bankApplication = bankApplicationService
                .getBankApplicationByBorrowerId(borrowerProfile.getId()).get(0);
        bankApplicationService.changeStatus(bankApplication.getId(), BankApplicationStatus.READY_TO_SENDING);

    }

    private void removeSignatureForm(BorrowerProfile borrowerProfile) {
        Optional.ofNullable(borrowerProfile.getSignedForm())
                .map(Attachment::getId)
                .ifPresent(attachment -> {
                    attachmentService.markAttachmentAsDeleted(attachment);
                    borrowerProfileService.deleteSignatureForm(attachment);
                });
    }

    private ResponseEntity<ByteArrayResource> processFormResponse(ByteArrayResource resource) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=example.docx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    private ByteArrayResource matchReplacements(Map<String, String> replacements, String filePath) {
        byte[] file = getFileFromPath(filePath);

        try (InputStream inputStream = new ByteArrayInputStream(file)) {
            XWPFDocument document = new XWPFDocument(inputStream);
            replaceTextInDocx(document, replacements);
            ByteArrayOutputStream docxOutputStream = new ByteArrayOutputStream();
            document.write(docxOutputStream);

            return new ByteArrayResource(docxOutputStream.toByteArray());
        } catch (IOException e) {
            log.error("Error processing the form with replacements", e);
            throw new RuntimeException(e);
        }

    }

    private byte[] getFileFromPath(String path) {
        ClassPathResource classPathResource = new ClassPathResource(path);
        try (InputStream inputStream = classPathResource.getInputStream()) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            log.error("Error reading {}", path, e);
            throw new RuntimeException("Error reading " + path, e);
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
        if (originalText.contains("«borrowerSign»")) {
            if (Objects.nonNull(replacements.get("borrowerSign"))) {
                byte[] decodedImage = Base64.getDecoder().decode(replacements.get("borrowerSign"));
                addImageToParagraph(paragraph, decodedImage, XWPFDocument.PICTURE_TYPE_PNG);
                originalText = originalText.replace("«borrowerSign»", "");
            }
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

    private void addImageToParagraph(XWPFParagraph paragraph, byte[] imageBytes, int imageFormat) {
        try {
            XWPFRun run = paragraph.createRun();
            run.addPicture(new ByteArrayInputStream(imageBytes), imageFormat, "signature.png", Units.toEMU(60), Units.toEMU(15));
        } catch (Exception e) {
            throw new RuntimeException("Could not add image to paragraph", e);
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
