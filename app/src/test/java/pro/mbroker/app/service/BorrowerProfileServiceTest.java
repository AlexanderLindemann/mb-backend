package pro.mbroker.app.service;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.dto.request.BorrowerProfileRequest;
import pro.mbroker.api.dto.request.BorrowerRequest;
import pro.mbroker.api.dto.response.BorrowerProfileResponse;
import pro.mbroker.api.dto.response.BorrowerResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = "classpath:sql/test_data.sql")
@Sql(value = "classpath:sql/clear_all.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class BorrowerProfileServiceTest extends AbstractServiceTest {

    @Autowired
    private BorrowerProfileService borrowerProfileService;

    @Test
    public void testUpdateBorrowerProfile() {
        BorrowerRequest borrowerRequest = new BorrowerRequest()
                .setMainBorrower(getBorrowerProfileRequestList().get(0)
                        .setId(UUID.fromString("1348b508-f476-11ed-a05b-0242ac120003")))
                .setId(UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34"));

        BorrowerResponse orUpdateGenericBorrowerApplication = borrowerProfileService.createOrUpdateGenericBorrowerProfile(borrowerRequest);
        BorrowerProfileResponse mainBorrower = orUpdateGenericBorrowerApplication.getMainBorrower();
        assertThat(mainBorrower.getEmail(), Matchers.is("test@test.com"));
        assertThat(mainBorrower.getFirstName(), Matchers.is("TestFirstName"));
        assertThat(mainBorrower.getLastName(), Matchers.is("TestLastName"));
    }

    private List<BorrowerProfileRequest> getBorrowerProfileRequestList() {
        BorrowerProfileRequest borrowerProfileRequest1 = new BorrowerProfileRequest()
                .setEmail("test@test.com")
                .setFirstName("TestFirstName")
                .setLastName("TestLastName");
        BorrowerProfileRequest borrowerProfileRequest2 = new BorrowerProfileRequest()
                .setEmail("test2@test.com")
                .setFirstName("TestFirstName2")
                .setLastName("TestLastName2");
        return new ArrayList<>(List.of(borrowerProfileRequest1, borrowerProfileRequest2));
    }

}
