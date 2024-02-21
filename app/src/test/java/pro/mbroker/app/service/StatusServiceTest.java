package pro.mbroker.app.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.controller.AttachmentController;
import pro.mbroker.api.dto.request.BankApplicationRequest;
import pro.mbroker.api.dto.request.BorrowerRequest;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.api.enums.BorrowerProfileStatus;
import pro.mbroker.api.enums.DocumentType;
import pro.mbroker.api.enums.PartnerApplicationStatus;
import pro.mbroker.app.TestConstants;
import pro.mbroker.app.TestData;
import pro.mbroker.app.entity.BankApplication;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.entity.PartnerApplication;
import pro.smartdeal.ng.attachment.api.AttachmentRestApi;
import pro.smartdeal.ng.attachment.api.pojo.AttachmentMeta;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pro.mbroker.app.TestConstants.*;

public class StatusServiceTest extends BaseServiceTest {
    @Autowired
    private PartnerApplicationService partnerApplicationService;
    @Autowired
    private BorrowerDocumentService borrowerDocumentService;
    @Autowired
    private BorrowerProfileService borrowerProfileService;
    @Autowired
    private BankApplicationService bankApplicationService;
    @Autowired
    private FormService formService;
    @Autowired
    private AttachmentController attachmentController;
    @Autowired
    private TestData testData;
    @MockBean
    private AttachmentRestApi mockAttachmentRestApi;

    private static UUID borrowerId = UUID.fromString("1348b508-f476-11ed-a05b-0242ac120003");


    @Test
    @SneakyThrows
    public void testBorrowerProfileStatusChangeOnDataUpdateAfterSigning() {
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"additionalIncome\": 10000}", new TypeReference<>() {
        }));
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
    }

    @Test
    @SneakyThrows
    public void testBorrowerProfileStatusChangeOnProfileValue(){
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"mainIncome\": null}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"mainIncome\": 100000}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"proofOfIncome\": null}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"proofOfIncome\": \"TWO_NDFL\"}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"passportIssuedDate\": null}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"passportIssuedDate\": \"2020-01-01\"}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"passportNumber\": null}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"passportNumber\": \"11112222\"}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"passportIssuedByName\": null}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"passportIssuedByName\": \"Отделение Тестового УВД\"}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"passportIssuedByCode\": null}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"passportIssuedByCode\": \"123123\"}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employmentStatus\": null}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employmentStatus\": \"MILITARY\"}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"totalWorkExperience\": null}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"totalWorkExperience\": \"FROM_1_TO_3\"}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employer\": {\"name\": null}}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employer\": {\"name\": \"test_name_employer\"}}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employer\": {\"branch\": null}}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employer\": {\"branch\": \"RESCUE\"}}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employer\": {\"tin\": null}}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employer\": {\"tin\": \"555666677788\"}}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employer\": {\"phone\": null}}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employer\": {\"phone\": \"4556667778\"}}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employer\": {\"numberOfEmployees\": null}}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employer\": {\"numberOfEmployees\": \"LESS_THAN_10\"}}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employer\": {\"organizationAge\": null}}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employer\": {\"organizationAge\": \"LESS_THAN_1\"}}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employer\": {\"address\": null}}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employer\": {\"address\": \"test_address\"}}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employer\": {\"workExperience\": null}}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employer\": {\"workExperience\": \"FROM_1_TO_3\"}}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employer\": {\"position\": null}}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employer\": {\"position\": \"director\"}}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"firstName\": null}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"firstName\": \"test_first_name\"}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"lastName\": null}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"lastName\": \"test_last_name\"}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"phoneNumber\": null}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"phoneNumber\": \"9999999999\"}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"birthdate\": null}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"birthdate\": \"1990-01-01\"}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"gender\": null}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"gender\": \"MALE\"}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"snils\": null}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"snils\": \"8877665544\"}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"registrationAddress\": null}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"registrationAddress\": \"test_address\"}", new TypeReference<>() {}));
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
    }


    @Test
    public void testDefaultStatusesForNewPartnerApplication() {
        PartnerApplication partnerApplication = partnerApplicationService.createPartnerApplication(testData.getPartnerApplicationRequest(), 1234);
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

        borrowerDocumentService.deleteDocumentByAttachmentId(1L, 1234); //delete GENERATED_FORM
        assertEquals(BorrowerProfileStatus.DOCS_SIGNED, getBorrowerProfile(BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        getBorrowerProfile(BORROWER_PROFILE_ID_1).getPartnerApplication().getBankApplications()
                .forEach(bankApplication -> assertEquals(bankApplication.getBankApplicationStatus(), BankApplicationStatus.READY_TO_SENDING));
        assertEquals(PartnerApplicationStatus.UPLOADING_DOCS,
                getBorrowerProfile(BORROWER_PROFILE_ID_1).getPartnerApplication().getPartnerApplicationStatus());
        borrowerDocumentService.deleteDocumentByAttachmentId(2L, 1234); //delete INCOME_CERTIFICATE
        getBorrowerProfile(BORROWER_PROFILE_ID_1).getPartnerApplication().getBankApplications()
                .forEach(bankApplication -> assertEquals(bankApplication.getBankApplicationStatus(), BankApplicationStatus.DATA_NO_ENTERED));
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        uploadDocumentTest(mockMultipartFile, DocumentType.INCOME_CERTIFICATE);
        assertEquals(BorrowerProfileStatus.DOCS_SIGNED, getBorrowerProfile(BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        getBorrowerProfile(BORROWER_PROFILE_ID_1).getPartnerApplication().getBankApplications()
                .forEach(bankApplication -> assertEquals(bankApplication.getBankApplicationStatus(), BankApplicationStatus.READY_TO_SENDING));
        borrowerDocumentService.deleteDocumentByAttachmentId(6L, 1234); //delete GENERATED_SIGNATURE_FORM
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        uploadDocumentTest(mockMultipartFile, DocumentType.GENERATED_SIGNATURE_FORM);
        assertEquals(BorrowerProfileStatus.DOCS_SIGNED, getBorrowerProfile(BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerDocumentService.deleteDocumentByAttachmentId(3L, 1234); //delete CERTIFIED_COPY_TK
        assertEquals(BorrowerProfileStatus.DOCS_SIGNED, getBorrowerProfile(BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerDocumentService.deleteDocumentByAttachmentId(4L, 1234); //delete BORROWER_SNILS
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        uploadDocumentTest(mockMultipartFile, DocumentType.BORROWER_SNILS);
        assertEquals(BorrowerProfileStatus.DOCS_SIGNED, getBorrowerProfile(BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        borrowerDocumentService.deleteDocumentByAttachmentId(5L, 1234); //delete BORROWER_PASSPORT
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
        uploadDocumentTest(mockMultipartFile, DocumentType.BORROWER_PASSPORT);
        assertEquals(BorrowerProfileStatus.DOCS_SIGNED, getBorrowerProfile(BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
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

        borrowerDocumentService.deleteDocumentByAttachmentId(1L, 1234); //delete GENERATED_FORM
        borrowerDocumentService.deleteDocumentByAttachmentId(6L, 1234); //delete GENERATED_SIGNATURE_FORM
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(BORROWER_PROFILE_ID_1).getBorrowerProfileStatus());
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

    @Test
    public void testStatusAddNewBorrowerProfile() {
        bankApplicationService.changeStatus(BANK_APPLICATION_1, BankApplicationStatus.SENT_TO_BANK, 1234);
        BorrowerRequest borrowerRequest = testData.getBorrowerRequest().setId(BANK_APPLICATION_1);
        borrowerRequest.getMainBorrower().setId(BORROWER_PROFILE_ID_1);
        borrowerProfileService.createOrUpdateBorrowerProfile(borrowerRequest, 1111);

        PartnerApplication partnerApplication = partnerApplicationService.getPartnerApplication(PARTNER_APPLICATION_1);
        List<BankApplication> bankApplications = partnerApplication.getBankApplications();
        bankApplications.forEach(bankApplication -> {
            if (bankApplication.getId().equals(BANK_APPLICATION_2)) {
                assertEquals(BankApplicationStatus.DATA_NO_ENTERED, bankApplication.getBankApplicationStatus());
            } else if (bankApplication.getId().equals(BANK_APPLICATION_1)) {
                assertEquals(BankApplicationStatus.SENT_TO_BANK, bankApplication.getBankApplicationStatus());
            }
        });
    }

    @Test
    public void testCREDIT_APPROVEDStatusChangeStatusBankApplication() {
        bankApplicationService.changeStatus(BANK_APPLICATION_1, BankApplicationStatus.CREDIT_APPROVED, 1234);
        PartnerApplication partnerApplication = partnerApplicationService.getPartnerApplication(PARTNER_APPLICATION_1);
        assertEquals(PartnerApplicationStatus.CREDIT_APPROVED, partnerApplication.getPartnerApplicationStatus());
    }

    @Test
    public void testEXPIREDStatusChangeStatusBankApplication() {
        bankApplicationService.changeStatus(BANK_APPLICATION_1, BankApplicationStatus.EXPIRED, 1234);
        bankApplicationService.changeStatus(BANK_APPLICATION_2, BankApplicationStatus.EXPIRED, 1234);
        PartnerApplication partnerApplication = partnerApplicationService.getPartnerApplication(PARTNER_APPLICATION_1);
        assertEquals(PartnerApplicationStatus.EXPIRED, partnerApplication.getPartnerApplicationStatus());
    }

    @Test
    public void testREJECTEDStatusChangeStatusBankApplication() {
        bankApplicationService.changeStatus(BANK_APPLICATION_1, BankApplicationStatus.REJECTED, 1234);
        bankApplicationService.changeStatus(BANK_APPLICATION_2, BankApplicationStatus.REJECTED, 1234);
        PartnerApplication partnerApplication = partnerApplicationService.getPartnerApplication(PARTNER_APPLICATION_1);
        assertEquals(PartnerApplicationStatus.REJECTED, partnerApplication.getPartnerApplicationStatus());
    }

    @Test
    public void testUPLOADING_DOCStatusChangeStatusBankApplication1() {
        bankApplicationService.changeStatus(BANK_APPLICATION_1, BankApplicationStatus.REJECTED, 1234);
        bankApplicationService.changeStatus(BANK_APPLICATION_2, BankApplicationStatus.REJECTED, 1234);
        List<BankApplicationRequest> bankApplications = testData.getPartnerApplicationRequest().getBankApplications();
        bankApplications.add(testData.getBankApplicationRequest());
        partnerApplicationService.updatePartnerApplication(UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34"), testData.getPartnerApplicationRequest().setBankApplications(bankApplications), 1234);
        PartnerApplication partnerApplication = partnerApplicationService.getPartnerApplication(PARTNER_APPLICATION_1);
        assertEquals(PartnerApplicationStatus.UPLOADING_DOCS, partnerApplication.getPartnerApplicationStatus());
    }

    @Test
    public void testUPLOADING_DOCStatusChangeStatusBankApplication2() {
        bankApplicationService.changeStatus(BANK_APPLICATION_1, BankApplicationStatus.EXPIRED, 1234);
        bankApplicationService.changeStatus(BANK_APPLICATION_2, BankApplicationStatus.EXPIRED, 1234);
        List<BankApplicationRequest> bankApplications = testData.getPartnerApplicationRequest().getBankApplications();
        bankApplications.add(testData.getBankApplicationRequest());
        partnerApplicationService.updatePartnerApplication(UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34"), testData.getPartnerApplicationRequest().setBankApplications(bankApplications), 1234);
        PartnerApplication partnerApplication = partnerApplicationService.getPartnerApplication(PARTNER_APPLICATION_1);
        assertEquals(PartnerApplicationStatus.UPLOADING_DOCS, partnerApplication.getPartnerApplicationStatus());
    }

    //TODO написать тест который удаляет созаемщика и это афектит на статус
    public void testStatusDeleteNewBorrowerProfile() {

    }

    private void uploadDocumentTest(MultipartFile multipartFile, DocumentType documentType) {
        attachmentController.uploadDocument(multipartFile,
                BORROWER_PROFILE_ID_1,
                documentType,
                null,
                null,
                1234);
    }

    private BorrowerProfile getBorrowerProfile(UUID id) {
        return borrowerProfileService.findByIdWithRealEstateVehicleAndEmployer(id);
    }

}
