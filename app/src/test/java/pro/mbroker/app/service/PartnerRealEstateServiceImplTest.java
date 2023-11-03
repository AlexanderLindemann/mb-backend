package pro.mbroker.app.service;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pro.mbroker.api.dto.request.RealEstateRequest;
import pro.mbroker.api.enums.RegionType;
import pro.mbroker.app.entity.RealEstate;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static pro.mbroker.app.TestConstants.PARTNER_ID;
import static pro.mbroker.app.TestConstants.REAL_ESTATE_ID;

public class PartnerRealEstateServiceImplTest extends AbstractServiceTest {
    @Autowired
    private PartnerRealEstateService partnerRealEstateService;


    @Test
    public void testAddRealEstate() {
        RealEstateRequest request = getRealEstateRequest();

        RealEstate result = partnerRealEstateService.addRealEstate(PARTNER_ID, request);
        assertEquals(result.getAddress(), "Test Address");
        assertEquals(result.getRegion(), RegionType.MOSCOW);
        assertEquals(result.getResidentialComplexName(), "Test Complex Name");
    }

    @Test
    public void testUpdateRealEstate() {
        RealEstateRequest request = getRealEstateRequest();

        RealEstate result = partnerRealEstateService.updateRealEstate(REAL_ESTATE_ID, request);
        assertEquals(result.getAddress(), "Test Address");
        assertEquals(result.getRegion(), RegionType.MOSCOW);
        assertEquals(result.getResidentialComplexName(), "Test Complex Name");
    }

    @Test
    public void testGetRealEstateByPartnerId() {
        List<RealEstate> result = partnerRealEstateService.getRealEstateByPartnerId(0, 2, "residentialComplexName", "asc", PARTNER_ID);
        assertThat(result.size(), Matchers.is(2));
        assertEquals(result.get(0).getResidentialComplexName(), "testResidentialComplexName1");
        assertEquals(result.get(1).getResidentialComplexName(), "testResidentialComplexName2");
    }

    @Test
    public void testDeleteRealEstate() {
        partnerRealEstateService.deleteRealEstate(REAL_ESTATE_ID);
        List<RealEstate> result = partnerRealEstateService.getRealEstateByPartnerId(0, 10, "residentialComplexName", "asc", PARTNER_ID);
        assertThat(result.size(), Matchers.is(2));
        assertEquals(result.get(0).getResidentialComplexName(), "testResidentialComplexName2");
    }

    private RealEstateRequest getRealEstateRequest() {
        RealEstateRequest request = new RealEstateRequest();
        request.setResidentialComplexName("Test Complex Name");
        request.setRegion(RegionType.MOSCOW);
        request.setAddress("Test Address");
        return request;
    }
}
