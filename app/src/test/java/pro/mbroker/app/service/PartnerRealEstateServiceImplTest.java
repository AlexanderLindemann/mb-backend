package pro.mbroker.app.service;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import pro.mbroker.api.dto.request.RealEstateRequest;
import pro.mbroker.api.enums.RegionType;
import pro.mbroker.app.entity.RealEstate;
import pro.mbroker.app.integration.cian.CianAPIClient;
import pro.mbroker.app.integration.cian.CiansRealEstate;
import pro.mbroker.app.integration.cian.response.BuilderDto;
import pro.mbroker.app.integration.cian.response.RealEstateCianResponse;
import pro.mbroker.app.integration.cian.response.RegionDto;
import pro.mbroker.app.repository.PartnerRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static pro.mbroker.app.TestConstants.PARTNER_ID_1;
import static pro.mbroker.app.TestConstants.REAL_ESTATE_ID;

public class PartnerRealEstateServiceImplTest extends AbstractServiceTest {
    @Autowired
    private PartnerRealEstateService partnerRealEstateService;

    @Autowired
    private PartnerRepository partnerRepository;
    @MockBean
    private CianAPIClient cianAPIClient;

    @Test
    public void testAddRealEstate() {
        RealEstateRequest request = getRealEstateRequest();

        RealEstate result = partnerRealEstateService.addRealEstate(PARTNER_ID_1, request);
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
        List<RealEstate> result = partnerRealEstateService.getRealEstateByPartnerId(0, 2, "residentialComplexName", "asc", PARTNER_ID_1);
        assertThat(result.size(), Matchers.is(2));
        assertEquals(result.get(0).getResidentialComplexName(), "testResidentialComplexName1");
        assertEquals(result.get(1).getResidentialComplexName(), "testResidentialComplexName2");
    }

    @Test
    public void testDeleteRealEstate() {
        partnerRealEstateService.deleteRealEstate(REAL_ESTATE_ID);
        List<RealEstate> result = partnerRealEstateService.getRealEstateByPartnerId(0, 10, "residentialComplexName", "asc", PARTNER_ID_1);
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

    @Test
    public void testGetRealEstatesCian() {
        RealEstateCianResponse response = new RealEstateCianResponse();

        CiansRealEstate realEstate = new CiansRealEstate();
        RegionDto regionDto = new RegionDto();
        regionDto.setName("Москва");
        regionDto.setId(123);

        BuilderDto builderDto = new BuilderDto();
        builderDto.setId(4444);
        builderDto.setName("Застройщик года");

        realEstate.setRegion(regionDto);
        realEstate.setName("ЖК Самолет");
        realEstate.setFullAddress("Москва ул.Строителей 9");
        realEstate.setId(3456);
        realEstate.setBuilders(List.of(builderDto));

        List<CiansRealEstate> list = List.of(realEstate);

        response.setNewBuildings(list);

        when(cianAPIClient.getRealEstate()).thenReturn(response);

        partnerRealEstateService.loadRealEstatesFromCian();
        var result = partnerRepository.findByCianId(realEstate.getBuilders().get(0).getId());
        assertEquals(result.get().getName(), realEstate.getBuilders().get(0).getName());
    }
}
