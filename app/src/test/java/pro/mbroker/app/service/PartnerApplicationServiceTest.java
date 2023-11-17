package pro.mbroker.app.service;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pro.mbroker.api.dto.request.PartnerApplicationRequest;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.PartnerApplicationStatus;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.app.TestData;
import pro.mbroker.app.entity.PartnerApplication;
import pro.mbroker.app.repository.PartnerApplicationRepository;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PartnerApplicationServiceTest extends AbstractServiceTest {
    @Autowired
    private PartnerApplicationService partnerApplicationService;
    @Autowired
    private PartnerApplicationRepository repository;
    @Autowired
    private TestData testData;

    @Test
    public void testCreatePartnerApplication() {
        PartnerApplication partnerApplication = partnerApplicationService.createPartnerApplication(testData.getPartnerApplicationRequest());
        assertEquals(partnerApplication.getPartner().getId(), UUID.fromString("5fec2326-d92e-11ed-afa1-0242ac120002"));
        assertEquals(partnerApplication.getCreditPurposeType(), CreditPurposeType.PURCHASE_UNDER_CONSTRUCTION);
        assertEquals(partnerApplication.getPartnerApplicationStatus(), PartnerApplicationStatus.UPLOADING_DOCS);
        assertEquals(partnerApplication.getRealEstateType(), RealEstateType.APARTMENT);
        assertEquals(partnerApplication.getRealEstate().getId(), UUID.fromString("2b8850b2-d930-11ed-afa1-0242ac120002"));
        assertThat(partnerApplication.getBankApplications().size(), Matchers.is(2));
        assertThat(partnerApplication.getBorrowerProfiles().size(), Matchers.is(1));
    }

    @Test
    public void testUpdatePartnerApplication() {
        PartnerApplicationRequest partnerApplication1 = testData.getPartnerApplicationRequest();
        partnerApplication1.getMainBorrower()
                .setId(UUID.fromString("1348b508-f476-11ed-a05b-0242ac120003"));
        PartnerApplication partnerApplication =
                partnerApplicationService.updatePartnerApplication(UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34"), partnerApplication1);
        assertEquals(partnerApplication.getPartner().getId(), UUID.fromString("5fec2326-d92e-11ed-afa1-0242ac120002"));
        assertEquals(partnerApplication.getCreditPurposeType(), CreditPurposeType.PURCHASE_UNDER_CONSTRUCTION);
        assertEquals(partnerApplication.getPartnerApplicationStatus(), PartnerApplicationStatus.UPLOADING_DOCS);
        assertEquals(partnerApplication.getRealEstateType(), RealEstateType.APARTMENT);
        assertEquals(partnerApplication.getRealEstate().getId(), UUID.fromString("2b8850b2-d930-11ed-afa1-0242ac120002"));
        assertThat(partnerApplication.getBankApplications().size(), Matchers.is(2));
        assertThat(partnerApplication.getBorrowerProfiles().size(), Matchers.is(1));
    }

    @Test
    public void testGetPartnerApplication() {
        PartnerApplication partnerApplication =
                partnerApplicationService.getPartnerApplication(UUID.fromString("7addcbef-c1e0-4de1-adeb-377f864efcfa"));
        assertEquals(partnerApplication.getPartner().getId(), UUID.fromString("dce73f3e-f2db-11ed-a05b-0242ac120003"));
        assertEquals(partnerApplication.getCreditPurposeType(), CreditPurposeType.PURCHASE_UNDER_CONSTRUCTION);
        assertEquals(partnerApplication.getRealEstateType(), RealEstateType.APARTMENT);
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
