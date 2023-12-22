package pro.mbroker.app.service;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pro.mbroker.api.dto.request.PartnerApplicationRequest;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.Insurance;
import pro.mbroker.api.enums.PartnerApplicationStatus;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.app.TestData;
import pro.mbroker.app.entity.PartnerApplication;
import pro.mbroker.app.util.Converter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PartnerApplicationServiceTest extends AbstractServiceTest {
    @Autowired
    private PartnerApplicationService partnerApplicationService;
    @Autowired
    private TestData testData;

    @Test
    public void testCreatePartnerApplication() {
        PartnerApplication partnerApplication = partnerApplicationService.createPartnerApplication(testData.getPartnerApplicationRequest());
        assertEquals(partnerApplication.getPartner().getId(), UUID.fromString("5fec2326-d92e-11ed-afa1-0242ac120002"));
        assertEquals(partnerApplication.getCreditPurposeType(), CreditPurposeType.PURCHASE_UNDER_CONSTRUCTION);
        assertEquals(partnerApplication.getPartnerApplicationStatus(), PartnerApplicationStatus.UPLOADING_DOCS);
        assertEquals(Converter.convertStringListToEnumList(partnerApplication.getRealEstateTypes(), RealEstateType.class), List.of(RealEstateType.APARTMENT, RealEstateType.ROOM));
        assertEquals(partnerApplication.getRealEstate().getId(), UUID.fromString("2b8850b2-d930-11ed-afa1-0242ac120002"));
        assertThat(partnerApplication.getBankApplications().size(), Matchers.is(2));
        assertThat(partnerApplication.getBorrowerProfiles().size(), Matchers.is(1));
    }

    @Test
    public void testUpdatePartnerApplication() {
        PartnerApplication partnerApplicationBefore =
                partnerApplicationService.getPartnerApplication(UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34"));
        LocalDateTime updateAtBefore = partnerApplicationBefore.getUpdatedAt();
        PartnerApplicationRequest partnerApplicationNew = testData.getPartnerApplicationRequest()
                .setInsurances(List.of(Insurance.LIFE_INSURANCE, Insurance.TITLE_INSURANCE));
        partnerApplicationNew.getMainBorrower()
                .setId(UUID.fromString("1348b508-f476-11ed-a05b-0242ac120003"));
        PartnerApplication partnerApplication =
                partnerApplicationService.updatePartnerApplication(UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34"), partnerApplicationNew);
        assertEquals(partnerApplication.getPartner().getId(), UUID.fromString("5fec2326-d92e-11ed-afa1-0242ac120002"));
        assertEquals(partnerApplication.getCreditPurposeType(), CreditPurposeType.PURCHASE_UNDER_CONSTRUCTION);
        assertEquals(partnerApplication.getPartnerApplicationStatus(), PartnerApplicationStatus.UPLOADING_DOCS);
        assertEquals(Converter.convertStringListToEnumList(partnerApplication.getRealEstateTypes(), RealEstateType.class), List.of(RealEstateType.APARTMENT, RealEstateType.ROOM));
        assertEquals(partnerApplication.getRealEstate().getId(), UUID.fromString("2b8850b2-d930-11ed-afa1-0242ac120002"));
        assertThat(partnerApplication.getBankApplications().size(), Matchers.is(2));
        assertThat(partnerApplication.getBorrowerProfiles().size(), Matchers.is(1));
        assertTrue(partnerApplication.getUpdatedAt().isAfter(updateAtBefore));
    }

    @Test
    public void testShortUpdatePartnerApplication() {
        PartnerApplication partnerApplicationBefore =
                partnerApplicationService.getPartnerApplication(UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34"));
        LocalDateTime updateAtBefore = partnerApplicationBefore.getUpdatedAt();
        PartnerApplication partnerApplicationAfterUpdate =
                partnerApplicationService.updatePartnerApplication(UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34"),
                        testData.getShortPartnerApplicationRequest());
        assertNotEquals(partnerApplicationAfterUpdate.getUpdatedAt(), updateAtBefore);

        assertEquals(partnerApplicationAfterUpdate.getInsurances(), "LIFE_INSURANCE,TITLE_INSURANCE");
        assertEquals(partnerApplicationAfterUpdate.getPaymentSource(), "APARTMENT_SALE,MILITARY_MORTGAGE");
        assertEquals(partnerApplicationAfterUpdate.getMaternalCapitalAmount(), BigDecimal.valueOf(120000));
        assertEquals(partnerApplicationAfterUpdate.getSubsidyAmount(), BigDecimal.valueOf(30000));

        assertEquals(partnerApplicationBefore.getCreditPurposeType(), partnerApplicationAfterUpdate.getCreditPurposeType());
        assertEquals(partnerApplicationBefore.getPartnerApplicationStatus(), partnerApplicationAfterUpdate.getPartnerApplicationStatus());
        assertEquals(partnerApplicationBefore.getRealEstateTypes(), partnerApplicationAfterUpdate.getRealEstateTypes());
        assertEquals(partnerApplicationBefore.getRealEstate(), partnerApplicationAfterUpdate.getRealEstate());
        assertEquals(partnerApplicationBefore.getBankApplications(), partnerApplicationAfterUpdate.getBankApplications());
        assertEquals(partnerApplicationBefore.getBorrowerProfiles(), partnerApplicationAfterUpdate.getBorrowerProfiles());
        assertEquals(partnerApplicationBefore.getMortgageCalculation(), partnerApplicationAfterUpdate.getMortgageCalculation());
        assertEquals(partnerApplicationBefore.getCreatedAt(), partnerApplicationAfterUpdate.getCreatedAt());
        assertEquals(partnerApplicationBefore.getCreatedBy(), partnerApplicationAfterUpdate.getCreatedBy());
        assertEquals(partnerApplicationBefore.isActive(), partnerApplicationAfterUpdate.isActive());

        PartnerApplication partnerApplicationAfterUpdateWithNullValues =
                partnerApplicationService.updatePartnerApplication(UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34"),
                        testData.getShortPartnerApplicationRequestWithNullValues());

        Assert.assertNull(partnerApplicationAfterUpdateWithNullValues.getInsurances());
        Assert.assertNull(partnerApplicationAfterUpdateWithNullValues.getPaymentSource());
        assertEquals(partnerApplicationAfterUpdateWithNullValues.getMaternalCapitalAmount(), BigDecimal.valueOf(0));
        assertEquals(partnerApplicationAfterUpdateWithNullValues.getSubsidyAmount(), BigDecimal.valueOf(0));
        assertEquals(partnerApplicationBefore.getCreditPurposeType(), partnerApplicationAfterUpdateWithNullValues.getCreditPurposeType());
        assertEquals(partnerApplicationBefore.getPartnerApplicationStatus(), partnerApplicationAfterUpdateWithNullValues.getPartnerApplicationStatus());
        assertEquals(partnerApplicationBefore.getRealEstateTypes(), partnerApplicationAfterUpdateWithNullValues.getRealEstateTypes());
        assertEquals(partnerApplicationBefore.getRealEstate(), partnerApplicationAfterUpdateWithNullValues.getRealEstate());
        assertEquals(partnerApplicationBefore.getBankApplications(), partnerApplicationAfterUpdateWithNullValues.getBankApplications());
        assertEquals(partnerApplicationBefore.getBorrowerProfiles(), partnerApplicationAfterUpdateWithNullValues.getBorrowerProfiles());
        assertEquals(partnerApplicationBefore.getMortgageCalculation(), partnerApplicationAfterUpdateWithNullValues.getMortgageCalculation());
        assertEquals(partnerApplicationBefore.getCreatedAt(), partnerApplicationAfterUpdateWithNullValues.getCreatedAt());
        assertEquals(partnerApplicationBefore.getCreatedBy(), partnerApplicationAfterUpdateWithNullValues.getCreatedBy());
        assertEquals(partnerApplicationBefore.isActive(), partnerApplicationAfterUpdateWithNullValues.isActive());
    }

    @Test
    public void testGetPartnerApplication() {
        LocalDateTime now = LocalDateTime.now();
        PartnerApplication partnerApplication =
                partnerApplicationService.getPartnerApplication(UUID.fromString("7addcbef-c1e0-4de1-adeb-377f864efcfa"));
        assertTrue(partnerApplication.getUpdatedAt().isBefore(now));
        assertEquals(partnerApplication.getPartner().getId(), UUID.fromString("dce73f3e-f2db-11ed-a05b-0242ac120003"));
        assertEquals(partnerApplication.getCreditPurposeType(), CreditPurposeType.PURCHASE_UNDER_CONSTRUCTION);
        assertEquals(Converter.convertStringListToEnumList(partnerApplication.getRealEstateTypes(), RealEstateType.class), List.of(RealEstateType.APARTMENT));
        assertEquals(partnerApplication.getRealEstate().getId(), UUID.fromString("2b8850b2-d930-11ed-afa1-0242ac120002"));
        assertThat(partnerApplication.getBankApplications().size(), Matchers.is(1));
        assertThat(partnerApplication.getBorrowerProfiles().size(), Matchers.is(1));
    }

    @Test
    public void testChangeBankApplication() {
        PartnerApplication partnerApplication =
                partnerApplicationService.disableBankApplication(UUID.fromString("7addcbef-c1e0-4de1-adeb-377f864efcfa"), UUID.fromString("8222cb80-d928-11ed-afa1-0242ac120002"));
        assertThat(partnerApplication.getBankApplications().get(0).isActive(), Matchers.is(false));
        partnerApplicationService.enableBankApplication(UUID.fromString("7addcbef-c1e0-4de1-adeb-377f864efcfa"), testData.getBankApplicationUpdateRequest());
        assertThat(partnerApplication.getBankApplications().get(0).isActive(), Matchers.is(true));
    }
}
