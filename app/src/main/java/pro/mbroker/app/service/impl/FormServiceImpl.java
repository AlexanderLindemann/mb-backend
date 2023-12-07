package pro.mbroker.app.service.impl;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.jsoup.select.Elements;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.dto.request.BorrowerDocumentRequest;
import pro.mbroker.api.enums.BorrowerProfileStatus;
import pro.mbroker.api.enums.DocumentType;
import pro.mbroker.app.entity.Attachment;
import pro.mbroker.app.entity.BaseEntity;
import pro.mbroker.app.entity.BorrowerDocument;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.entity.PartnerApplication;
import pro.mbroker.app.service.AttachmentService;
import pro.mbroker.app.service.BorrowerProfileService;
import pro.mbroker.app.service.DocxFieldHandler;
import pro.mbroker.app.service.FormService;
import pro.mbroker.app.service.PartnerApplicationService;
import pro.mbroker.app.service.StatusService;
import pro.mbroker.app.util.CustomMultipartFile;
import pro.mbroker.app.util.FooterHandler;
import pro.mbroker.app.util.PageNumeratorHandler;

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
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
@PropertySource("classpath:application.yaml")
public class FormServiceImpl implements FormService {

    private final AttachmentService attachmentService;
    private final BorrowerProfileService borrowerProfileService;
    private final StatusService statusService;
    private final DocxFieldHandler docxFieldHandler;
    private final PartnerApplicationService partnerApplicationService;

    @Value("${form_path_docx}")
    private String FORM_PATH_DOCX;
    @Value("${form_path_html}")
    private String FORM_PATH_HTML;
    @Value("${font_path}")
    private String FONT_PATH;
    @Value("${footer_path}")
    private String FOOTER_PATH;
    @Value("${fit_width_image}")
    private float FIT_WIDTH_IMAGE;
    @Value("${fit_height_image}")
    private float FIT_HEIGHT_IMAGE;
    @Value("${left_position_image}")
    private float LEFT_POSITION_IMAGE;
    @Value("${bottom_position_image}")
    private float BOTTOM_POSITION_IMAGE;
    @Value("${page_number_front_size}")
    private float PAGE_NUMBER_FRONT_SIZE;
    @Value("${page_number_right_offset}")
    private float PAGE_NUMBER_RIGHT_OFFSET;
    @Value("${page_number_bottom_offset}")
    private float PAGE_NUMBER_BOTTOM_OFFSET;

    @Override
    @Transactional
    public ResponseEntity<ByteArrayResource> generateFormFileDocx(UUID borrowerProfileId) {
        BorrowerProfile borrowerProfile = borrowerProfileService.findByIdWithRealEstateVehicleAndEmployer(borrowerProfileId);
        PartnerApplication partnerApplication = borrowerProfile.getPartnerApplication();

        Map<String, String> replacements = docxFieldHandler.replaceFieldValue(
                partnerApplication,
                borrowerProfile);
        ByteArrayResource byteAreaResource = matchReplacements(replacements, FORM_PATH_DOCX);
        return processFormDocxResponse(byteAreaResource);
    }


    @Override
    public ResponseEntity<ByteArrayResource> signatureFormFile(UUID borrowerProfileId, byte[] signature) {
        BorrowerProfile borrowerProfile = borrowerProfileService.findByIdWithRealEstateVehicleAndEmployer(borrowerProfileId);
        PartnerApplication partnerApplication = borrowerProfile.getPartnerApplication();
        String encodedImage = Base64.getEncoder().encodeToString(signature);
        Map<String, String> replacements = docxFieldHandler.replaceFieldValue(
                partnerApplication, borrowerProfile);
        replacements.put("borrowerSign", encodedImage);

        ByteArrayResource byteAreaResource = matchReplacements(replacements, FORM_PATH_DOCX);
        return processFormDocxResponse(byteAreaResource);
    }

    //TODO - тестовое апи, удалить после Саниных тестов
    @Override
    @Transactional
    public ResponseEntity<ByteArrayResource> generateFormFileTest(UUID borrowerProfileId, byte[] file) {
        BorrowerProfile borrowerProfile = borrowerProfileService.findByIdWithRealEstateVehicleAndEmployer(borrowerProfileId);
        PartnerApplication partnerApplication = borrowerProfile.getPartnerApplication();
        Map<String, String> replacements = docxFieldHandler.replaceFieldValue(
                partnerApplication,
                borrowerProfile);
        ByteArrayResource byteAreaResource = matchReplacementsTest(replacements, file);
        borrowerProfileService.updateBorrowerStatus(borrowerProfileId, BorrowerProfileStatus.DATA_ENTERED);
        return processFormDocxResponse(byteAreaResource);
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

        List<Long> generatedAndSignatureFormsAttachmentIds = Stream.concat(
                        getAttachmentIdsByDocumentType(borrowerProfile, DocumentType.GENERATED_FORM).stream(),
                        getAttachmentIdsByDocumentType(borrowerProfile, DocumentType.GENERATED_SIGNATURE_FORM).stream())
                .collect(Collectors.toList());

        for (BorrowerDocument borrowerDocument : borrowerProfile.getBorrowerDocument()) {
            if (generatedAndSignatureFormsAttachmentIds.contains(borrowerDocument.getAttachment().getId())) {
                borrowerDocument.setActive(false);
                borrowerDocument.getAttachment().setActive(false);
            }
        }
        BorrowerDocumentRequest borrowerDocumentRequest = new BorrowerDocumentRequest()
                .setBorrowerProfileId(borrowerProfileId)
                .setDocumentType(DocumentType.GENERATED_FORM);
        borrowerProfile.getBorrowerDocument().add(attachmentService.uploadDocument(multipartFile, borrowerDocumentRequest));
        PartnerApplication partnerApplication = borrowerProfileService.getBorrowerProfile(borrowerProfileId).getPartnerApplication();
        statusService.statusChanger(partnerApplication);
        partnerApplicationService.save(partnerApplication);
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
        List<Long> signatureFormsAttachmentIds = getAttachmentIdsByDocumentType(borrowerProfile, DocumentType.GENERATED_SIGNATURE_FORM);
        for (BorrowerDocument borrowerDocument : borrowerProfile.getBorrowerDocument()) {
            if (signatureFormsAttachmentIds.contains(borrowerDocument.getAttachment().getId())) {
                borrowerDocument.setActive(false);
                borrowerDocument.getAttachment().setActive(false);
            }
        }
        BorrowerDocumentRequest borrowerDocumentRequest = new BorrowerDocumentRequest()
                .setBorrowerProfileId(borrowerProfileId)
                .setDocumentType(DocumentType.GENERATED_SIGNATURE_FORM);
        borrowerProfile.getBorrowerDocument().add(attachmentService.uploadDocument(multipartFile, borrowerDocumentRequest));
        PartnerApplication partnerApplication = borrowerProfileService.getBorrowerProfile(borrowerProfileId).getPartnerApplication();
        statusService.statusChanger(partnerApplication);
        partnerApplicationService.save(partnerApplication);
    }

    @Override
    public ResponseEntity<ByteArrayResource> generateFormFileHtml(UUID borrowerProfileId) {
        BorrowerProfile borrowerProfile = borrowerProfileService.findByIdWithRealEstateVehicleAndEmployer(borrowerProfileId);
        Document document = modifyHtmlDocument(borrowerProfile, getFileFromPath(FORM_PATH_HTML));
        ByteArrayOutputStream pdfOutputStream = generatePdf(document);
        return processFormHtmlResponse(new ByteArrayResource(pdfOutputStream.toByteArray()));
    }

    @Override
    public ResponseEntity<ByteArrayResource> signatureFormFileHtml(UUID borrowerProfileId, byte[] signature) {
        BorrowerProfile borrowerProfile = borrowerProfileService.findByIdWithRealEstateVehicleAndEmployer(borrowerProfileId);
        String encodedImage = Base64.getEncoder().encodeToString(signature);
        Document document = modifyHtmlDocument(borrowerProfile, getFileFromPath(FORM_PATH_HTML), encodedImage);
        ByteArrayOutputStream pdfOutputStream = generatePdf(document);
        return processFormHtmlResponse(new ByteArrayResource(pdfOutputStream.toByteArray()));
    }

    private Document modifyHtmlDocument(BorrowerProfile borrowerProfile, byte[] file) {
        Map<String, String> replacements = docxFieldHandler.replaceFieldValue(
                borrowerProfile.getPartnerApplication(), borrowerProfile);
        Document document = Jsoup.parse(new String(file));
        replaceDataInHtml(document, replacements, null);
        return document;
    }

    private Document modifyHtmlDocument(BorrowerProfile borrowerProfile, byte[] file, String encodedImage) {
        Map<String, String> replacements = docxFieldHandler.replaceFieldValue(
                borrowerProfile.getPartnerApplication(), borrowerProfile);
        replacements.put("borrowerSign", encodedImage);
        Document document = Jsoup.parse(new String(file));
        replaceDataInHtml(document, replacements, encodedImage);
        return document;
    }


    private ByteArrayOutputStream generatePdf(Document document) {
        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
        PdfDocument pdf = new PdfDocument(new PdfWriter(pdfOutputStream));
        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, createFooterHandler());
        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, createPageNumberHandler());
        HtmlConverter.convertToPdf(document.toString(), pdf, new ConverterProperties());
        pdf.close();
        return pdfOutputStream;
    }

    private IEventHandler createFooterHandler() {
        try {
            InputStream footerImageStream = new ClassPathResource(FOOTER_PATH).getInputStream();
            ImageData footerImgData = ImageDataFactory.create(IOUtils.toByteArray(footerImageStream));
            return new FooterHandler(footerImgData, FIT_WIDTH_IMAGE, FIT_HEIGHT_IMAGE, LEFT_POSITION_IMAGE, BOTTOM_POSITION_IMAGE);
        } catch (IOException e) {
            log.error("Ошибка при загрузке изображения для футера: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось загрузить изображение футера", e);
        }
    }

    private IEventHandler createPageNumberHandler() {
        try {
            PdfFont font = PdfFontFactory.createFont(FONT_PATH, "Cp1251");
            return new PageNumeratorHandler(font, PAGE_NUMBER_FRONT_SIZE, PAGE_NUMBER_RIGHT_OFFSET, PAGE_NUMBER_BOTTOM_OFFSET);
        } catch (IOException e) {
            log.error("Ошибка при загрузке шрифта: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось загрузить шрифт", e);
        }
    }

    private void replaceDataInHtml(Document document, Map<String, String> replacements, String encodedSignatureImage) {
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            Elements elements = document.select("[data-replace-key='" + entry.getKey() + "']");
            for (Element element : elements) {
                if ("borrowerSign".equals(entry.getKey())) {
                    element.empty();
                    Element img = new Element(Tag.valueOf("img"), "");
                    img.attr("src", "data:image/png;base64," + encodedSignatureImage);
                    img.attr("style", "width: 200px; height: 100px; display: block; margin: 0 auto;");
                    element.appendChild(img);
                } else {
                    element.text(entry.getValue());
                }
            }
        }
    }

    private List<Long> getAttachmentIdsByDocumentType(BorrowerProfile borrowerProfile, DocumentType documentType) {
        return borrowerProfile.getBorrowerDocument().stream()
                .filter(d -> d.getDocumentType().equals(documentType))
                .filter(BaseEntity::isActive)
                .map(BorrowerDocument::getAttachment)
                .map(Attachment::getId)
                .collect(Collectors.toList());
    }

    private ResponseEntity<ByteArrayResource> processFormDocxResponse(ByteArrayResource resource) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=example.docx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    private ResponseEntity<ByteArrayResource> processFormHtmlResponse(ByteArrayResource resource) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=example.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_PDF)
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

    //TODO удалить после тестов
    private ByteArrayResource matchReplacementsTest(Map<String, String> replacements, byte[] file) {
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
            int fixedWidth = Units.toEMU(100);
            int fixedHeight = Units.toEMU(50);

            XWPFRun run = paragraph.createRun();
            run.addPicture(new ByteArrayInputStream(imageBytes), imageFormat, "forms/signature.png", fixedWidth, fixedHeight);
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
