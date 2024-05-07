package pro.mbroker.app.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pro.mbroker.api.dto.request.PartnerContactRequest;
import pro.mbroker.api.dto.request.PartnerRequest;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.NotificationTrigger;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.app.TestConstants;
import pro.mbroker.app.TestData;
import pro.mbroker.app.entity.BaseEntity;
import pro.mbroker.app.entity.CreditProgram;
import pro.mbroker.app.entity.Partner;
import pro.mbroker.app.entity.RealEstate;
import pro.mbroker.app.util.Converter;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

public class PartnerServiceTest extends BaseServiceTest {
    @Autowired
    private PartnerService partnerService;
    @Autowired
    private TestData testData;

    @Test
    public void testCreatePartner() {
        PartnerRequest request = testData.createPartnerRequest();
        Partner partner = partnerService.createPartner(request, 1234);
        asserPartner(request, partner);
    }

    @Test
    public void testUpdatePartner() {
        PartnerRequest request = testData.createPartnerRequest();
        request.setRealEstateType(List.of(RealEstateType.HOUSE_WITH_LAND, RealEstateType.TOWNHOUSE));
        request.setCreditPurposeType(List.of(CreditPurposeType.PURCHASE_READY_HOUSE));
        request.setRealEstateRequest(testData.createPartnerRequest().getRealEstateRequest());
        Partner updatedPartner = partnerService.updatePartnerById(TestConstants.PARTNER_ID_1, request, 1234);
        assertEquals(request.getSmartDealOrganizationId(), updatedPartner.getSmartDealOrganizationId());
        assertEquals(request.getName(), updatedPartner.getName());
        assertEquals(request.getType(), updatedPartner.getType());
        assertEquals(request.getRealEstateType(), Converter.convertStringListToEnumList(updatedPartner.getRealEstateType(), RealEstateType.class));
        assertEquals(request.getCreditPurposeType(), Converter.convertStringListToEnumList(updatedPartner.getCreditPurposeType(), CreditPurposeType.class));
        Set<UUID> actualCreditProgramUuids = updatedPartner.getCreditPrograms().stream()
                .map(CreditProgram::getId)
                .collect(Collectors.toSet());
        assertEquals(request.getBankCreditProgram(), actualCreditProgramUuids);
        assertEquals(request.getCianId(), updatedPartner.getCianId());
        List<RealEstate> activeRealEstates = updatedPartner.getRealEstates().stream()
                .filter(RealEstate::isActive)
                .collect(Collectors.toList());
        assertEquals(1, activeRealEstates.size());
    }

    @Test
    public void testUpdatePartnerContact() {
        PartnerRequest request = testData.createPartnerRequest();
        request.setContacts(testData.createPartnerContactRequest());
        Partner updatedPartner = partnerService.updatePartnerById(TestConstants.PARTNER_ID_1, request, 1234);
        assertPartnerContact(updatedPartner, testData.createPartnerContactRequest());
        request.setContacts(List.of(new PartnerContactRequest()
                .setName("Test Partner 1")
                .setEmail("test@mbroker1.com")
                .setTriggers(List.of(NotificationTrigger.BORROWER_SIGNED_FORM,
                        NotificationTrigger.UNDERWRITING_STATUS_CHANGED,
                        NotificationTrigger.BORROWER_SIGNED_FORM))));
        partnerService.updatePartnerById(TestConstants.PARTNER_ID_1, request, 1234);
        assertPartnerContact(updatedPartner, request.getContacts());
        List<PartnerContactRequest> partnerContactRequest = testData.createPartnerContactRequest();
        partnerContactRequest.add(new PartnerContactRequest()
                .setName("Test Partner 3 ")
                .setEmail("test@mbroker3.com")
                .setTriggers(List.of(NotificationTrigger.BORROWER_SIGNED_FORM)));
        request.setContacts(partnerContactRequest);
        partnerService.updatePartnerById(TestConstants.PARTNER_ID_1, request, 1234);
        assertPartnerContact(updatedPartner, request.getContacts());
    }


    @Test
    public void testGetPartnerById() {
        PartnerRequest request = testData.createPartnerRequest();
        Partner partner = partnerService.createPartner(request, 1234);
        Partner foundPartner = partnerService.getPartner(partner.getId());
        assertNotNull(foundPartner);
        assertEquals(partner.getId(), foundPartner.getId());
        assertEquals(partner.getName(), foundPartner.getName());
    }

    @Test
    public void testDeletePartner() {
        partnerService.deletePartner(UUID.fromString("5fec2326-d92e-11ed-afa1-0242ac120002"), 1234);
        List<Partner> allPartner = partnerService.getAllPartner(0, 10, "name", "asc");
        allPartner.stream()
                .noneMatch(partner ->
                        partner.getId().equals(UUID.fromString("5fec2326-d92e-11ed-afa1-0242ac120002")));
    }

    @Test
    public void testCreatePartnerWithContacts() {
        PartnerRequest request = testData.createPartnerRequest();
        List<PartnerContactRequest> expectedContacts = testData.createPartnerContactRequest();
        request.setContacts(expectedContacts);
        Partner partner = partnerService.createPartner(request, 1234);
        asserPartner(request, partner);
        assertEquals(request.getContacts().size(), 2);
        assertPartnerContact(partner, expectedContacts);
    }

    private static void assertPartnerContact(Partner partner, List<PartnerContactRequest> expectedContacts) {
        int partnerContactSize = (int) partner.getPartnerContacts().stream().filter(BaseEntity::isActive).count();
        assertEquals(partnerContactSize, expectedContacts.size());
        for (PartnerContactRequest expectedContact : expectedContacts) {
            boolean found = partner.getPartnerContacts().stream()
                    .anyMatch(contact ->
                            contact.getName().equals(expectedContact.getName()) &&
                                    contact.getEmail().equals(expectedContact.getEmail()) &&
                                    Converter.convertStringListToEnumList(contact.getTriggers(), NotificationTrigger.class).equals(expectedContact.getTriggers())
                    );
            assertTrue("Expected contact not found: " + expectedContact, found);
        }
    }

    private void asserPartner(PartnerRequest request, Partner partner) {
        assertNotNull(partner);
        assertNotNull(partner.getId());
        assertEquals(request.getSmartDealOrganizationId(), partner.getSmartDealOrganizationId());
        assertEquals(request.getName(), partner.getName());
        assertEquals(request.getType(), partner.getType());
        assertEquals(Converter.convertEnumListToString(request.getRealEstateType()), partner.getRealEstateType());
        assertEquals(Converter.convertEnumListToString(request.getCreditPurposeType()), partner.getCreditPurposeType());
        assertEquals(request.getBankCreditProgram().size(), partner.getCreditPrograms().size());
        assertEquals(request.getRealEstateRequest().size(), partner.getRealEstates().size());
    }
}