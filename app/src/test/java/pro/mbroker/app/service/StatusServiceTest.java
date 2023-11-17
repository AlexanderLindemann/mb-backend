package pro.mbroker.app.service;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.controller.AttachmentController;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.api.enums.BorrowerProfileStatus;
import pro.mbroker.api.enums.DocumentType;
import pro.mbroker.api.enums.PartnerApplicationStatus;
import pro.mbroker.app.TestConstants;
import pro.mbroker.app.TestData;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.entity.PartnerApplication;
import pro.smartdeal.ng.attachment.api.AttachmentRestApi;
import pro.smartdeal.ng.attachment.api.pojo.AttachmentMeta;

import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatusServiceTest extends AbstractServiceTest {
    @Autowired
    private PartnerApplicationService partnerApplicationService;
    @Autowired
    private BorrowerDocumentService borrowerDocumentService;
    @Autowired
    private BorrowerProfileService borrowerProfileService;
    @Autowired
    private FormService formService;
    @Autowired
    private AttachmentController attachmentController;
    @Autowired
    private TestData testData;
    @MockBean
    private AttachmentRestApi mockAttachmentRestApi;


    @Test
    public void testDefaultStatusesForNewPartnerApplication() {
        PartnerApplication partnerApplication = partnerApplicationService.createPartnerApplication(testData.getPartnerApplicationRequest());
        assertEquals(partnerApplication.getPartnerApplicationStatus(), PartnerApplicationStatus.UPLOADING_DOCS);
        partnerApplication.getBankApplications()
                .forEach(bankApplication -> assertEquals(bankApplication.getBankApplicationStatus(), BankApplicationStatus.DATA_NO_ENTERED));
        partnerApplication.getBorrowerProfiles()
                .forEach(borrowerProfile -> assertEquals(borrowerProfile.getBorrowerProfileStatus(), BorrowerProfileStatus.DATA_NO_ENTERED));
    }

    @Test
    public void testDeleteUploadDocumentTestStatus() {
        MockMultipartFile mockMultipartFile = testData.getMockMultipartFile();

        Mockito.when(mockAttachmentRestApi.upload(mockMultipartFile))
                .thenAnswer(invocation -> new AttachmentMeta()
                        .setId(new Random().nextLong())
                        .setName("filename.txt")
                        .setMimeType("mimeType")
                        .setSizeBytes(123456)
                        .setMd5Hash("contentMD"));

        borrowerDocumentService.deleteDocumentByAttachmentId(1L); //delete GENERATED_FORM
        assertEquals(BorrowerProfileStatus.DOCS_SIGNED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getPartnerApplication().getBankApplications()
                .forEach(bankApplication -> assertEquals(bankApplication.getBankApplicationStatus(), BankApplicationStatus.READY_TO_SENDING));
        assertEquals(PartnerApplicationStatus.UPLOADING_DOCS,
                getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getPartnerApplication().getPartnerApplicationStatus());
        borrowerDocumentService.deleteDocumentByAttachmentId(2L); //delete INCOME_CERTIFICATE
        getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getPartnerApplication().getBankApplications()
                .forEach(bankApplication -> assertEquals(bankApplication.getBankApplicationStatus(), BankApplicationStatus.DATA_NO_ENTERED));
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        uploadDocumentTest(mockMultipartFile, DocumentType.INCOME_CERTIFICATE);
        assertEquals(BorrowerProfileStatus.DOCS_SIGNED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getPartnerApplication().getBankApplications()
                .forEach(bankApplication -> assertEquals(bankApplication.getBankApplicationStatus(), BankApplicationStatus.READY_TO_SENDING));
        borrowerDocumentService.deleteDocumentByAttachmentId(6L); //delete GENERATED_SIGNATURE_FORM
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        uploadDocumentTest(mockMultipartFile, DocumentType.GENERATED_SIGNATURE_FORM);
        assertEquals(BorrowerProfileStatus.DOCS_SIGNED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        borrowerDocumentService.deleteDocumentByAttachmentId(3L); //delete CERTIFIED_COPY_TK
        assertEquals(BorrowerProfileStatus.DOCS_SIGNED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        borrowerDocumentService.deleteDocumentByAttachmentId(4L); //delete BORROWER_SNILS
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        uploadDocumentTest(mockMultipartFile, DocumentType.BORROWER_SNILS);
        assertEquals(BorrowerProfileStatus.DOCS_SIGNED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        borrowerDocumentService.deleteDocumentByAttachmentId(5L); //delete BORROWER_PASSPORT
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        uploadDocumentTest(mockMultipartFile, DocumentType.BORROWER_PASSPORT);
        assertEquals(BorrowerProfileStatus.DOCS_SIGNED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
    }

    @Test
    public void testDeleteUploadSignatureFormDocumentTestStatus() {
        MockMultipartFile mockMultipartFile = testData.getMockMultipartFile();

        Mockito.when(mockAttachmentRestApi.upload(mockMultipartFile))
                .thenAnswer(invocation -> new AttachmentMeta()
                        .setId(new Random().nextLong())
                        .setName("filename.txt")
                        .setMimeType("mimeType")
                        .setSizeBytes(123456)
                        .setMd5Hash("contentMD"));

        borrowerDocumentService.deleteDocumentByAttachmentId(1L); //delete GENERATED_FORM
        borrowerDocumentService.deleteDocumentByAttachmentId(6L); //delete GENERATED_SIGNATURE_FORM
        uploadDocumentTest(mockMultipartFile, DocumentType.SIGNATURE_FORM);
        assertEquals(BorrowerProfileStatus.DOCS_SIGNED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
    }

    //TODO тест для генерации анкеты (проверяет статус BorrowerProfile, если клиент генерирует новую анкет) - не работает мок
    @Test
    public void testStatusWithGeneratedForm() {
//        MockMultipartFile mockMultipartFile = testData.getMockMultipartFile();
//        Mockito.when(mockAttachmentRestApi.upload(mockMultipartFile))
//                .thenReturn(new AttachmentMeta()
//                        .setId(new Random().nextLong())
//                        .setName("filename.txt")
//                        .setMimeType("mimeType")
//                        .setSizeBytes(123456)
//                        .setMd5Hash("contentMD"));
//
//        byte[] specificData = "Тестовые данные".getBytes(StandardCharsets.UTF_8);
//        formService.updateGeneratedForm(TestConstants.BORROWER_PROFILE_ID, specificData);
//        BorrowerProfile borrowerProfile = getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID);
//        assertEquals(BorrowerProfileStatus.DATA_ENTERED, borrowerProfile.getBorrowerProfileStatus());
    }

    //TODO тест для генерации анкеты (проверяет статус BorrowerProfile, если клиент генерирует новую анкет и у него есть бумажная подписанная анкета) - не работает мок
    @Test
    public void testStatusGeneratedFormWithSignatureForm() {
//        MockMultipartFile mockMultipartFile = testData.getMockMultipartFile();
//        Mockito.when(mockAttachmentRestApi.upload(mockMultipartFile))
//                .thenReturn(new AttachmentMeta()
//                        .setId(new Random().nextLong())
//                        .setName("filename.txt")
//                        .setMimeType("mimeType")
//                        .setSizeBytes(123456)
//                        .setMd5Hash("contentMD"));
//
//        byte[] specificData = "Тестовые данные".getBytes(StandardCharsets.UTF_8);
//        uploadDocumentTest(mockMultipartFile, DocumentType.SIGNATURE_FORM);
//        formService.updateGeneratedForm(TestConstants.BORROWER_PROFILE_ID, specificData);
//        assertEquals(BorrowerProfileStatus.DOCS_SIGNED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
    }

    //TODO написать тест который добавляет созаемщика и это афектит на статус
    @Test
    public void testStatusAddNewBorrowerProfile() {

    }

    //TODO написать тест на смену статуса в методе changeStatus в BankApplication
    @Test
    public void testStatusChangeStatusBankApplication() {

    }

    private void uploadDocumentTest(MultipartFile multipartFile, DocumentType documentType) {
        attachmentController.uploadDocument(multipartFile,
                TestConstants.BORROWER_PROFILE_ID,
                documentType,
                null,
                null);
    }

    private BorrowerProfile getBorrowerProfile(UUID id) {
        return borrowerProfileService.findByIdWithRealEstateVehicleAndEmployer(id);
    }

}
