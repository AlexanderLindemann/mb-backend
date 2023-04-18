package pro.mbroker.app.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
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

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = "classpath:sql/test_data.sql")
@Sql(value = "classpath:sql/clear_all.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class PartnerServiceTest {

    @Autowired
    private PartnerService partnerService;

    @Test
    public void testCreatePartner() {
        PartnerRequest request = createPartnerRequest();
        Partner partner = partnerService.createPartner(request);
        asserPartner(request, partner);
    }

    @Test
    public void testGetPartnerById() {
        PartnerRequest request = createPartnerRequest();
        Partner partner = partnerService.createPartner(request);
        Partner foundPartner = partnerService.getPartner(partner.getId());
        assertNotNull(foundPartner);
        assertEquals(partner.getId(), foundPartner.getId());
        assertEquals(partner.getName(), foundPartner.getName());
    }

    @Test
    public void testGetAllPartner() {
        PartnerRequest request = createPartnerRequest();
        Partner partner = partnerService.createPartner(request);
        List<Partner> allPartner = partnerService.getAllPartner(0, 10, "name", "asc");
        assertNotNull(allPartner);
        assertEquals(2, allPartner.size());
        assertEquals(request.getName(), allPartner.get(0).getName());
        assertEquals("testName", allPartner.get(1).getName());
        asserPartner(request, partner);
    }

    private void asserPartner(PartnerRequest request, Partner partner) {
        assertNotNull(partner);
        assertNotNull(partner.getId());
        assertEquals(request.getSmartDealOrganizationId(), partner.getSmartDealOrganizationId());
        assertEquals(request.getName(), partner.getName());
        assertEquals(request.getType(), partner.getType());
        assertEquals(Converter.convertEnumListToStringList(request.getRealEstateType()), partner.getRealEstateType());
        assertEquals(Converter.convertEnumListToStringList(request.getCreditPurposeType()), partner.getCreditPurposeType());
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
