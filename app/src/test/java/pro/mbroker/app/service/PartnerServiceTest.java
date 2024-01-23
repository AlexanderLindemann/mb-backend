package pro.mbroker.app.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pro.mbroker.api.dto.request.PartnerRequest;
import pro.mbroker.api.dto.request.RealEstateRequest;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.PartnerType;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.api.enums.RegionType;
import pro.mbroker.app.entity.Partner;
import pro.mbroker.app.util.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PartnerServiceTest extends BaseServiceTest {
    @Autowired
    private PartnerService partnerService;

    @Test
    public void testCreatePartner() {
        PartnerRequest request = createPartnerRequest();
        Partner partner = partnerService.createPartner(request, 1234);
        asserPartner(request, partner);
    }

    @Test
    public void testGetPartnerById() {
        PartnerRequest request = createPartnerRequest();
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
        assertEquals(1, allPartner.size());
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

    private PartnerRequest createPartnerRequest() {
        PartnerRequest request = new PartnerRequest();
        request.setSmartDealOrganizationId(123456);
        request.setName("Test Partner");
        request.setType(PartnerType.DEVELOPER);
        request.setRealEstateType(List.of(RealEstateType.APARTMENT, RealEstateType.APARTMENT_COMPLEX));
        request.setCreditPurposeType(List.of(CreditPurposeType.PURCHASE_UNDER_CONSTRUCTION, CreditPurposeType.REFINANCING));
        request.setBankCreditProgram(List.of(UUID.fromString("bfda8d66-d926-11ed-afa1-0242ac120002"), UUID.fromString("8222cb80-d928-11ed-afa1-0242ac120002")));

        List<RealEstateRequest> realEstateAddresses = new ArrayList<>();
        RealEstateRequest realEstateAddressRequest = new RealEstateRequest();
        realEstateAddressRequest.setResidentialComplexName("Test Complex");
        realEstateAddressRequest.setRegion(RegionType.MOSCOW);
        realEstateAddressRequest.setAddress("test Address");
        realEstateAddresses.add(realEstateAddressRequest);
        request.setRealEstateRequest(realEstateAddresses);
        return request;
    }

}
