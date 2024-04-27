package pro.mbroker.app.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import pro.mbroker.api.controller.CreditProgramController;
import pro.mbroker.api.dto.request.CreditProgramServiceRequest;
import pro.mbroker.api.dto.response.CreditProgramResponse;
import pro.mbroker.api.enums.CreditProgramType;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.api.enums.RegionType;
import pro.mbroker.app.TestConstants;
import pro.mbroker.app.TestData;

import java.util.Set;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CreditProgramServiceTest extends BaseServiceTest {

    @Autowired
    private CreditProgramController creditProgramController;

    @Autowired
    private TestData testData;

    @Test
    void loadCreditProgramFromCian() {
    }

    @Test
    public void testFilterCreditProgramsByBankId() {
        CreditProgramServiceRequest creditProgramServiceRequest =
                testData.createCreditProgramServiceRequest()
                        .setBanks(Set.of(TestConstants.BANK_ID1));
        Page<CreditProgramResponse> allCreditProgram = creditProgramController.getAllCreditProgram(creditProgramServiceRequest);
        assertNotNull(allCreditProgram);
        assertTrue(allCreditProgram.hasContent(), "Результат не содержит элементов");
        allCreditProgram.forEach(creditProgramResponse -> {
            assertNotNull(creditProgramResponse.getBank(), "Банк в ответе не должен быть null");
            assertEquals(TestConstants.BANK_ID1, creditProgramResponse.getBank().getId(),
                    "UUID банка не соответствует ожидаемому");
        });
    }

    @Test
    public void testFilterCreditProgramsByBankIds() {
        CreditProgramServiceRequest creditProgramServiceRequest =
                testData.createCreditProgramServiceRequest()
                        .setBanks(Set.of(TestConstants.BANK_ID1, TestConstants.BANK_ID2));
        Page<CreditProgramResponse> allCreditProgram = creditProgramController.getAllCreditProgram(creditProgramServiceRequest);
        assertNotNull(allCreditProgram);
        assertTrue(allCreditProgram.hasContent(), "Результат не содержит элементов");
        allCreditProgram.forEach(creditProgramResponse -> {
            assertNotNull(creditProgramResponse.getBank(), "Банк в ответе не должен быть null");
            assertTrue(
                    Set.of(TestConstants.BANK_ID1, TestConstants.BANK_ID2).contains(creditProgramResponse.getBank().getId()),
                    "Программа кредитования принадлежит банку, который не соответствует ни одному из ожидаемых идентификаторов");
        });
    }

    @Test
    public void testFilterCreditProgramsByCreditPurposeType() {
        CreditProgramServiceRequest creditProgramServiceRequest =
                testData.createCreditProgramServiceRequest()
                        .setCreditPurposeTypes(Set.of(CreditPurposeType.PURCHASE_UNDER_CONSTRUCTION));
        Page<CreditProgramResponse> allCreditPrograms = creditProgramController.getAllCreditProgram(creditProgramServiceRequest);
        assertNotNull(allCreditPrograms);
        assertTrue(allCreditPrograms.hasContent(), "Результат не содержит элементов");
        allCreditPrograms.forEach(creditProgramResponse -> {
            assertTrue(creditProgramResponse.getCreditProgramDetail().getCreditPurposeType().contains(CreditPurposeType.PURCHASE_UNDER_CONSTRUCTION),
                    "Список целей кредита не содержит PURCHASE_UNDER_CONSTRUCTION");
        });
    }

    @Test
    public void testFilterCreditProgramsByCreditPurposeTypes() {
        CreditProgramServiceRequest creditProgramServiceRequest =
                testData.createCreditProgramServiceRequest()
                        .setCreditPurposeTypes(Set.of(CreditPurposeType.PURCHASE_READY_HOUSE, CreditPurposeType.PURCHASE_UNDER_CONSTRUCTION));
        Page<CreditProgramResponse> allCreditPrograms = creditProgramController.getAllCreditProgram(creditProgramServiceRequest);
        assertNotNull(allCreditPrograms);
        assertTrue(allCreditPrograms.hasContent(), "Результат не содержит элементов");
        allCreditPrograms.forEach(creditProgramResponse -> {
            assertTrue(creditProgramResponse.getCreditProgramDetail().getCreditPurposeType().contains(CreditPurposeType.PURCHASE_READY_HOUSE)
                            || creditProgramResponse.getCreditProgramDetail().getCreditPurposeType().contains(CreditPurposeType.PURCHASE_UNDER_CONSTRUCTION),
                    "Список целей кредита не содержит PURCHASE_READY_HOUSE или PURCHASE_UNDER_CONSTRUCTION");
        });
    }

    @Test
    public void testFilterCreditProgramsByCreditProgramType() {
        CreditProgramServiceRequest creditProgramServiceRequest =
                testData.createCreditProgramServiceRequest()
                        .setCreditProgramTypes(Set.of(CreditProgramType.STANDARD));
        Page<CreditProgramResponse> allCreditPrograms = creditProgramController.getAllCreditProgram(creditProgramServiceRequest);
        assertNotNull(allCreditPrograms);
        assertTrue(allCreditPrograms.hasContent(), "Результат не содержит элементов");
        allCreditPrograms.forEach(creditProgramResponse -> {
            assertTrue(creditProgramResponse.getCreditProgramDetail().getCreditProgramType().equals(CreditProgramType.STANDARD),
                    "Тип кредитования не соответствует ожидаемому типу кредитной программы");
        });
    }

    @Test
    public void testFilterCreditProgramsByCreditProgramTypes() {
        CreditProgramServiceRequest creditProgramServiceRequest =
                testData.createCreditProgramServiceRequest()
                        .setCreditProgramTypes(Set.of(CreditProgramType.STANDARD, CreditProgramType.IT));
        Page<CreditProgramResponse> allCreditPrograms = creditProgramController.getAllCreditProgram(creditProgramServiceRequest);
        assertNotNull(allCreditPrograms);
        assertTrue(allCreditPrograms.hasContent(), "Результат не содержит элементов");
        allCreditPrograms.forEach(creditProgramResponse -> {
            assertTrue(creditProgramResponse.getCreditProgramDetail().getCreditProgramType().equals(CreditProgramType.STANDARD)
                            || creditProgramResponse.getCreditProgramDetail().getCreditProgramType().equals(CreditProgramType.IT),
                    "Список типов кредитования не соответствует ожидаемому типу кредитной программы");
        });
    }

    @Test
    public void testFilterCreditProgramsByAPARTMENTRealEstateType() {
        CreditProgramServiceRequest creditProgramServiceRequest =
                testData.createCreditProgramServiceRequest()
                        .setRealEstateTypes(Set.of(RealEstateType.APARTMENT));
        Page<CreditProgramResponse> allCreditPrograms = creditProgramController.getAllCreditProgram(creditProgramServiceRequest);
        assertNotNull(allCreditPrograms);
        assertTrue(allCreditPrograms.hasContent(), "Результат не содержит элементов");
        allCreditPrograms.forEach(creditProgramResponse -> {
            assertTrue(creditProgramResponse.getCreditProgramDetail().getRealEstateType().contains(RealEstateType.APARTMENT),
                    "Список типов кредитования не соответствует ожидаемому типу кредитной программы");
        });
    }

    @Test
    public void testFilterCreditProgramsByHOUSE_WITH_LANDRealEstateType() {
        CreditProgramServiceRequest creditProgramServiceRequest =
                testData.createCreditProgramServiceRequest()
                        .setRealEstateTypes(Set.of(RealEstateType.HOUSE_WITH_LAND));
        Page<CreditProgramResponse> allCreditPrograms = creditProgramController.getAllCreditProgram(creditProgramServiceRequest);
        assertNotNull(allCreditPrograms);
        assertTrue(allCreditPrograms.hasContent(), "Результат не содержит элементов");
        allCreditPrograms.forEach(creditProgramResponse -> {
            assertTrue(creditProgramResponse.getCreditProgramDetail().getRealEstateType().contains(RealEstateType.HOUSE_WITH_LAND),
                    "Список типов кредитования не соответствует ожидаемому типу кредитной программы");
        });
    }

    @Test
    public void testFilterCreditProgramsByRealEstateTypes() {
        CreditProgramServiceRequest creditProgramServiceRequest =
                testData.createCreditProgramServiceRequest()
                        .setRealEstateTypes(Set.of(RealEstateType.HOUSE_WITH_LAND, RealEstateType.APARTMENT));
        Page<CreditProgramResponse> allCreditPrograms = creditProgramController.getAllCreditProgram(creditProgramServiceRequest);
        assertNotNull(allCreditPrograms);
        assertTrue(allCreditPrograms.hasContent(), "Результат не содержит элементов");
        allCreditPrograms.forEach(creditProgramResponse -> {
            assertTrue(creditProgramResponse.getCreditProgramDetail().getRealEstateType().contains(RealEstateType.HOUSE_WITH_LAND)
                            || creditProgramResponse.getCreditProgramDetail().getRealEstateType().contains(RealEstateType.APARTMENT),
                    "Список типов кредитования не соответствует ожидаемому типу кредитной программы");
        });
    }

    @Test
    public void testFilterCreditProgramsByRegion() {
        CreditProgramServiceRequest creditProgramServiceRequest =
                testData.createCreditProgramServiceRequest()
                        .setRegions(Set.of(RegionType.MOSCOW));
        Page<CreditProgramResponse> allCreditPrograms = creditProgramController.getAllCreditProgram(creditProgramServiceRequest);
        assertNotNull(allCreditPrograms);
        assertTrue(allCreditPrograms.hasContent(), "Результат не содержит элементов");
        allCreditPrograms.forEach(creditProgramResponse -> {
            assertTrue(creditProgramResponse.getCreditProgramDetail().getInclude().contains(RegionType.MOSCOW),
                    "Список регионов не соответствует ожидаемому региону RegionType.MOSCOW");
        });
    }

    @Test
    public void testFilterCreditProgramsByRegions() {
        CreditProgramServiceRequest creditProgramServiceRequest =
                testData.createCreditProgramServiceRequest()
                        .setRegions(Set.of(RegionType.SVERDLOVSK, RegionType.ROSTOV));
        Page<CreditProgramResponse> allCreditPrograms = creditProgramController.getAllCreditProgram(creditProgramServiceRequest);
        assertNotNull(allCreditPrograms);
        assertTrue(allCreditPrograms.hasContent(), "Результат не содержит элементов");
        allCreditPrograms.forEach(creditProgramResponse -> {
            assertTrue(creditProgramResponse.getCreditProgramDetail().getInclude().contains(RegionType.SVERDLOVSK)
                            || creditProgramResponse.getCreditProgramDetail().getInclude().contains(RegionType.ROSTOV),
                    "Список регионов не соответствует ожидаемому региону RegionType.ROSTOV, RegionType.SVERDLOVSK");
        });
    }

    @Test
    public void testFilterCreditProgramsByCianId() {
        CreditProgramServiceRequest creditProgramServiceRequest =
                testData.createCreditProgramServiceRequest()
                        .setCianId(2233);
        Page<CreditProgramResponse> allCreditPrograms = creditProgramController.getAllCreditProgram(creditProgramServiceRequest);
        assertNotNull(allCreditPrograms);
        assertTrue(allCreditPrograms.hasContent(), "Результат не содержит элементов");
        allCreditPrograms.forEach(creditProgramResponse -> {
            assertTrue(creditProgramResponse.getCianId().equals(2233),
                    "Список регионов не соответствует ожидаемому региону RegionType.ROSTOV, RegionType.SVERDLOVSK");
        });
    }

    @Test
    public void testFilterCreditProgramsByAllOptions() {
        CreditProgramServiceRequest creditProgramServiceRequest =
                testData.createCreditProgramServiceRequest()
                        .setCreditProgramTypes(Set.of(CreditProgramType.STANDARD))
                        .setRealEstateTypes(Set.of(RealEstateType.APARTMENT))
                        .setBanks(Set.of(TestConstants.BANK_ID1))
                        .setCreditPurposeTypes(Set.of(CreditPurposeType.PURCHASE_READY_HOUSE, CreditPurposeType.PURCHASE_UNDER_CONSTRUCTION))
                        .setRegions(Set.of(RegionType.SVERDLOVSK, RegionType.MOSCOW))
                        .setCianId(3344);
        Page<CreditProgramResponse> allCreditPrograms = creditProgramController.getAllCreditProgram(creditProgramServiceRequest);
        assertNotNull(allCreditPrograms);
        assertTrue(allCreditPrograms.hasContent(), "Результат не содержит элементов");
        allCreditPrograms.forEach(creditProgramResponse -> {
            assertTrue(creditProgramResponse.getCreditProgramDetail().getInclude().contains(RegionType.SVERDLOVSK)
                            || creditProgramResponse.getCreditProgramDetail().getInclude().contains(RegionType.MOSCOW),
                    "Список регионов не соответствует ожидаемому региону RegionType.ROSTOV, RegionType.SVERDLOVSK");
            assertEquals(creditProgramResponse.getCreditProgramDetail().getCreditProgramType(), CreditProgramType.STANDARD,
                    "Тип кредитной программы не соответствует ожидаемому типу CreditProgramType.STANDARD");
            assertTrue(creditProgramResponse.getCreditProgramDetail().getRealEstateType().contains(RealEstateType.APARTMENT),
                    "Тип недвижимости не содержит RealEstateType.APARTMENT");
            assertEquals(creditProgramResponse.getBank().getId(), TestConstants.BANK_ID1,
                    "Банк кредитной программы не соответствует переданному UUID банка");
            assertTrue(creditProgramResponse.getCreditProgramDetail().getCreditPurposeType().contains(CreditPurposeType.PURCHASE_READY_HOUSE)
                            || creditProgramResponse.getCreditProgramDetail().getCreditPurposeType().contains(CreditPurposeType.PURCHASE_UNDER_CONSTRUCTION),
                    "Цель кредита не соответствует CreditPurposeType.PURCHASE_READY_HOUSE или CreditPurposeType.PURCHASE_UNDER_CONSTRUCTION");
            assertEquals(creditProgramResponse.getCianId(), 3344,
                    "Cian_id не соответствует переданному cian_id: 3344");

        });
    }
}