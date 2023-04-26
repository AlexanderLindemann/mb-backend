package pro.mbroker.app.service;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.dto.request.RealEstateRequest;
import pro.mbroker.api.enums.RegionType;
import pro.mbroker.app.entity.RealEstate;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = "classpath:sql/test_data.sql")
@Sql(value = "classpath:sql/clear_all.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class PartnerRealEstateServiceImplTest {

    @Autowired
    private PartnerRealEstateService partnerRealEstateService;

    @Test
    public void testAddRealEstate() {
        UUID partnerId = UUID.fromString("5fec2326-d92e-11ed-afa1-0242ac120002");
        RealEstateRequest request = getRealEstateRequest();

        RealEstate result = partnerRealEstateService.addRealEstate(partnerId, request);
        assertEquals(result.getAddress(), "Test Address");
        assertEquals(result.getRegion(), RegionType.MOSCOW);
        assertEquals(result.getResidentialComplexName(), "Test Complex Name");
    }

    @Test
    public void testUpdateRealEstate() {
        UUID realEstateId = UUID.fromString("2b8850b2-d930-11ed-afa1-0242ac120002");
        RealEstateRequest request = getRealEstateRequest();

        RealEstate result = partnerRealEstateService.updateRealEstate(realEstateId, request);
        assertEquals(result.getAddress(), "Test Address");
        assertEquals(result.getRegion(), RegionType.MOSCOW);
        assertEquals(result.getResidentialComplexName(), "Test Complex Name");
    }

    @Test
    public void testGetRealEstateByPartnerId() {
        UUID partnerId = UUID.fromString("5fec2326-d92e-11ed-afa1-0242ac120002");

        List<RealEstate> result = partnerRealEstateService.getRealEstateByPartnerId(0, 2, "residentialComplexName", "asc", partnerId);
        assertThat(result.size(), Matchers.is(2));
        assertEquals(result.get(0).getResidentialComplexName(), "testResidentialComplexName1");
        assertEquals(result.get(1).getResidentialComplexName(), "testResidentialComplexName2");
    }

    private RealEstateRequest getRealEstateRequest() {
        RealEstateRequest request = new RealEstateRequest();
        request.setResidentialComplexName("Test Complex Name");
        request.setRegion(RegionType.MOSCOW);
        request.setAddress("Test Address");
        return request;
    }
}
